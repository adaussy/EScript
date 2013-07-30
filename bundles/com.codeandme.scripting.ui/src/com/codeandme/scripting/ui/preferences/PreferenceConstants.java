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
package com.codeandme.scripting.ui.preferences;

/**
 * Constant definitions for plug-in preferences.
 */
public final class PreferenceConstants {

	public static final String VALUE_OUTPUT_CONSOLE = "Console";
	public static final String VALUE_OUTPUT_SHELL = "Shell";
	public static final String VALUE_OUTPUT_NONE = "None";

	public static final String SHELL_BASE = "com.codeandme.scripting.shell.prefs";
	public static final String TARGET_STDOUT = "com.codeandme.scripting.shell.prefs.stdout";
	public static final String TARGET_RESULT = "com.codeandme.scripting.shell.prefs.result";
	public static final String TARGET_ERRORS = "com.codeandme.scripting.shell.prefs.errors";

	public static final String INIT_COMMANDS = "com.codeandme.scripting.shell.prefs.initCommands";

	/**
	 * Hidden constructor. Never used.
	 */
	@Deprecated
	private PreferenceConstants() {
	}
}
