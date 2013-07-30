package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.codeandme.scripting.ui.view.ScriptShell;

public class SwitchEngine extends AbstractHandler implements IHandler {

    public static final String COMMAND_ID = "com.codeandme.commands.scriptShell.switchEngine";
    public static final String PARAMETER_ID = COMMAND_ID + ".id";

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof ScriptShell)
            ((ScriptShell) part).setEngine(event.getParameter(PARAMETER_ID));

        return null;
    }
}
