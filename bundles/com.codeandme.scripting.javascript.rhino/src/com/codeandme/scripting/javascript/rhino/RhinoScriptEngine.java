/*******************************************************************************
 * Copyright (c) 2013 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/
package com.codeandme.scripting.javascript.rhino;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.debug.Debugger;

import com.codeandme.scripting.AbstractScriptEngine;
import com.codeandme.scripting.FileTrace;
import com.codeandme.scripting.IDebugEngine;
import com.codeandme.scripting.javascript.rhino.debugger.LineNumberDebugger;
import com.codeandme.scripting.javascript.rhino.debugger.LineNumberDebugger.LineNumberDebugFrame;

/**
 * A script engine to execute JavaScript code on a Rhino interpreter.
 */
public class RhinoScriptEngine extends AbstractScriptEngine implements IDebugEngine {

    static {
        // set context factory that is able to terminate script execution
        ContextFactory.initGlobal(new ObservingContextFactory());

        // set a custom class loader to find everything in the eclipse universe
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                ContextFactory.getGlobal().initApplicationClassLoader(new RhinoClassLoader());
                return null;
            }
        });
    }

    /** Rhino Scope. Created when interpreter is initialized */
    protected ScriptableObject mScope;

    private Context mContext;

    private int mOptimizationLevel = 9;

    private boolean mCreateLineNumberInformation = false;

    private final Map<String, Object> mBufferedVariables = new HashMap<String, Object>();

    /**
     * Creates a new Rhino interpreter.
     */
    public RhinoScriptEngine() {
        super("Rhino");
    }

    /**
     * Creates a new Rhino interpreter.
     * 
     * @param name
     *            name of interpreter (used for the jobs name)
     */
    protected RhinoScriptEngine(final String name) {
        super(name);
    }

    public void setOptimizationLevel(final int level) {
        mOptimizationLevel = level;
    }

    @Override
    protected synchronized boolean setupEngine() {
        mContext = getContext();

        if (mCreateLineNumberInformation) {
            // when we want to report line numbers on stack traces we need to add a debugger to our engine
            mContext.setGeneratingDebug(true);
            mContext.setOptimizationLevel(-1);
            mContext.setDebugger(new LineNumberDebugger(), null);

        } else {
            mContext.setGeneratingDebug(false);
            mContext.setOptimizationLevel(mOptimizationLevel);
            mContext.setDebugger(null, null);
        }

        mScope = mContext.initStandardObjects();

        // scope is initialized, set buffered variables
        for (String name : mBufferedVariables.keySet())
            setVariable(name, mBufferedVariables.get(name));

        // enable script termination support
        mContext.setGenerateObserverCount(true);
        mContext.setInstructionObserverThreshold(10);

        return true;
    }

    @Override
    protected synchronized boolean teardownEngine() {
        // cleanup context
        Context.exit();

        // unregister from classloader
        RhinoClassLoader.unregisterEngine(this);

        return true;
    }

    @Override
    protected Object execute(final InputStream code, final Object reference, final String fileName) throws Exception {
        // remove an eventually cached terminate request
        ((ObservingContextFactory) ContextFactory.getGlobal()).cancelTerminate(mContext);

        try {
            final Object result = getContext().evaluateReader(mScope, new InputStreamReader(code), fileName, 1, null);

            if (result instanceof Undefined)
                return null;

            else if (result instanceof NativeJavaObject)
                return ((NativeJavaObject) result).unwrap();

            return result;

        } catch (final WrappedException e) {
            final Throwable wrapped = e.getWrappedException();
            if (wrapped instanceof Exception)
                throw ((Exception) wrapped);

            e.printStackTrace();
        }

        return null;
    }

    public Context getContext() {
        Context context = Context.getCurrentContext();
        if (context == null) {
            synchronized (ContextFactory.getGlobal()) {
                context = Context.enter();
            }
        }

        return context;
    }

    @Override
    public void terminateCurrent() {
        ((ObservingContextFactory) ContextFactory.getGlobal()).terminate(mContext);
    }

    public Scriptable getScope() {
        return mScope;
    }

    public synchronized void registerJar(final URL url) {
        RhinoClassLoader.registerURL(this, url);
    }

    @Override
    public synchronized void reset() {
        RhinoClassLoader.unregisterEngine(this);

        super.reset();

        setupEngine();
    }

    @Override
    public void setVariable(final String name, final Object content) {
        if (!RhinoModuleWrapper.isSaveName(name))
            throw new RuntimeException("\"" + name + "\" is not a valid JavaScript variable name");

        final Scriptable scope = getScope();
        if (scope != null) {

            final Object jsOut = javaToJS(content, scope);
            scope.put(name, scope, jsOut);
        } else
            mBufferedVariables.put(name, content);
    }

    @Override
    public Object getVariable(final String name) {
        if (getScope() != null) {
            final Object value = getScope().get(name, getScope());
            if (value instanceof NativeJavaObject)
                return ((NativeJavaObject) value).unwrap();

            else if ((value != null) && (value.getClass().getName().startsWith("org.mozilla.javascript")))
                return null;

            return value;
        }

        throw new RuntimeException("Cannot retrieve variable, Scope not initialized");
    }

    @Override
    public Object removeVariable(final String name) {
        if (getScope() != null) {
            final Object result = getVariable(name);
            getScope().delete(name);

            return result;
        }

        // scope not initialized, no variables exist yet
        return null;
    }

    @Override
    public Map<String, Object> getVariables() {
        if (getScope() != null) {
            Map<String, Object> result = new HashMap<String, Object>();

            for (Object key : getScope().getIds()) {
                if (key instanceof String)
                    result.put((String) key, getVariable((String) key));
            }

            return result;
        }

        throw new RuntimeException("Cannot retrieve variable, Scope not initialized");
    }

    /**
     * Method copied from {@link Context}.{@link #javaToJS(Object, Scriptable)} and slightly adapted to use associated context.
     */
    private Object javaToJS(final Object value, final Scriptable scope) {
        if ((value instanceof String) || (value instanceof Number) || (value instanceof Boolean) || (value instanceof Scriptable)) {
            return value;
        } else if (value instanceof Character) {
            return String.valueOf(((Character) value).charValue());
        } else {
            return getContext().getWrapFactory().wrap(getContext(), scope, value, null);
        }
    }

    public void setCreateLineNumberInformation(final boolean createLineNumberInformation) {
        mCreateLineNumberInformation = createLineNumberInformation;
    }

    /**
     * Get the current file trace. This trace is created dynamically.
     */
    @Override
    public FileTrace getFileTrace() {

        // might request the trace from a different thread, so do not use getContext() here
        Debugger debugger = mContext.getDebugger();
        if (debugger instanceof LineNumberDebugger) {
            final FileTrace trace = new FileTrace();

            List<LineNumberDebugFrame> frames = ((LineNumberDebugger) debugger).getFrames();
            for (LineNumberDebugFrame frame : frames) {
                trace.push(frame.getScript().getSourceName(), frame.getLineNumber(), null);
            }

            return trace;
        }

        return super.getFileTrace();
    }
}
