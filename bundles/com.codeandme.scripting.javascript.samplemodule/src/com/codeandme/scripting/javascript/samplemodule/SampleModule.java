package com.codeandme.scripting.javascript.samplemodule;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.codeandme.scripting.javascript.modules.AbstractJavaScriptModule;
import com.codeandme.scripting.javascript.modules.WrapToJavaScript;

public class SampleModule extends AbstractJavaScriptModule {

    public SampleModule() {
    }

    @WrapToJavaScript
    public void displayMessage(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message);
            }
        });
    }

    // @WrapToJavaScript
    // public void openView(final String identifier) {
    // Display.getDefault().asyncExec(new Runnable() {
    // @Override
    // public void run() {
    // try {
    // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(identifier);
    // } catch (final NullPointerException e) {
    // if (PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0)
    // PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().showView(identifier);
    // }
    // }
    // });
    // }
}
