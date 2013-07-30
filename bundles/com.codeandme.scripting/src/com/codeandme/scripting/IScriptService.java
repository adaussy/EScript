package com.codeandme.scripting;

import java.util.Collection;
import java.util.Map;

import com.codeandme.scripting.modules.IModuleWrapper;
import com.codeandme.scripting.modules.ModuleDefinition;

public interface IScriptService {

    IScriptEngine createEngine(String scriptType);

    IScriptEngine createEngineByID(String engineID);

    Collection<EngineDescription> getEngines();

    IModuleWrapper getModuleWrapper(String engineID);

    Map<String, ModuleDefinition> getAvailableModules();
}
