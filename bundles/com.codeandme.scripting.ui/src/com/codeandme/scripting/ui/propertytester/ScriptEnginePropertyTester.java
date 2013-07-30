package com.codeandme.scripting.ui.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import com.codeandme.scripting.IScriptEngineProvider;

public class ScriptEnginePropertyTester extends PropertyTester {

    private static final String PROPERTY_ENGINE_ID = "engineID";

    public ScriptEnginePropertyTester() {
    }

    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        if (receiver instanceof IScriptEngineProvider) {
            if (PROPERTY_ENGINE_ID.equals(property))
                return ((IScriptEngineProvider) receiver).getScriptEngine().getID().equals(expectedValue);
        }

        return false;
    }
}
