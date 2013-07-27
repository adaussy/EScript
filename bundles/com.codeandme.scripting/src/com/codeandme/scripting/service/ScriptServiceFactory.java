package com.codeandme.scripting.service;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import com.codeandme.scripting.IScriptService;

public class ScriptServiceFactory extends AbstractServiceFactory {

    public ScriptServiceFactory() {
    }

    @Override
    public Object create(final Class serviceInterface, final IServiceLocator parentLocator, final IServiceLocator locator) {
        if (serviceInterface.equals(IScriptService.class))
            return ScriptService.getInstance();

        return null;
    }
}