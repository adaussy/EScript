/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2012-11-07 09:37:30 +0100 (Mi, 07 Nov 2012) $
 *     Revision:       $Revision: 1943 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/commands/Edit.java $
 *******************************************************************************/
package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import com.codeandme.scripting.ui.IMacroService;
import com.codeandme.scripting.ui.macro.Macro;

/**
 * Edit macro command. Triggers editing of a given macro.
 */
public class Edit extends AbstractHandler implements IHandler {

    public static final String COMMAND_ID = "com.codeandme.commands.macro.edit";
    public static final String PARAMETER_NAME = "com.codeandme.commands.macro.edit.name";

    @Override
    public final Object execute(final ExecutionEvent event) throws ExecutionException {

        IMacroService macroService = (IMacroService) PlatformUI.getWorkbench().getService(IMacroService.class);

        if (macroService != null) {
            final String[] macros = event.getParameter(PARAMETER_NAME).split(";");

            for (final String macroID : macros) {
                final Macro macro = macroService.getMacro(macroID);
                if (macro != null)
                    macro.edit();
            }
        }

        return null;
    }
}
