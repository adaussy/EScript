package com.codeandme.scripting.modules;

import com.codeandme.scripting.IScriptEngine;

public interface IScriptModule {

    void initialize(final String moduleName, final IScriptEngine engine, final EnvironmentModule module);

    String getModuleName();

    IScriptEngine getScriptEngine();
}
