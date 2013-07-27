/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-05-09 13:21:28 +0200 (Mo, 09 Mai 2011) $
 *     Revision:       $Revision: 279 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/commands/Rename.java $
 *******************************************************************************/
package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class Rename extends AbstractHandler implements IHandler {

	public static final String COMMAND_ID = "com.codeandme.commands.macro.rename";
	public static final String PARAMETER_NAME = "com.codeandme.commands.macro.rename.name";

	@Override
	public final Object execute(final ExecutionEvent event) throws ExecutionException {

		final String[] macros = event.getParameter(PARAMETER_NAME).split(";");

		for (final String macroID : macros) {
			// TODO implement dialog
			// final Macro macro = MacroManager.getDefault().getMacro(macroID);
			// MacroManager.getDefault().removeMacro(macro);
		}

		return null;
	}
}
