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
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/IMacroListener.java $
 *******************************************************************************/
package com.codeandme.scripting.ui.macro;

/**
 * Interface for Macro listeners. Listeners will be informed of macro addition
 * and removals.
 */
public interface IMacroListener {

	/** Add macro event type. */
	int ADD_MACRO = 1;

	/** Delete macro event type. */
	int DELETE_MACRO = 2;

	/**
	 * Notify of Macro event.
	 * 
	 * @param event
	 *            event happened
	 * @param macro
	 *            macro affected by event
	 */
	void notify(final int event, final Macro macro);
}
