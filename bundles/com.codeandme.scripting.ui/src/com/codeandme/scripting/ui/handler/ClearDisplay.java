package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.codeandme.scripting.ui.view.ScriptShell;

/**
 * Handler to clear the command pane.
 */
public class ClearDisplay extends AbstractHandler implements IHandler {

    @Override
    public final Object execute(final ExecutionEvent event) throws ExecutionException {
        final IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof ScriptShell)
            ((ScriptShell) part).clearOutput();

        return null;
    }
}
