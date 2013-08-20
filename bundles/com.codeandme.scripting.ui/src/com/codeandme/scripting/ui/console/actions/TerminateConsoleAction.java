/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codeandme.scripting.ui.console.actions;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IUpdate;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.ui.console.ScriptConsole;

/**
 * ConsoleTerminateAction
 */
public class TerminateConsoleAction extends Action implements IUpdate {

    private ScriptConsole mConsole;

    /**
     * Creates a terminate action for the console
     * 
     * @param window
     *            the window
     * @param console
     *            the console
     */
    public TerminateConsoleAction(final IWorkbenchWindow window, final ScriptConsole console) {
        super(ConsoleMessages.ConsoleTerminateAction_0);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.CONSOLE_TERMINATE_ACTION);
        mConsole = console;
        setToolTipText(ConsoleMessages.ConsoleTerminateAction_1);
        setImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_LCL_TERMINATE));
        setDisabledImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_DLCL_TERMINATE));
        setHoverImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_LCL_TERMINATE));
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.CONSOLE_TERMINATE_ACTION);
        update();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.IUpdate#update()
     */
    @Override
    public void update() {
        IScriptEngine engine = mConsole.getScriptEngine();
        setEnabled(engine != null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        IScriptEngine engine = mConsole.getScriptEngine();
        if (engine != null) {

            if (engine.getTerminateOnIdle())
                // terminate completely
                engine.terminate();

            else
                // terminate current piece of code
                engine.terminateCurrent();
        }
    }

    public void dispose() {
        mConsole = null;
    }
}
