package com.codeandme.scripting.modules.sample;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.codeandme.scripting.modules.AbstractScriptModule;
import com.codeandme.scripting.modules.WrapToScript;

public class SampleModule extends AbstractScriptModule {

    private final class WizardRunnable implements Runnable {
        private IProject mProject = null;

        @Override
        public void run() {

            IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(BasicNewProjectResourceWizard.WIZARD_ID);
            try {
                if (descriptor != null) {
                    IWizard wizard = descriptor.createWizard();
                    ((BasicNewProjectResourceWizard) wizard).init(PlatformUI.getWorkbench(), null);

                    WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
                    dialog.setTitle(wizard.getWindowTitle());
                    dialog.open();

                    mProject = ((BasicNewProjectResourceWizard) wizard).getNewProject();
                }
            } catch (CoreException e) {
            }
        }

        public IProject getProject() {
            return mProject;
        }
    }

    private static final String EXTENSION_VIEWS_ID = "org.eclipse.ui.views";
    private static final Object EXTENSION_VIEW = "view";
    private static final String EXTENSION_ID = "id";
    private static final String EXTENSION_NAME = "name";

    public SampleModule() {
    }

    @WrapToScript
    public void displayMessage(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message);
            }
        });
    }

    @WrapToScript
    public void openView(final String identifier) {

        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                // try to open view with matching ID
                try {
                    IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(identifier);

                } catch (PartInitException ex) {
                    // not found, try to match pattern
                    Pattern pattern = Pattern.compile(identifier);

                    final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_VIEWS_ID);
                    for (final IConfigurationElement e : config) {
                        if (EXTENSION_VIEW.equals(e.getName())) {
                            String id = e.getAttribute(EXTENSION_ID);
                            String name = e.getAttribute(EXTENSION_NAME);

                            try {
                                if ((pattern.matcher(id).matches()) || (pattern.matcher(name).matches()))
                                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(id);
                            } catch (PartInitException e1) {
                            }
                        }
                    }
                }
            }
        });
    }

    @WrapToScript
    public IProject openProjectWizard() {
        WizardRunnable runnable = new WizardRunnable();
        Display.getDefault().syncExec(runnable);

        return runnable.getProject();
    }

}
