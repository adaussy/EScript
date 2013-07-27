package com.codeandme.scripting.ui.preferences;

/**
 * Constant definitions for plug-in preferences.
 */
public final class PreferenceConstants {

	public static final String VALUE_OUTPUT_CONSOLE = "Console";
	public static final String VALUE_OUTPUT_SHELL = "Shell";
	public static final String VALUE_OUTPUT_NONE = "None";

	public static final String TARGET_STDOUT = "com.infineon.javascript.shell.prefs.stdout";
	public static final String TARGET_RESULT = "com.infineon.javascript.shell.prefs.result";
	public static final String TARGET_ERRORS = "com.infineon.javascript.shell.prefs.errors";

	public static final String INIT_COMMANDS = "com.infineon.javascript.shell.prefs.initCommands";

	/**
	 * Hidden constructor. Never used.
	 */
	@Deprecated
	private PreferenceConstants() {
	}
}
