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
package com.codeandme.scripting.javascript.rhino.ui.launching;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.PlatformUI;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptService;
import com.codeandme.scripting.ui.launching.AbstractLaunchDelegate;

/**
 * Quick launcher for JavaScript files.
 */
public class Launcher extends AbstractLaunchDelegate {

    @Override
    protected IScriptEngine getScriptEngine(final ILaunchConfiguration configuration, final String mode) {
        IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
        if (MODE_RUN.equals(mode))
            return scriptService.createEngineByID("com.codeandme.scripting.javascript.rhino");

        else if (MODE_DEBUG.equals(mode))
            return scriptService.createEngineByID("com.codeandme.scripting.javascript.rhino.debug");

        return null;
    }

    @Override
    protected String getLaunchConfigurationType() {
        return "com.codeandme.launchConfigurationType.rhino";
    }
}
