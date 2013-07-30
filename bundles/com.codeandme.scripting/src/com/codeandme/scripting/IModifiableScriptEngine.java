package com.codeandme.scripting;

public interface IModifiableScriptEngine extends IScriptEngine {

    /**
     * Set a variable in the script engine. This variable will be stored in the global script scope
     * 
     * @param name
     *            variable name
     * @param content
     *            variable content
     */
    void setVariable(final String name, final Object content);

    /**
     * Get a script variable. Retrieve a variable from the global script scope.
     * 
     * @param name
     *            variable name
     * 
     * @return variable content or <code>null</code>
     */
    Object getVariable(final String name);
}
