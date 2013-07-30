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
package com.codeandme.scripting.ui.macro.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.codeandme.scripting.ui.macro.Macro;

public class ScriptEngineFilter extends ViewerFilter {

    private final String mEngineID;

    public ScriptEngineFilter(final String engineID) {
        mEngineID = engineID;
    }

    @Override
    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
        if (element instanceof Macro)
            return ((Macro) element).getEngine().equals(mEngineID);

        return true;
    }
}
