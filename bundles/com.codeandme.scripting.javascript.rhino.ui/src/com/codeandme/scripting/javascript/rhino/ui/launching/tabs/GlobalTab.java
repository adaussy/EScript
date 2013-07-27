package com.codeandme.scripting.javascript.rhino.ui.launching.tabs;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.codeandme.scripting.javascript.rhino.ui.launching.JavaScriptFileContentProvider;
import com.codeandme.scripting.javascript.rhino.ui.launching.LaunchConstants;

public class GlobalTab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {
    private Text txtWorkspace;
    private Text txtFilesystem;
    private Text txtStartupCode;
    private Button btnWorkspace;
    private Button btnFileSystem;
    private Button btnCheckButton;
    private boolean mDisableUpdate = false;

    @Override
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    }

    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {
        mDisableUpdate = true;

        btnWorkspace.setSelection(false);
        btnFileSystem.setSelection(false);
        btnCheckButton.setSelection(false);
        txtWorkspace.setText("");
        txtFilesystem.setText("");
        txtStartupCode.setText("");

        try {
            boolean isWorkspaceFile = configuration.getAttribute(LaunchConstants.WORKSPACE_FILE, true);
            if (isWorkspaceFile) {
                btnWorkspace.setSelection(true);
                txtWorkspace.setText(configuration.getAttribute(LaunchConstants.FILE_PATH, ""));
            } else {
                btnFileSystem.setSelection(true);
                txtFilesystem.setText(configuration.getAttribute(LaunchConstants.FILE_PATH, ""));
            }

            txtStartupCode.setText(configuration.getAttribute(LaunchConstants.STARTUP_CODE, ""));
        } catch (CoreException e) {
        }

        mDisableUpdate = false;
    }

    @Override
    public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
        if (btnWorkspace.getSelection()) {
            configuration.setAttribute(LaunchConstants.WORKSPACE_FILE, true);
            configuration.setAttribute(LaunchConstants.FILE_PATH, txtWorkspace.getText());
        } else {
            configuration.setAttribute(LaunchConstants.WORKSPACE_FILE, false);
            configuration.setAttribute(LaunchConstants.FILE_PATH, txtFilesystem.getText());
        }
        configuration.setAttribute(LaunchConstants.STOP_AT_LAUNCH, btnCheckButton.getSelection());
        configuration.setAttribute(LaunchConstants.STARTUP_CODE, txtStartupCode.getText().trim());
    }

    @Override
    public boolean isValid(final ILaunchConfiguration launchConfig) {
        // allow launch when a file is selected and file exists
        try {
            String filePath = launchConfig.getAttribute(LaunchConstants.FILE_PATH, "");
            boolean isWorkspaceFile = launchConfig.getAttribute(LaunchConstants.WORKSPACE_FILE, true);
            if (isWorkspaceFile) {
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
                return file.exists();

            } else
                return new File(filePath).exists();
        } catch (Exception e) {
            // on any configuration error
            setErrorMessage("Invalid source file selected.");
        }

        return false;
    }

    @Override
    public boolean canSave() {
        // allow save when a file location is entered - no matter if the file
        // exists or not
        if (btnWorkspace.getSelection())
            return !txtWorkspace.getText().isEmpty();

        else if (btnFileSystem.getSelection())
            return !txtFilesystem.getText().isEmpty();

        return false;
    }

    @Override
    public String getMessage() {
        return "Please select a JavaScript source file.";
    }

    @Override
    public String getName() {
        return "Global";
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void createControl(final Composite parent) {
        Composite topControl = new Composite(parent, SWT.NONE);
        topControl.setLayout(new GridLayout(1, false));

        Group grpLaunch = new Group(topControl, SWT.NONE);
        grpLaunch.setLayout(new GridLayout(3, false));
        grpLaunch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpLaunch.setText("Launch");

        btnWorkspace = new Button(grpLaunch, SWT.RADIO);
        btnWorkspace.setBounds(0, 0, 104, 21);
        btnWorkspace.setText("Workspace");

        txtWorkspace = new Text(grpLaunch, SWT.BORDER);
        txtWorkspace.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                if (!mDisableUpdate)
                    updateLaunchConfigurationDialog();
            }
        });
        txtWorkspace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnBrowseWorkspace = new Button(grpLaunch, SWT.NONE);
        btnBrowseWorkspace.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(parent.getShell(), new WorkbenchLabelProvider(),
                        new JavaScriptFileContentProvider());
                dialog.setTitle("Select JavaScript source file");
                dialog.setMessage("Select the JavaScript file to execute:");
                dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
                if (dialog.open() == Window.OK) {
                    btnFileSystem.setSelection(false);
                    btnWorkspace.setSelection(true);
                    txtWorkspace.setText(((IFile) dialog.getFirstResult()).getFullPath().toPortableString());
                }
            }
        });
        btnBrowseWorkspace.setText("Browse...");

        btnFileSystem = new Button(grpLaunch, SWT.RADIO);
        btnFileSystem.setText("File System");

        txtFilesystem = new Text(grpLaunch, SWT.BORDER);
        txtFilesystem.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                if (!mDisableUpdate)
                    updateLaunchConfigurationDialog();
            }
        });
        txtFilesystem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button btnBrowseFilesystem = new Button(grpLaunch, SWT.NONE);
        btnBrowseFilesystem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
                dialog.setFilterExtensions(new String[] { "*.js", "*.*" });
                dialog.setFilterNames(new String[] { "JavaScript Source Files", "All Files" });
                String filePath = dialog.open();
                if (filePath != null) {
                    btnWorkspace.setSelection(false);
                    btnFileSystem.setSelection(true);
                    txtFilesystem.setText(filePath);
                }
            }
        });
        btnBrowseFilesystem.setText("Browse...");

        btnCheckButton = new Button(topControl, SWT.CHECK);
        btnCheckButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (!mDisableUpdate)
                    updateLaunchConfigurationDialog();
            }
        });
        btnCheckButton.setText("Stop at first Line");

        Label lblStartupCode = new Label(topControl, SWT.NONE);
        lblStartupCode.setText("Startup code:");

        txtStartupCode = new Text(topControl, SWT.BORDER | SWT.MULTI);
        txtStartupCode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtStartupCode.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                if (!mDisableUpdate)
                    updateLaunchConfigurationDialog();
            }
        });

        setControl(topControl);
    }
}
