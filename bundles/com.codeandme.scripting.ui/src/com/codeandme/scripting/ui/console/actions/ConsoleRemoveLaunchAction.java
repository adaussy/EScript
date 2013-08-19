package com.codeandme.scripting.ui.console.actions;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.codeandme.scripting.IExecutionListener;
import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.Script;
import com.codeandme.scripting.ui.console.ScriptConsole;

/**
 * ConsoleRemoveTerminatedAction
 */
public class ConsoleRemoveLaunchAction extends Action implements IExecutionListener {

    private ScriptConsole mConsole;

    public ConsoleRemoveLaunchAction() {
        super(ConsoleMessages.ConsoleRemoveTerminatedAction_0);
        setToolTipText(ConsoleMessages.ConsoleRemoveTerminatedAction_1);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.CONSOLE_REMOVE_LAUNCH);
        setImageDescriptor(DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_LCL_REMOVE));
        setDisabledImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_DLCL_REMOVE));
        setHoverImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_ELCL_REMOVE));
    }

    public ConsoleRemoveLaunchAction(final ScriptConsole console) {
        this();
        mConsole = console;
        IScriptEngine engine = mConsole.getScriptEngine();
        if (engine != null)
            engine.addExecutionListener(this);

        setEnabled(engine == null);
    }

    @Override
    public synchronized void run() {
        IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
        manager.removeConsoles(new IConsole[] { mConsole });
    }

    @Override
    public void notify(final IScriptEngine engine, final Script script, final int status) {
        switch (status) {
            case ENGINE_END:
                // remove engine once terminated
                engine.removeExecutionListener(this);
                setEnabled(true);
                break;
        }
    }
}
