package com.codeandme.scripting.javascript.rhino.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.codeandme.scripting.IScriptEngineProvider;

/**
 * Pulldown handler to load/ list modules.
 */
public class LoadModule extends AbstractHandler implements IHandler {

    public static final String COMMAND_ID = "com.codeandme.commands.scriptShell.loadModule";
    public static final String PARAMETER_NAME = COMMAND_ID + ".moduleID";

    @Override
    public final Object execute(final ExecutionEvent event) throws ExecutionException {

        final IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof IScriptEngineProvider) {
            final String moduleID = event.getParameter(PARAMETER_NAME);

            if (moduleID != null)
                // specific module selected
                ((IScriptEngineProvider) part).getScriptEngine().executeAsync("loadModule(\"" + moduleID + "\");");

            else
                // button was clicked, no module selected
                ((IScriptEngineProvider) part).getScriptEngine().executeAsync("listModules();");
        }

        return null;
    }
}
