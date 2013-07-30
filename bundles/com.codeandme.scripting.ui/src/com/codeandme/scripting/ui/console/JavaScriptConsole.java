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
package com.codeandme.scripting.ui.console;

import java.io.PrintStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IOConsole;

import com.codeandme.scripting.IExecutionListener;
import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.Script;

public class JavaScriptConsole extends IOConsole implements IExecutionListener {

    public static final String CONSOLE_ACTIVE = "ACTIVE";
    private PrintStream mOutputStream = null;
    private PrintStream mErrorStream = null;
    private IScriptEngine mEngine;

    public JavaScriptConsole(final String name, final IScriptEngine engine) {
        this(name, getConsoleType(), null, engine);
    }

    public JavaScriptConsole(final String name, final String consoleType, final ImageDescriptor imageDescriptor, final IScriptEngine engine) {
        super(name, consoleType, imageDescriptor, true);

        mEngine = engine;
        if (mEngine != null)
            mEngine.addExecutionListener(this);
    }

    public static String getConsoleType() {
        return "Text console type";
    }

    public PrintStream getErrorStream() {
        if (mErrorStream == null) {
            mErrorStream = new PrintStream(newOutputStream());
        }

        return mErrorStream;
    }

    public PrintStream getOutputStream() {
        if (mOutputStream == null) {
            mOutputStream = new PrintStream(newOutputStream());
        }

        return mOutputStream;
    }

    public void stopScriptExecution() {
        if (mEngine != null)
            mEngine.terminate();
    }

    /**
     * @param engine
     */
    public void setEngine(final IScriptEngine engine) {
        mEngine = engine;
    }

    @Override
    protected void dispose() {
        firePropertyChange(this, CONSOLE_ACTIVE, true, false);

        super.dispose();
    }

    @Override
    public void notify(final IScriptEngine engine, final Script script, final int status) {
        switch (status) {
            case ENGINE_END:
                // remove engine once terminated
                mEngine.removeExecutionListener(this);
                setEngine(null);
                break;
        }
    }
}
