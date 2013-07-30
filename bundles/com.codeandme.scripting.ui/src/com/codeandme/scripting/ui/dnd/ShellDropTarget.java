/*******************************************************************************
 * Copyright (c) 2013 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/
package com.codeandme.scripting.ui.dnd;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.ui.macro.Macro;

/**
 * DND support for JavaScript shell. DND of plain text, files, resources and IDevices is supported.
 */
public final class ShellDropTarget extends DropTargetAdapter {

    /**
     * JavaScript shell for DND execution.
     */
    private final IScriptEngine mScriptEngine;

    /**
     * Add drop support for various objects. A drop will always be interpreted as <i>copy</i>, even if <i>move</i> was requested.
     * 
     * @param parent
     *            control accepting drops
     * @param javaScriptShell
     *            shell for DND action execution
     */
    public static void addDropSupport(final Control parent, final IScriptEngine engine) {
        final DropTarget target = new DropTarget(parent, DND.DROP_COPY | DND.DROP_MOVE);
        target.setTransfer(new Transfer[] { FileTransfer.getInstance(), TextTransfer.getInstance(), LocalSelectionTransfer.getTransfer() });
        target.addDropListener(new ShellDropTarget(engine));
    }

    /**
     * Constructor.
     * 
     * @param engine
     *            shell for DND action execution
     */
    private ShellDropTarget(final IScriptEngine engine) {
        mScriptEngine = engine;
    }

    @Override
    public void dragEnter(final DropTargetEvent event) {
        if ((event.detail == DND.DROP_MOVE) || (event.detail == DND.DROP_DEFAULT)) {
            if ((event.operations & DND.DROP_COPY) != 0)
                event.detail = DND.DROP_COPY;
            else
                event.detail = DND.DROP_NONE;
        }
    }

    @Override
    public void drop(final DropTargetEvent event) {
        if (event.data instanceof Macro[]) {
            // drop of macros
            for (final Macro macro : (Macro[]) event.data)
                execute(macro);

        } else if (event.data instanceof IResource[]) {
            // drop of IResources
            for (final IResource resource : (IResource[]) event.data)
                execute(resource);

        } else if (event.data instanceof String[]) {
            // drop of external files (eg. from explorer)
            for (final String path : (String[]) event.data)
                mScriptEngine.executeAsync("include(\"file:/" + new Path(path).toString() + "\");");

        } else if (event.data instanceof String) {
            // drop of plain (multiline) text
            execute(event.data);

        } else if (event.data instanceof Object[]) {
            // drop of eg. devices
            for (final Object object : (Object[]) event.data)
                execute(object);

        } else if (event.data instanceof IStructuredSelection) {
            for (Object element : ((IStructuredSelection) event.data).toArray())
                execute(element);

        } else if (event.data instanceof Object)
            // don't think this happens as even single objects are dropped as Object[]
            mScriptEngine.executeAsync(event.data);
    }

    private void execute(final Object element) {
        if (element instanceof IResource)
            mScriptEngine.executeAsync("include(\"workspace:/" + ((IResource) element).getFullPath().toString() + "\");");

        else if (element instanceof Macro)
            mScriptEngine.executeAsync("include(\"macro://" + ((Macro) element).getName() + "\");");

        else
            mScriptEngine.executeAsync(element);
    }
}
