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
package com.codeandme.scripting.javascript.rhino.ui.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptService;
import com.codeandme.scripting.ui.console.JavaScriptConsole;
import com.codeandme.scripting.ui.console.JavaScriptConsoleFactory;

/**
 * Quick launcher for JavaScript files.
 */
public class JavaScriptLaunchShortcut implements ILaunchShortcut, ILaunchShortcut2 {

    @Override
    public final void launch(final ISelection selection, final String mode) {
        if (selection instanceof IStructuredSelection) {
            for (final Object element : ((IStructuredSelection) selection).toArray()) {
                if (element instanceof IFile) {
                    // try to save dirty editors
                    IEditorPart[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDirtyEditors();
                    for (IEditorPart editor : editors) {
                        IEditorInput input = editor.getEditorInput();
                        if (input instanceof FileEditorInput) {
                            if (element.equals(((FileEditorInput) input).getFile()))
                                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveEditor(editor, true);
                        }
                    }

                    runJavaScript((IFile) element);
                }
            }
        }
    }

    /**
     * Execute JavaScript code from an {@link IFile}.
     * 
     * @param file
     *            file to execute
     */
    private void runJavaScript(final IFile file) {
        IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
        final IScriptEngine engine = scriptService.createEngine("JavaScript");

        final JavaScriptConsole console = JavaScriptConsoleFactory.createConsole("JavaScript: " + file.getName(), engine);
        engine.setOutputStream(console.getOutputStream());
        engine.setErrorStream(console.getErrorStream());

        engine.setTerminateOnIdle(true);
        engine.executeAsync(file);

        engine.schedule();
    }

    @Override
    public final void launch(final IEditorPart editor, final String mode) {

        // try to save dirty editor
        if (editor.isDirty())
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveEditor(editor, true);

        final IEditorInput input = editor.getEditorInput();
        if (input instanceof FileEditorInput)
            runJavaScript(((FileEditorInput) input).getFile());
    }

    @Override
    public final ILaunchConfiguration[] getLaunchConfigurations(final ISelection selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final ILaunchConfiguration[] getLaunchConfigurations(final IEditorPart editorpart) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final IResource getLaunchableResource(final ISelection selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final IResource getLaunchableResource(final IEditorPart editorpart) {
        // TODO Auto-generated method stub
        return null;
    }
}
