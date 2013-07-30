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
package com.codeandme.scripting.javascript.rhino.ui.launching.tabs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.codeandme.scripting.javascript.rhino.ui.launching.LaunchConstants;

public class EngineTab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {

    private Label lblOptimizationLevel;
    private Combo cmbOptimizationLevel;
    private Button chkCreateLineNumber;

    private boolean mDisableUpdate = false;
    private Button chkLegacySupport;
    private Label mlabel;

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(LaunchConstants.OPTIMIZATION_LEVEL, -1);
        configuration.setAttribute(LaunchConstants.GENERATE_LINE_NUMBER_INFORMATION, false);
        configuration.setAttribute(LaunchConstants.LEGACY_MODE, false);
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {
        mDisableUpdate = true;

        chkCreateLineNumber.setSelection(false);
        lblOptimizationLevel.setEnabled(true);
        cmbOptimizationLevel.setEnabled(true);
        cmbOptimizationLevel.setText(cmbOptimizationLevel.getItem(0));
        chkLegacySupport.setSelection(false);

        try {
            final boolean needsLineNumberInformation = configuration.getAttribute(LaunchConstants.GENERATE_LINE_NUMBER_INFORMATION, false);

            if (needsLineNumberInformation) {
                chkCreateLineNumber.setSelection(true);
                lblOptimizationLevel.setEnabled(false);
                cmbOptimizationLevel.setEnabled(false);
                cmbOptimizationLevel.setText(cmbOptimizationLevel.getItem(0));

            } else {
                final int optimizationLevel = configuration.getAttribute(LaunchConstants.OPTIMIZATION_LEVEL, LaunchConstants.DEFAULT_OPTIMIZATION_LEVEL);

                cmbOptimizationLevel.setText(cmbOptimizationLevel.getItem(optimizationLevel + 1));
            }

            chkLegacySupport.setSelection(configuration.getAttribute(LaunchConstants.LEGACY_MODE, false));

        } catch (final CoreException e) {
        }

        mDisableUpdate = false;
    }

    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(LaunchConstants.GENERATE_LINE_NUMBER_INFORMATION, chkCreateLineNumber.getSelection());
        configuration.setAttribute(LaunchConstants.OPTIMIZATION_LEVEL, cmbOptimizationLevel.getSelectionIndex() - 1);
        configuration.setAttribute(LaunchConstants.LEGACY_MODE, chkLegacySupport.getSelection());
    }

    @Override
    public String getMessage() {
        return "Set script engine parameters.";
    }

    @Override
    public String getName() {
        return "Script Engine";
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createControl(final Composite parent) {
        final Composite topControl = new Composite(parent, SWT.NONE);
        topControl.setLayout(new GridLayout(2, false));

        setControl(topControl);

        chkCreateLineNumber = new Button(topControl, SWT.CHECK);
        chkCreateLineNumber.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                lblOptimizationLevel.setEnabled(!chkCreateLineNumber.getSelection());
                cmbOptimizationLevel.setEnabled(!chkCreateLineNumber.getSelection());

                if (!mDisableUpdate)
                    updateLaunchConfigurationDialog();
            }
        });
        chkCreateLineNumber.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        chkCreateLineNumber.setText("create line number information during execution");

        lblOptimizationLevel = new Label(topControl, SWT.NONE);
        lblOptimizationLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblOptimizationLevel.setText("Optimization level:");

        cmbOptimizationLevel = new Combo(topControl, SWT.READ_ONLY);
        cmbOptimizationLevel.setVisibleItemCount(11);
        cmbOptimizationLevel.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                if (!mDisableUpdate)
                    updateLaunchConfigurationDialog();
            }
        });
        cmbOptimizationLevel.setItems(new String[] { "interpretive mode", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9 (highest)" });
        cmbOptimizationLevel.setText(cmbOptimizationLevel.getItem(0));

        mlabel = new Label(topControl, SWT.SEPARATOR | SWT.HORIZONTAL);
        mlabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        chkLegacySupport = new Button(topControl, SWT.CHECK);
        chkLegacySupport.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        chkLegacySupport.setText("support SmartCardManager statements");
        chkLegacySupport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (!mDisableUpdate)
                    updateLaunchConfigurationDialog();
            }
        });
    }
}
