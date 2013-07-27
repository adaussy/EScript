package com.codeandme.scripting.javascript.modules;

public interface IModuleListener {

    int LOADED = 1;
    int RELOADED = 2;

    void notifyModule(AbstractJavaScriptModule module, int type);
}
