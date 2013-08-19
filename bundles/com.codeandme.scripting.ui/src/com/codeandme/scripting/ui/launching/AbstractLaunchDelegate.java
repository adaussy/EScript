package com.codeandme.scripting.ui.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import com.codeandme.scripting.IScriptEngine;

public abstract class AbstractLaunchDelegate implements ILaunchConfigurationDelegate {

    @Override
    public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
        String projectName = configuration.getAttribute(LaunchConstants.PROJECT, "");
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        if (project.exists()) {
            String scriptName = configuration.getAttribute(LaunchConstants.SCRIPT_LOCATION, "");
            IFile script = project.getFile(scriptName);

            if (script.exists()) {
                // we have a valid script, lets feed it to the script engine
                IScriptEngine engine = getScriptEngine();

                if (engine != null) {

                    // TODO add generic script console output
                    // final JavaScriptConsole console = JavaScriptConsoleFactory.createConsole("JavaScript: " + file.getName(), engine);
                    // engine.setOutputStream(console.getOutputStream());
                    // engine.setErrorStream(console.getErrorStream());

                    engine.setTerminateOnIdle(true);

                    // register script file
                    engine.executeAsync(script);

                    // start engine
                    engine.schedule();

                } else
                    // TODO add some error output to inform user
                    throw new CoreException(Status.CANCEL_STATUS);
            }
        }
    }

    protected abstract IScriptEngine getScriptEngine();
}
