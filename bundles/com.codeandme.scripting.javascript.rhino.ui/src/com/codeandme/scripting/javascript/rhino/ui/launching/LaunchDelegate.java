package com.codeandme.scripting.javascript.rhino.ui.launching;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.core.IJavaScriptModelMarker;
import org.eclipse.wst.jsdt.core.ITypeRoot;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.debug.core.jsdi.VirtualMachine;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.AttachingConnector;
import org.eclipse.wst.jsdt.debug.core.jsdi.connect.Connector;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.connect.HostArgument;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.connect.PortArgument;
import org.eclipse.wst.jsdt.debug.internal.rhino.jsdi.connect.RhinoAttachingConnector;
import org.eclipse.wst.jsdt.debug.internal.rhino.ui.ILaunchConstants;
import org.eclipse.wst.jsdt.debug.internal.rhino.ui.RhinoUIPlugin;
import org.eclipse.wst.jsdt.debug.internal.rhino.ui.launching.IncludeEntry;
import org.eclipse.wst.jsdt.debug.internal.rhino.ui.refactoring.Refactoring;
import org.mozilla.javascript.JavaScriptException;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptService;
import com.codeandme.scripting.javascript.rhino.RhinoScriptEngine;
import com.codeandme.scripting.javascript.rhino.debugger.InfineonDebuggerImpl;
import com.codeandme.scripting.javascript.rhino.debugger.RhinoScriptDebugEngine;
import com.codeandme.scripting.ui.console.JavaScriptConsole;
import com.codeandme.scripting.ui.console.JavaScriptConsoleFactory;

public class LaunchDelegate implements ILaunchConfigurationDelegate {

    @Override
    public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
        computeScriptScope(configuration, monitor);
        if ("run".equals(mode)) {
            // debug mode
            final String filePath = configuration.getAttribute(LaunchConstants.FILE_PATH, "");
            final boolean isWorkspaceFile = configuration.getAttribute(LaunchConstants.WORKSPACE_FILE, true);
            final boolean stopAtLaunch = configuration.getAttribute(LaunchConstants.STOP_AT_LAUNCH, false);
            if (isWorkspaceFile) {
                // extract breakpoints
                final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));

                IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
                final IScriptEngine engine = scriptService.createEngine("JavaScript");

                final JavaScriptConsole console = JavaScriptConsoleFactory.createConsole("JavaScript: " + file.getName(), engine);
                engine.setOutputStream(console.getOutputStream());
                engine.setErrorStream(console.getErrorStream());

                engine.setTerminateOnIdle(true);

                if (engine instanceof RhinoScriptEngine) {
                    // set optimization level
                    ((RhinoScriptEngine) engine).setOptimizationLevel(configuration.getAttribute(LaunchConstants.OPTIMIZATION_LEVEL,
                            LaunchConstants.DEFAULT_OPTIMIZATION_LEVEL));

                    // register libraries
                    final Collection<File> libraries = LaunchConstants.getLibraries(configuration);
                    for (final File library : libraries) {
                        try {
                            ((RhinoScriptEngine) engine).registerJar(library.toURI().toURL());
                        } catch (final MalformedURLException e) {

                            e.printStackTrace();
                        }
                    }

                    // set legacy mode
                    if (configuration.getAttribute(LaunchConstants.LEGACY_MODE, LaunchConstants.DEFAULT_LEGACY_MODE))
                        ((RhinoScriptEngine) engine).setLegacyMode();
                }

                // register startup code
                final String startupCode = configuration.getAttribute(LaunchConstants.STARTUP_CODE, "");
                if (!startupCode.trim().isEmpty())
                    engine.executeAsync(startupCode);

                // register source file
                engine.executeAsync(file);

