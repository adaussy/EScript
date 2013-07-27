package com.codeandme.scripting.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.codeandme.scripting.ui.view.ScriptShell;

public class SpawnShell extends AbstractHandler implements IHandler {

    public static final String COMMAND_ID = "com.codeandme.commands.scriptShell.newShell";

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        IWorkbenchPage page = HandlerUtil.getActivePart(event).getSite().getPage();

        // create dynamic secondary ID
        int maxID = 0;
        for (IViewReference reference : page.getViewReferences()) {
            if (ScriptShell.VIEW_ID.equals(reference.getId())) {
                try {
                    int secondaryID = Integer.parseInt(reference.getSecondaryId());
                    maxID = Math.max(maxID, secondaryID);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        // open view
        try {
            page.showView(ScriptShell.VIEW_ID, Integer.toString(maxID + 1), IWorkbenchPage.VIEW_ACTIVATE);
        } catch (PartInitException e) {
            // TODO handle this exception (but for now, at least know it happened)
            throw new RuntimeException(e);
        }

        return null;
    }
}
