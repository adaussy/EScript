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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(wrapper.classInstantiation(EnvironmentModule.class, new String[0]));
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