                // start engine
                engine.schedule();
            }
        } else if ("debug".equals(mode)) {

            SubMonitor localmonitor = SubMonitor.convert(monitor, NLS.bind(Messages.launching__, configuration.getName()), 8);
            int port = SocketUtil.findFreePort();
            localmonitor.subTask(Messages.starting_rhino_interpreter);
            // launch the interpreter
            if (localmonitor.isCanceled()) {
                return;
            }
            localmonitor.worked(1);
            VirtualMachine vm = null;
            try {
                PipedInputStream inputStreamIn = new PipedInputStream();
                PipedOutputStream inputStreamOut = new PipedOutputStream(inputStreamIn);

                PipedInputStream outputStreamIn = new PipedInputStream();
                PipedOutputStream outputStreamOut = new PipedOutputStream(outputStreamIn);

                PipedInputStream errorStreamIn = new PipedInputStream();
                PipedOutputStream errorStreamOut = new PipedOutputStream(errorStreamIn);

                localmonitor.subTask(Messages.configuring_rhino_debugger);
                if (localmonitor.isCanceled()) {
                    return;
                }
                localmonitor.worked(1);

                // create connector for eclipse
                HashMap args = new HashMap();
                args.put(HostArgument.HOST, "localhost"); //$NON-NLS-1$
                args.put(PortArgument.PORT, Integer.toString(port));

                localmonitor.subTask(Messages.creating_rhino_vm);
                if (localmonitor.isCanceled()) {
                    return;
                }
                localmonitor.worked(1);

                RhinoAttachingConnector connector = new RhinoAttachingConnector();

                InfineonRhinoProcess process = new InfineonRhinoProcess(launch, computeProcessName(connector, args), inputStreamOut, outputStreamIn,
                        errorStreamIn);
                launch.addProcess(process);
                localmonitor.subTask(Messages.creating_js_debug_target);
                if (localmonitor.isCanceled()) {
                    return;
                }
                localmonitor.worked(1);

                // Open Listening Server
                final boolean stopAtLaunch = configuration.getAttribute(LaunchConstants.STOP_AT_LAUNCH, false);
                final String filePath = configuration.getAttribute(LaunchConstants.FILE_PATH, "");
                final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
                InfineonDebuggerImpl debugger = new InfineonDebuggerImpl(file, stopAtLaunch, Integer.toString(port));
                debugger.start();

                // start eclipse connector and connect to rhino
                ConnectRunnable runnable = new ConnectRunnable(connector, args);
                Thread thread = new Thread(runnable, Messages.connect_thread);
                thread.setDaemon(true);
                thread.start();

                // wait for connection
                while (thread.isAlive()) {
                    if (localmonitor.isCanceled()) {
                        thread.interrupt();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                }

                if (runnable.exception != null) {
                    throw runnable.exception;
                }
                if (runnable.vm == null) {
                    throw new IOException("Failed to connect to Rhino interpreter."); //$NON-NLS-1$
                }

                vm = runnable.vm;

                localmonitor.subTask(Messages.starting_rhino_process);
                if (localmonitor.isCanceled()) {
                    cancel(vm);
                    return;
                }
                localmonitor.worked(1);

                // create debug target
                JavaScriptDebugTarget target = new JavaScriptDebugTarget(vm, process, launch, true, false);
                launch.addDebugTarget(target);
                if (localmonitor.isCanceled()) {
                    cancel(vm);
                    return;
                }
                localmonitor.worked(1);

                process.setTarget(target);

                // create script engine
                IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
                final RhinoScriptDebugEngine engine = (RhinoScriptDebugEngine) scriptService.createEngineByID("com.codeandme.scripting.javascript.rhino.debug");

                engine.setInputStream(inputStreamIn);
                engine.setOutputStream(new PrintStream(outputStreamOut));
                engine.setErrorStream(new PrintStream(errorStreamOut));

                // engine.setInputStream(inputStreamIn);
                // process.setInputStream(inputStreamOut);

                // add main file to script engine
                // final String filePath = configuration.getAttribute(LaunchConstants.FILE_PATH, "");
                final boolean isWorkspaceFile = configuration.getAttribute(LaunchConstants.WORKSPACE_FILE, true);

                if (isWorkspaceFile) {
                    final IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
                    engine.executeAsync(file2);
                } else {
                    engine.executeAsync(new File(filePath));
                }
                engine.setDebugger(debugger);

                // start Rhino debugger engine
                engine.setContextConsumer(process);
                engine.schedule();

                return;

            } catch (IOException ioe) {
                ioe.printStackTrace();
                cancel(vm);
                RhinoUIPlugin.log(ioe);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof JavaScriptException) {
                    // ignore certain exceptions from code
                    if (configuration.getAttribute(ILaunchConstants.ATTR_LOG_INTERPRETER_EXCEPTIONS, true)) {
                        RhinoUIPlugin.log(e);
                    }
                    return;
                }
                cancel(vm);
                RhinoUIPlugin.log(e);
                System.out.println(e);
            } finally {
                if (scope != null) {
                    scope.clear();
                    scope = null;
                }
                script = null;
            }
            launch.terminate();
            return;
        }

    }

    class ConnectRunnable implements Runnable {

        VirtualMachine vm = null;
        Exception exception = null;
        private AttachingConnector connector = null;
        private Map args = null;

        ConnectRunnable(final AttachingConnector connector, final Map args) {
            this.connector = connector;
            this.args = args;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                long start = System.currentTimeMillis();
                Exception inner = null;
                do {
                    try {
                        vm = connector.attach(args);
                    } catch (Exception e) {
                        inner = e;
                    }
                } while ((vm == null) && (System.currentTimeMillis() < (start + 15000)));
                if (vm == null) {
                    throw inner;
                }
            } catch (Exception e) {
                exception = e;
            }
        }
    }

    class Filter implements FileFilter {
        @Override
        public boolean accept(final File pathname) {
            return pathname.isDirectory() || JavaScriptCore.isJavaScriptLikeFileName(pathname.getName());
        }
    }

    /**
     * The name of the main class to run <br>
     * <br>
     * Value is: <code>"org.eclipse.wst.jsdt.debug.rhino.debugger.shell.DebugShell"</code>
     */
    public static final String DEBUG_SHELL_CLASS = "org.eclipse.wst.jsdt.debug.rhino.debugger.shell.DebugMain"; //$NON-NLS-1$
    /**
     * The name of the main class to run <br>
     * <br>
     * Value is: <code>"org.mozilla.javascript.tools.shell.Main"</code>
     */
    public static final String RHINO_MAIN_CLASS = "org.mozilla.javascript.tools.shell.Main"; //$NON-NLS-1$
    /**
     * The symbolic name of the Mozilla Rhino bundle <br>
     * <br>
     * Value is: <code>org.mozilla.javascript</code>
     */
    public static final String MOZILLA_JAVASCRIPT_BUNDLE = "org.mozilla.javascript"; //$NON-NLS-1$
    /**
     * The symbolic name of the debug transport bundle <br>
     * <br>
     * Value is: <code>org.eclipse.wst.jsdt.debug.transport</code>
     */
    public static final String DEBUG_TRANSPORT_BUNDLE = "org.eclipse.wst.jsdt.debug.transport"; //$NON-NLS-1$
    /**
     * The symbolic name of the Rhino debug interface bundle <br>
     * <br>
     * Value is: <code>org.eclipse.wst.jsdt.debug.rhino.debugger</code>
     */
    private static final String RHINO_DEBUGGER_BUNDLE = "org.eclipse.wst.jsdt.debug.rhino.debugger"; //$NON-NLS-1$

    /**
     * Array of the bundles required to launch
     */
    public static final String[] REQUIRED_BUNDLES = { MOZILLA_JAVASCRIPT_BUNDLE, DEBUG_TRANSPORT_BUNDLE, RHINO_DEBUGGER_BUNDLE };

    private ArrayList scope = null;
    private ITypeRoot script = null;

    synchronized ITypeRoot getScript(final ILaunchConfiguration configuration) throws CoreException {
        if (this.script == null) {
            IResource resource = Refactoring.getScript(configuration);
            if (resource != null) {
                this.script = (ITypeRoot) JavaScriptCore.create((IFile) resource);
            }
        }
        return this.script;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String,
     * org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
     */

    /**
     * Stops + cleans up on cancellation / failure
     * 
     * @param vm
     * @param process
     */
    void cancel(final VirtualMachine vm) {
        if (vm != null) {
            vm.terminate();
        }
    }

    /**
     * Adds the script scope to the args
     * 
     * @param args
     */
    void addFArg(final ArrayList args) {
        args.add("-f"); //$NON-NLS-1$
        args.addAll(scope);
    }

    /**
     * Return the <code>-Dfile.encoding</code> VM argument to use or <code>null</code> it no special encoding has been specified
     * 
     * @param configuration
     * @return the file encoding string or <code>null</code>
     */
    String getEncoding(final ILaunch launch) {
        String encoding = launch.getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING);
        if (encoding != null) {
            StringBuffer buffer = new StringBuffer("-Dfile.encoding="); //$NON-NLS-1$
            buffer.append(encoding);
            return buffer.toString();
        }
        return null;
    }

    /**
     * Escapes the path of the given file.
     * 
     * @param file
     *            the file to escape the path for
     * @return the escaped path
     */
    String escapePath(final File file, final boolean singlequote) {
        String path = file.getAbsolutePath();
        StringCharacterIterator iter = new StringCharacterIterator(path);
        StringBuffer buffer = new StringBuffer();
        boolean hasspace = false;
        char c = iter.current();
        while (c != CharacterIterator.DONE) {
            if (c == '\\') {
                buffer.append("/"); //$NON-NLS-1$
            } else if (c == '"') {
                buffer.append("\""); //$NON-NLS-1$
            } else if (c == ' ') {
                hasspace = true;
                buffer.append(c);
            } else {
                buffer.append(c);
            }
            c = iter.next();
        }
        /*
         * if(singlequote) { buffer.insert(0, '\''); buffer.append('\''); return buffer.toString(); } else
         */if (hasspace && !singlequote) {
            buffer.insert(0, "\""); //$NON-NLS-1$
            buffer.append("\""); //$NON-NLS-1$
            return buffer.toString();
        }
        return path;
    }

    /**
     * Appends the correct version of a classpath separator to the given buffer
     * 
     * @param buffer
     *            the buffer to add the separator to
     */
    void appendSep(final StringBuffer buffer) {
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            buffer.append(';');
        } else {
            buffer.append(':');
        }
    }

    /**
     * Computes the complete list of scripts to load, with the last loaded script being the one desired
     * 
     * @param configuration
     * @param monitor
     * @throws CoreException
     */
    synchronized void computeScriptScope(final ILaunchConfiguration configuration, final IProgressMonitor monitor) throws CoreException {
        // for now we only load the script you want to debug and the ones reported from the JS project as source containers
        if (this.scope == null) {
            this.scope = new ArrayList();
            List list = configuration.getAttribute(ILaunchConstants.ATTR_INCLUDE_PATH, (List) null);
            if (list != null) {
                String entry = null;
                for (Iterator i = list.iterator(); i.hasNext();) {
                    entry = (String) i.next();
                    int kind = Integer.parseInt(entry.substring(0, 1));
                    switch (kind) {
                        case IncludeEntry.LOCAL_SCRIPT: {
                            String value = entry.substring(1);
                            IFile ifile = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(value);
                            if ((ifile != null) && ifile.exists()) {
                                File file = URIUtil.toFile(ifile.getLocationURI());
                                value = escapePath(file, true);
                                if (!scope.contains(value)) {
                                    scope.add(value);
                                }
                            }
                            continue;
                        }
                        case IncludeEntry.EXT_SCRIPT: {
                            String f = entry.substring(1);
                            File file = new File(f);
                            if (file.exists() && !scope.contains(file.getAbsolutePath())) {
                                scope.add(file.getAbsolutePath());
                            }
                            continue;
                        }
                    }
                }
            }
            addScriptAttribute(configuration, scope);
        }
    }

    /**
     * Adds the absolute path of the script specified in the launch configuration to the listing of arguments
     * 
     * @param configuration
     * @param args
     * @throws CoreException
     */
    void addScriptAttribute(final ILaunchConfiguration configuration, final ArrayList args) throws CoreException {
        // ITypeRoot root = getScript(configuration);
        // File file = URIUtil.toFile(root.getResource().getLocationURI());
        String filePath = configuration.getAttribute(LaunchConstants.FILE_PATH, "");
        IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
        File file = ifile.getFullPath().toFile();
        String value = escapePath(file, true);
        args.remove(value);
        // want to make sure it is interpreted last
        args.add(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate2#getLaunch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String)
     */
    public ILaunch getLaunch(final ILaunchConfiguration configuration, final String mode) throws CoreException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate2#buildForLaunch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean buildForLaunch(final ILaunchConfiguration configuration, final String mode, final IProgressMonitor monitor) throws CoreException {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate2#finalLaunchCheck(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean finalLaunchCheck(final ILaunchConfiguration configuration, final String mode, final IProgressMonitor monitor) throws CoreException {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate2#preLaunchCheck(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public boolean preLaunchCheck(final ILaunchConfiguration configuration, final String mode, final IProgressMonitor monitor) throws CoreException {
        monitor.subTask(Messages.computing_script_scope);
        computeScriptScope(configuration, monitor);
        if (this.scope.isEmpty()) {
            this.scope = null;
            throw new CoreException(new Status(IStatus.ERROR, RhinoUIPlugin.PLUGIN_ID, Messages.failed_to_compute_scope));
        }
        return true;
    }

    /**
     * Returns if the script to launch has any JavaScript problems
     * 
     * @param configuration
     * @return
     * @throws CoreException
     */
    boolean hasProblems(final ILaunchConfiguration configuration) throws CoreException {
        // TODO this should be expanded to check the entire script scope
        String name = configuration.getAttribute(ILaunchConstants.ATTR_SCRIPT, ILaunchConstants.EMPTY_STRING);
        IResource script = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(name));
        if (script.isAccessible()) {
            IMarker[] problems = script.findMarkers(IJavaScriptModelMarker.JAVASCRIPT_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
            return problems.length > 0;
        }
        return false;
    }

    /**
     * Turns the command line argument list into a string
     * 
     * @param args
     * @return the command line list as a string
     */
    String formatCommandlineArgs(final ArrayList args) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < args.size(); i++) {
            buffer.append(args.get(i));
            if (i < (args.size() - 1)) {
                buffer.append(" "); //$NON-NLS-1$
            }
        }
        return buffer.toString();
    }

    /**
     * Computes the command line to set on the process attribute
     * 
     * @param args
     * @return
     */
    String formatConnectionArguments(final Map args) {
        StringBuffer buffer = new StringBuffer();
        Map.Entry entry = null;
        for (Iterator iter = args.entrySet().iterator(); iter.hasNext();) {
            entry = (Entry) iter.next();
            buffer.append(entry.getKey()).append(':').append(entry.getValue());
            if (iter.hasNext()) {
                buffer.append(',');
            }
        }
        return buffer.toString();
    }

    /**
     * Computes the display name for the {@link IProcess} given the connector
     * 
     * @param connector
     * @return the name for the process
     */
    String computeProcessName(final Connector connector, final Map args) {
        StringBuffer buffer = new StringBuffer(connector.name());
        String timestamp = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(System.currentTimeMillis()));
        return NLS.bind(Messages.process_label, new String[] { buffer.toString(), formatConnectionArguments(args), timestamp });
    }

}
