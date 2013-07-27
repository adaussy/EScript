/*******************************************************************************
 * Copyright (c) 2012 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author$
 *     Date:           $Date$
 *     Revision:       $Revision$
 *     Head URL:       $URL$
 *******************************************************************************/

package com.codeandme.scripting.javascript.modules;

import java.util.List;

/**
 * COMMENT add type comment.
 * 
 */
public interface IModuleProvider {

	AbstractJavaScriptModule findModule(final String name);

	List<AbstractJavaScriptModule> getLoadedModules();

	/**
	 * List all available (visible) modules. Returns a list of visible modules. Loaded modules are indicated.
	 * 
	 * @return string containing module information
	 */
	String listModules();

	/**
	 * Load a module. Loading a module generally enhances the JavaScript environment with new functions and variables.
	 * If a module was already loaded before, it gets refreshed and moved to the top of the module stack. When a module
	 * is loaded, all its dependencies are loaded too. So loading one module might change the whole module stack.
	 * 
	 * @param name
	 *            name of module to load
	 * @return loaded module instance
	 */
	AbstractJavaScriptModule loadModule(final String moduleIdentifier);
}