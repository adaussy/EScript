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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import com.codeandme.scripting.IExecutionListener;
import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineProvider;
import com.codeandme.scripting.Script;

public class ScriptConsole extends IOConsole implements IExecutionListener, IScriptEngineProvider {

    public static final String CONSOLE_ACTIVE = "ACTIVE";

    public static ScriptConsole create(final String title, final IScriptEngine engine) {
        final ScriptConsole console = new ScriptConsole(title, engine);

        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);

        return console;
    }

    public static ScriptConsole create(final IScriptEngine engine) {
        return create(engine.getName(), engine);
    }

    private PrintStream mOutputStream = null;
    private PrintStream mErrorStream = null;
    private IScriptEngine mEngine;
    private ILaunch mLaunch = null;

    private ScriptConsole(final String name, final IScriptEngine engine) {
        this(name, getConsoleType(), null, engine);
    }

    private ScriptConsole(final String name, final String consoleType, final ImageDescriptor imageDescriptor, final IScriptEngine engine) {
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
            IOConsoleOutputStream stream = newOutputStream();
            // TODO set error stream output color
            mErrorStream = new PrintStream(stream);
        }

        return mErrorStream;
    }

    public PrintStream getOutputStream() {
        if (mOutputStream == null)
            mOutputStream = new PrintStream(newOutputStream());

        return mOutputStream;
    }

    // public void stopScriptExecution() {
    // if (mEngine != null)
    // mEngine.terminate();
    // }

    @Override
    protected void dispose() {
        if (mEngine != null)
            mEngine.removeExecutionListener(this);

        // firePropertyChange(this, CONSOLE_ACTIVE, true, false);

        super.dispose();
    }

    @Override
    public void notify(final IScriptEngine engine, final Script script, final int status) {
        switch (status) {
            case ENGINE_END:
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        setName(getName() + " [terminated]");
                    }
                });

                // remove engine once terminated
                engine.removeExecutionListener(this);
                mEngine = null;
                break;
        }
    }

    @Override
    public IScriptEngine getScriptEngine() {
        return mEngine;
    }

    public void setLaunch(final ILaunch launch) {
        mLaunch = launch;
    }

    public ILaunch getLaunch() {
        return mLaunch;
    }
}
