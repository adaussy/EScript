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
package com.codeandme.scripting.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.codeandme.scripting.ui.Activator;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we can use the
 * field support built into JFace that allows us to create a page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main plug-in class. That way, preferences can be
 * accessed directly via the preference store.
 */
public class ShellPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public ShellPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        // FIXME fix preferences lookup

        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Set preferences for the JavaScript shell. These settings will be applied when the shell view is opened only.");

    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various types of preferences. Each field editor
     * knows how to save and restore itself.
     */
    @Override
    public final void createFieldEditors() {
        addField(new RadioGroupFieldEditor(PreferenceConstants.TARGET_RESULT, "Command Results Output", 3, new String[][] { { "Console", "Console" },
                { "Shell", "Shell" }, { "None", "None" } }, getFieldEditorParent(), true));
        addField(new RadioGroupFieldEditor(PreferenceConstants.TARGET_STDOUT, "Standard Output", 3, new String[][] { { "Console", "Console" },
                { "Shell", "Shell" }, { "None", "None" } }, getFieldEditorParent(), true));
        addField(new RadioGroupFieldEditor(PreferenceConstants.TARGET_ERRORS, "Error Output", 3, new String[][] { { "Console", "Console" },
                { "Shell", "Shell" }, { "None", "None" } }, getFieldEditorParent(), true));
        {
            final TextFieldEditor stringFieldEditor = new TextFieldEditor(PreferenceConstants.INIT_COMMANDS, "Startup commands", -1,
                    StringFieldEditor.VALIDATE_ON_FOCUS_LOST, getFieldEditorParent());
            addField(stringFieldEditor);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(final IWorkbench workbench) {
    }
}
