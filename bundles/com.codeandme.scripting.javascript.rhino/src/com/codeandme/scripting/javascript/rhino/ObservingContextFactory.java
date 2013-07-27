package com.codeandme.scripting.javascript.rhino;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import com.codeandme.scripting.ExitException;

public class ObservingContextFactory extends ContextFactory {

    private final Set<Context> mTerminationRequests = new HashSet<Context>();

    @Override
    protected synchronized void observeInstructionCount(final Context cx, final int instructionCount) {
        if (mTerminationRequests.remove(cx))
            throw new ExitException();

        super.observeInstructionCount(cx, instructionCount);
    }

    public synchronized void terminate(final Context context) {
        mTerminationRequests.add(context);
    }
}
