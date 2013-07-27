package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineProvider;

public class Reset extends AbstractHandler implements IHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof IScriptEngineProvider) {
            final IScriptEngine engine = ((IScriptEngineProvider) part).getScriptEngine();
            if (engine != null)
                engine.reset();
        }

        return null;
    }
}
