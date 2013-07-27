package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.handlers.HandlerUtil;

import com.codeandme.scripting.IScriptEngineProvider;
import com.codeandme.scripting.ui.console.JavaScriptConsole;

public class StopScriptExecution extends AbstractHandler implements IHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof org.eclipse.ui.internal.console.ConsoleView) {
            final IConsole console = ((org.eclipse.ui.internal.console.ConsoleView) part).getConsole();
            if (console instanceof JavaScriptConsole)
                ((JavaScriptConsole) console).stopScriptExecution();

        } else if (part instanceof IScriptEngineProvider)
            ((IScriptEngineProvider) part).getScriptEngine().terminateCurrent();

        return null;
    }
}
