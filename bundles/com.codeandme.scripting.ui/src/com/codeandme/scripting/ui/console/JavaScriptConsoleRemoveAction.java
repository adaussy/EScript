package com.codeandme.scripting.ui.console;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.codeandme.scripting.ui.Activator;

/**
 * closes the JavaScript console
 */
public class JavaScriptConsoleRemoveAction extends Action {

    private IConsole console;

    public JavaScriptConsoleRemoveAction() {
        super("Close JavaScript Console");
        setToolTipText("Close JavaScript Console");
        setImageDescriptor(Activator.getImageDescriptor("images\\close.gif"));
    }

    public JavaScriptConsoleRemoveAction(final JavaScriptConsole fConsole) {
        this();
        this.console = fConsole;
    }

    @Override
    public synchronized void run() {
        if (this.console != null) {
            IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
            manager.removeConsoles(new IConsole[] { console });
        }
    }
}