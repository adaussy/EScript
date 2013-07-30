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
package com.codeandme.scripting.javascript.rhino.debugger;

import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.ToolErrorReporter;

import com.codeandme.scripting.javascript.rhino.RhinoScriptEngine;

public class RhinoScriptDebugEngine extends RhinoScriptEngine {

    private IContextConsumer mConsumer;
    private InfineonDebuggerImpl mDebugger;

    public RhinoScriptDebugEngine() {
        super("Rhino debugger");
    }

    @Override
    protected synchronized boolean setupEngine() {

        super.setupEngine();

        Context context = getContext();

        context.setOptimizationLevel(-1);
        context.setGeneratingDebug(true);
        context.setGeneratingSource(true);

        if (mConsumer != null) {
            mConsumer.setContext(context);
            // consumer only needed once
            mConsumer = null;
        }

        context.setErrorReporter(new ToolErrorReporter(true));

        if (mDebugger == null) {
            LineNumberDebugger debugger = new LineNumberDebugger();
            context.setDebugger(debugger, null);
        }

        else {
            context.setDebugger(mDebugger, null);
            mDebugger.contextCreated(context);
        }

        return true;
    }

    @Override
    protected synchronized boolean teardownEngine() {
        if (mDebugger != null) {
            mDebugger.stop();
            mDebugger = null;
        }

        // gracefully close I/O streams
        try {
            if ((getInputStream() != null) && (!System.in.equals(getInputStream())))
                getInputStream().close();
        } catch (IOException e) {
        }
        try {
            if ((getOutputStream() != null) && (!System.out.equals(getOutputStream())))
                getOutputStream().close();
        } catch (Exception e) {
        }
        try {
            if ((getErrorStream() != null) && (!System.err.equals(getErrorStream())))
                getErrorStream().close();
        } catch (Exception e) {
        }

        return super.teardownEngine();
    }

    public void setContextConsumer(final IContextConsumer consumer) {
        mConsumer = consumer;
    }

    public void setDebugger(final InfineonDebuggerImpl debugger) {
        mDebugger = debugger;
    }
}
