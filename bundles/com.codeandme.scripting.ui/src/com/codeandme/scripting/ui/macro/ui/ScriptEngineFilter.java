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
