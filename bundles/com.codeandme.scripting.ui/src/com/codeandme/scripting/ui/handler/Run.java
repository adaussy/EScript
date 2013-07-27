/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2012-08-16 09:21:41 +0200 (Do, 16 Aug 2012) $
 *     Revision:       $Revision: 1515 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/commands/Run.java $
 *******************************************************************************/
package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineProvider;

/**
 * Run macro command. Invoke a given macro.
 */
public class Run extends AbstractHandler implements IHandler {

    public static final String COMMAND_ID = "com.codeandme.commands.macro.run";
    public static final String PARAMETER_NAME = "com.codeandme.commands.macro.run.name";

    @Override
    public final Object execute(final ExecutionEvent event) throws ExecutionException {

        final IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof IScriptEngineProvider) {
            final IScriptEngine scriptEngine = ((IScriptEngineProvider) part).getScriptEngine();
            if (scriptEngine != null) {
                final String[] macros = event.getParameter(PARAMETER_NAME).split(";");

                for (final String macroID : macros)
                    scriptEngine.executeAsync("include(\"macro://" + macroID + "\");");
            }
        }

        return null;
    }
}
