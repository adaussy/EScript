package com.codeandme.scripting.modules;

public interface IModuleListener {

    int LOADED = 1;
    int RELOADED = 2;

    void notifyModule(IScriptModule module, int type);
}
