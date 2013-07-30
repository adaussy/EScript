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
package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import com.codeandme.scripting.ui.IMacroService;

/**
 * Delete macro command. Deletes a given macro.
 */
public class Delete extends AbstractHandler implements IHandler {

    public static final String COMMAND_ID = "com.codeandme.commands.macro.delete";
    public static final String PARAMETER_NAME = "com.codeandme.commands.macro.delete.name";

    @Override
    public final Object execute(final ExecutionEvent event) throws ExecutionException {

        IMacroService macroService = (IMacroService) PlatformUI.getWorkbench().getService(IMacroService.class);

        if (macroService != null) {
            final String[] macros = event.getParameter(PARAMETER_NAME).split(";");

            for (final String macroID : macros)
                macroService.removeMacro(macroID);
        }

        return null;
    }
}
