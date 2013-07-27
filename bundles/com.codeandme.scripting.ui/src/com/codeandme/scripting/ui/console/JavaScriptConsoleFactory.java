package com.codeandme.scripting.ui.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;

import com.codeandme.scripting.IScriptEngine;

public class JavaScriptConsoleFactory implements IConsoleFactory {

    @Override
    public void openConsole() {
        final JavaScriptConsole console = new JavaScriptConsole("JavaScript Console", JavaScriptConsole.getConsoleType(), null, null);

        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
    }

    public static JavaScriptConsole createConsole(final String name, final IScriptEngine engine) {
        final JavaScriptConsole console = new JavaScriptConsole(name, engine);

        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);

        return console;
    }

    public static JavaScriptConsole getConsole(final String name, final IScriptEngine engine) {
        final IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
        for (final IConsole console : consoles) {
            if ((console instanceof JavaScriptConsole) && (console.getName().equals(name))) {
                ((JavaScriptConsole) console).setEngine(engine);
                return (JavaScriptConsole) console;
            }
        }

        return createConsole(name, engine);
    }

    /**
     * Returns the list of all JavaScript consoles
     * 
     * @return
     */
    public static List<IConsole> getAllJavaScriptConsoles() {
        List<IConsole> list = new ArrayList<IConsole>();
        final IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
        for (final IConsole console : consoles) {
            if ((console instanceof JavaScriptConsole)) {
                list.add(console);
            }
        }
        return list;
    }
}
