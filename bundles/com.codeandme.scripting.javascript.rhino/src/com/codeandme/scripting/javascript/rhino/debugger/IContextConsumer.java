package com.codeandme.scripting.javascript.rhino.debugger;

import org.mozilla.javascript.Context;

public interface IContextConsumer {
    void setContext(Context context);
}
