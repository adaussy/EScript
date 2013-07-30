package com.codeandme.scripting;

import java.util.Map;

/**
 * Interface to be implemented by a script debug engine
 */
public interface IDebugEngine extends IModifiableScriptEngine {

    Object removeVariable(final String name);

    Map<String, Object> getVariables();
}
