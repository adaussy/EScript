package com.codeandme.scripting;

import com.codeandme.scripting.legacy.BreakException;

public class ExitException extends BreakException {

    private static final long serialVersionUID = 9134574495641360069L;

    public ExitException() {
    }

    public ExitException(final Object condition) {
        super(condition);
    }
}
