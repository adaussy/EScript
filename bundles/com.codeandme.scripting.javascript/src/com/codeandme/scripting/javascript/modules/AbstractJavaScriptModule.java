package com.codeandme.scripting.javascript.modules;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.javascript.JavaScriptTools;
import com.codeandme.scripting.javascript.rhino.modules.EnvironmentModule;

/**
 * Abstract base class for IJavaScriptModule implementations. Provides a quite useful default implementation for initialize(). All methods annotated with @WrapToJavaScript
 * are automatically wrapped with a JavaScript function. Additionally a default help item is created. <br>
 * <b>Attention:</b> Overloaded methods will result in 2 JavaScript functions with the same name, which cannot exist simultaneously. This might result in
 * unexpected behavior! When implementing a method with an optional parameter this should be done the following way: <code>
 * "at"WrapToJavaScript
 * public String foo(int parameter);
 * 
 * public String foo(String parameter);
 * </code> Then foo(int) will be called whenever the parameter is set. When no parameter is provided foo(String) will be called and the parameter is set to
 * IJavaScriptModule.UNDEFINED_TOPIC <br>
 * On a refresh all wrappers will be regenerated as they might have been overwritten by other modules.
 */
public abstract class AbstractJavaScriptModule {

    public static final String UNDEFINED = "undefined";
    public static final String FALSE = "false";

    public static final String INTERNAL_VARIABLE_PREFIX = "__VAR_";

    public static final String MODULE_PREFIX = "__MOD_";

    /** Script engine. */
    private IScriptEngine mEngine;

    /** module name. */
    private String mModuleName;

    private EnvironmentModule mEnvironment;

    public void initialize(final String name, final IScriptEngine engine, final EnvironmentModule environment) {
        mModuleName = name;
        mEngine = engine;
        mEnvironment = environment;
    }

    public final String getModuleName() {
        return mModuleName;
    }

    /**
     * Get the registered script engine.
     * 
     * @return registered script engine
     */
    public final IScriptEngine getScriptEngine() {
        return mEngine;
    }

    protected final EnvironmentModule getEnvironment() {
        return mEnvironment;
    }

    public String getVariableName() {
        return MODULE_PREFIX + JavaScriptTools.getSaveName(getModuleName());
    }

    @Override
    public String toString() {
        return mModuleName + " Module";
    }
}
