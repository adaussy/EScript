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
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/IMacroSupport.java $
 *******************************************************************************/
package com.codeandme.scripting.ui.macro;

/**
 * Interface for components integrating macro support. Components implementing this interface will be informed of macro
 * events.
 */
public interface IMacroSupport {

	/**
	 * Toggle display status of macro manager.
	 */
	void toggleMacroManager();
}
