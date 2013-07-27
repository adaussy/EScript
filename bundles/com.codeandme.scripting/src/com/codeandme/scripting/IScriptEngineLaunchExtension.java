package com.codeandme.scripting;

/**
 * Interface for script engine launch extensions. An extension gets the chance to modify a script engine before scripts are fed to the engine.
 */
public interface IScriptEngineLaunchExtension {

    /**
     * Called upon a script engine creation. As there might be multiple launch extensions, this might not be the only contribution to the script engine.
     * 
     * @param engine
     *            engine just created
     */
    void createEngine(IScriptEngine engine);
}
