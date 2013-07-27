package com.codeandme.scripting.ui.service;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import com.codeandme.scripting.ui.IMacroService;

public class MacroServiceFactory extends AbstractServiceFactory {

    public MacroServiceFactory() {
    }

    @Override
    public Object create(final Class serviceInterface, final IServiceLocator parentLocator, final IServiceLocator locator) {
        if (serviceInterface.equals(IMacroService.class))
            return MacroService.getInstance();

        return null;
    }
}