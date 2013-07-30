package com.codeandme.scripting;

import java.util.Collection;

import com.codeandme.scripting.modules.IModuleWrapper;

public interface IScriptService {

    IScriptEngine createEngine(String scriptType);

    IScriptEngine createEngineByID(String engineID);

    Collection<EngineDescription> getEngines();

    Collection<IModuleWrapper> getModuleWrappers(String engineID);
}
