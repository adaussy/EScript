package com.codeandme.scripting;

public interface IScriptService {

    IScriptEngine createEngine(String scriptType);

    IScriptEngine createEngine(String scriptType, String engineID);

}
