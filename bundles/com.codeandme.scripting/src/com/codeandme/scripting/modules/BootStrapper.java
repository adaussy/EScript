package com.codeandme.scripting.modules;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineLaunchExtension;
import com.codeandme.scripting.service.ScriptService;

/**
 * The RhinoEnvironment provides base functions for all JavaScript interpreters. It is automatically loaded by any interpreter upon startup.
 */
public class BootStrapper implements IScriptEngineLaunchExtension {

    @Override
    public void createEngine(final IScriptEngine engine) {
        IModuleWrapper wrapper = getWrapper(engine.getID());
        if (wrapper != null) {
            String code = wrapper.classInstantiation(EnvironmentModule.class, new String[0]);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(code);
            stringBuilder.append(".loadModule(\"");
            stringBuilder.append(EnvironmentModule.ENVIRONMENT_MODULE_NAME);
            stringBuilder.append("\");");

            engine.executeAsync(stringBuilder.toString());
        }
    }

    public static IModuleWrapper getWrapper(final String engineID) {
        return ScriptService.getInstance().getModuleWrapper(engineID);
    }
}
