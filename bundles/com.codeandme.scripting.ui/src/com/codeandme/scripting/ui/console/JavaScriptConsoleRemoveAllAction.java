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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.codeandme.scripting.ui.Activator;

/**
 * closes all the JavaScript consoles
 */
public class JavaScriptConsoleRemoveAllAction extends Action {

    public JavaScriptConsoleRemoveAllAction() {
        super("Close all JavaScript Consoles");
        setToolTipText("Close all JavaScript Consoles");
        setImageDescriptor(Activator.getImageDescriptor("images\\close_all.gif"));
    }

    @Override
    public synchronized void run() {
        IConsole[] consoles = JavaScriptConsoleFactory.getAllJavaScriptConsoles().toArray(new IConsole[0]);
        if ((consoles != null) && (consoles.length > 0)) {
            IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
            manager.removeConsoles(consoles);
        }
    }
}