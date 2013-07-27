package com.codeandme.scripting.legacy;

public class BreakException extends RuntimeException {

    private static final long serialVersionUID = -4157933914171239048L;
    private Object mCondition = null;

    public BreakException() {
    }

    public BreakException(final Object condition) {
        super();
        mCondition = condition;
    }

    public Object getCondition() {
        return mCondition;
    }
}
