package com.codeandme.scripting.modules;

import com.codeandme.scripting.IScriptEngine;

public interface IScriptModule {

    String getModuleName();

    IScriptEngine getScriptEngine();
}
