package com.codeandme.scripting.modules;

import org.eclipse.ui.PlatformUI;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineLaunchExtension;
import com.codeandme.scripting.IScriptService;

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
            stringBuilder.append(EnvironmentModule.NAME);
            stringBuilder.append("\");");

            engine.executeAsync(stringBuilder.toString());
        }
    }

    public static IModuleWrapper getWrapper(final String engineID) {
        IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
        if (scriptService != null)
            return scriptService.getModuleWrapper(engineID);

        return null;
    }
}
