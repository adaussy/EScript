package com.codeandme.scripting.javascript.modules;

import helpers.ResourceTools;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;

import com.codeandme.scripting.BreakException;
import com.codeandme.scripting.ExitException;
import com.codeandme.scripting.IDebugEngine;
import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineLaunchExtension;
import com.codeandme.scripting.ScriptResult;

/**
 * The RhinoEnvironment provides base functions for all JavaScript interpreters. It is automatically loaded by any interpreter upon startup.
 */
public class EnvironmentModule extends AbstractJavaScriptModule implements IScriptEngineLaunchExtension, IModuleProvider {

    public static final String MODULE_ID = "Environment";

    /** Extension point ID for script modules. */
    private static final String EXTENSION_MODULE_ID = "com.codeandme.scripting.javascript.module";

    /** Module dependency parameter name. */
    private static final String CONFIG_DEPENDENCY_NAME = "module";

    private final List<AbstractJavaScriptModule> mModules = new ArrayList<AbstractJavaScriptModule>();

    private final ListenerList mModuleListeners = new ListenerList();

    /**
     * Register the environment and its functions within the current running engine.
     */
    public static void bootstrap() {
        final Job currentJob = Job.getJobManager().currentJob();
        if (currentJob instanceof IScriptEngine) {
            final EnvironmentModule module = new EnvironmentModule();
            module.initialize("Environment", (IScriptEngine) currentJob, module);
            module.loadModule(module);
        }
    }

    @Override
    public void createEngine(final IScriptEngine engine) {
        // FIXME check that engine supports Rhino
        if (engine.getID().contains(".rhino"))
            engine.executeAsync("Packages." + this.getClass().getName() + ".bootstrap();");
    }

    /**
     * Get a map of all available extension modules. This includes both loaded and unloaded modules. The map contains of paisr
     * 
     * @return list of extension modules
     */
    public static Map<String, Class<? extends AbstractJavaScriptModule>> getAvailableModules(final boolean findHidden) {
        final Map<String, Class<? extends AbstractJavaScriptModule>> modules = new HashMap<String, Class<? extends AbstractJavaScriptModule>>();

        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);
        for (final IConfigurationElement element : elements) {

            // only add items marked as visible
            if (Boolean.parseBoolean(element.getAttribute("visible")) || findHidden) {
                try {
                    final Object o = element.createExecutableExtension("class");
                    if (o instanceof AbstractJavaScriptModule) {
                        final String name = element.getAttribute("name");
                        modules.put(name, ((AbstractJavaScriptModule) o).getClass());
                    }
                } catch (final CoreException e) {
                    // FIXME add log message
                    // Logger.warning("Could not load module.", e);
                }
            }
        }

        return modules;
    }

    private void loadModule(final AbstractJavaScriptModule module) {
        // FIXME module loading always starts with searching the registry, afterwards it drops here. Change for performance reasons

        // try to find module if already loaded
        final AbstractJavaScriptModule loadedModule = findModule(module.getModuleName());

        if (loadedModule != null)
            // module already loaded, refresh registered functions
            mModules.remove(loadedModule);

        else
            // new module, register & initialize module
            // cast is save as RhinoEngine implements IDebugEngine (bootstrap won't work on anything else)
            ((IDebugEngine) getScriptEngine()).setVariable(module.getVariableName(), module);

        mModules.add(0, module);

        // load wrappers after adding the module to the list. This way a module
        // can add an IJavaScriptFunctionModifier
        // which will be applied to itself
        createWrappers(module, loadedModule != null);

        // notify listeners
        fireModuleEvent(module, (loadedModule != null) ? IModuleListener.RELOADED : IModuleListener.LOADED);
    }

    /**
     * Create JavaScript wrapper functions for autoload methods. Adds code of following style: <code>function {name} (a, b, c, ...) {
     *     __module.{name}(a, b, c, ...);
     *  }</code>
     * 
     * @param module
     *            module instance to create wrappers for
     * @param reload
     *            flag indicating that the module was already loaded
     */
    private void createWrappers(final AbstractJavaScriptModule module, final boolean reload) {
        // JavaScript code to injec t
        StringBuffer javaScriptCode = new StringBuffer();

        // use reflection to access methods
        for (final Method method : module.getClass().getMethods()) {
            if (useAutoLoader(method)) {

                // create body
                StringBuffer body = new StringBuffer();

                // insert hooked pre execution code
                body.append(getPreExecutionCode(method));

                // create parameter string
                final StringBuffer parameters = new StringBuffer();
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    parameters.append(',');
                    parameters.append((char) ('a' + i));
                }
                if (parameters.length() > 0)
                    parameters.replace(0, 1, "");

                // insert method call
                body.append("\tvar ");
                body.append(IJavaScriptFunctionModifier.RESULT_NAME);
                body.append(" = ");
                body.append(module.getVariableName());
                body.append(".");
                body.append(method.getName());
                body.append("(");
                body.append(parameters);
                body.append(");\n");

                // insert hooked post execution code
                body.append(getPostExecutionCode(method));

                // insert return statement
                body.append("\treturn ");
                body.append(IJavaScriptFunctionModifier.RESULT_NAME);
                body.append(";\n");

                Set<String> methodNames = new HashSet<String>();
                // register main method
                methodNames.add(method.getName());
                // re-register for synonyms
                methodNames.addAll(Arrays.asList(method.getAnnotation(WrapToJavaScript.class).alias().split(WrapToJavaScript.DELIMITER)));

                for (String name : methodNames) {
                    if (!name.isEmpty()) {
                        javaScriptCode.append("function ");
                        javaScriptCode.append(name);
                        javaScriptCode.append("(");
                        javaScriptCode.append(parameters);
                        javaScriptCode.append(") {\n");
                        javaScriptCode.append(body);
                        javaScriptCode.append("}\n");
                    }
                }
            }
        }

        if (!reload) {
            Field[] declaredFields = module.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    if (field.getAnnotation(WrapToJavaScript.class) != null) {
                        javaScriptCode.append("const ");
                        javaScriptCode.append(field.getName().toUpperCase());
                        javaScriptCode.append(" = ");
                        javaScriptCode.append(module.getVariableName());
                        javaScriptCode.append(".");
                        javaScriptCode.append(field.getName());
                        javaScriptCode.append(";\n");
                    }
                }
            }
        }

        // execute code
        getScriptEngine().inject(javaScriptCode.toString());
    }

    /**
     * Verify that a method is treated by the autoloader. This is the case when the method is marked by an @WrapToJavaScript annotation.
     * 
     * @param method
     *            method to be evaluated
     * @return true when autoloader should handle this method
     */
    private static boolean useAutoLoader(final Method method) {
        return (method.getAnnotation(WrapToJavaScript.class) != null);
    }

    /**
     * Resolves a loaded module and returns the Java instance. Will only query previously loaded modules.
     * 
     * @param name
     *            name of the module to resolve
     */
    @Override
    @WrapToJavaScript
    public final AbstractJavaScriptModule findModule(final String name) {
        for (final AbstractJavaScriptModule module : getLoadedModules()) {
            if (module.getModuleName().equals(name))
                return module;
        }

        return null;
    }

    @Override
    public final List<AbstractJavaScriptModule> getLoadedModules() {
        return mModules;
    }

    /**
     * Execute JavaScript code. This method executes JavaScript code directly in the Rhino interpreter. Execution is done in the same thread as the caller
     * thread.
     * 
     * @param data
     *            JavaScript code to be interpreted
     * @return result of code execution
     */
    @WrapToJavaScript
    public final Object execute(final Object data) {
        return getScriptEngine().inject(data);
    }

    /**
     * Terminates script execution immediately. Code following this command will not be executed anymore.
     * 
     * @param value
     *            return code
     */
    @WrapToJavaScript
    public final void exit(final Object value) {
        throw new ExitException(value);
    }

    /**
     * Terminates execution of the current piece of code. Eg forces an include command to return.
     * 
     * @param value
     *            return value
     */
    @WrapToJavaScript
    public final void stepUp(final Object value) {
        throw new BreakException(value);
    }

    /**
     * Include and execute a JavaScript file. Quite similar to execute(Object) a source file is opened and its content is executed. Multiple sources are
     * available: "workspace://" opens a file relative to the workspace root, "project://" opens a file relative to the current project, "file://" opens a file
     * from the file system.
     * 
     * @param filename
     *            name of file to be included
     * @return result of include operation
     * @throws Throwable
     */
    @WrapToJavaScript
    public final Object include(final String filename) throws Throwable {
        final Object currentFile = getScriptEngine().getFileTrace().getTopMostFile();
        Object content = ResourceTools.getContent(filename, currentFile);

        // maybe someone forgot to add the file extension, lets check
        if ((content == null) && (!filename.toLowerCase().endsWith(".js")))
            content = ResourceTools.getContent(filename + ".js", currentFile);

        // execute code
        ScriptResult scriptResult = null;
        if (((content instanceof File) && (((File) content).exists())) || ((content instanceof IFile) && (((IFile) content).exists()))
                || (content instanceof InputStream)) {

            scriptResult = getScriptEngine().inject(content);

            if (scriptResult.hasException())
                throw scriptResult.getException();

        } else
            throw new RuntimeException("cannot resolve \"" + filename + "\"");

        return scriptResult.getResult();
    }

    /**
     * Resolves a {@link File} object from a given string. If called from a script, a relative path will be resolved starting from the calling script location.
     * 
     * @param filename
     *            name of the file to resolve
     * @return file instance. Might return an instance of a non-existing file
     */
    @WrapToJavaScript
    public final File resolveFile(final String filename) {
        final Object currentFile = getScriptEngine().getFileTrace().getTopMostFile();
        final Object file = ResourceTools.getContent(filename, currentFile);

        if (file instanceof IResource)
            return ((IResource) file).getRawLocation().toFile();

        else if (file instanceof File)
            return (File) file;

        return null;
    }

    /**
     * List all available (visible) modules. Returns a list of visible modules. Loaded modules are indicated.
     * 
     * @return string containing module information
     */
    @Override
    @WrapToJavaScript
    public final String listModules() {
        final Map<String, Class<? extends AbstractJavaScriptModule>> modules = getAvailableModules(false);

        final StringBuffer output = new StringBuffer();

        // add header
        output.append("available modules\n=================\n\n");

        // add modules
        for (final String moduleName : modules.keySet()) {
            output.append('\t');

            output.append(moduleName);
            if (findModule(moduleName) != null)
                output.append(" [LOADED]");

            output.append('\n');
        }

        // write to default output
        getScriptEngine().getOutputStream().print(output);

        return output.toString();
    }

    private String getPreExecutionCode(final Method method) {
        final StringBuffer code = new StringBuffer();

        for (final AbstractJavaScriptModule module : getLoadedModules()) {
            if (module instanceof IJavaScriptFunctionModifier)
                code.append(((IJavaScriptFunctionModifier) module).getPreExecutionCode(method));
        }

        return code.toString();
    }

    private String getPostExecutionCode(final Method method) {
        final StringBuffer code = new StringBuffer();

        for (final AbstractJavaScriptModule module : getLoadedModules()) {
            if (module instanceof IJavaScriptFunctionModifier)
                code.append(((IJavaScriptFunctionModifier) module).getPostExecutionCode(method));
        }

        return code.toString();
    }

    /**
     * Load a module. Loading a module generally enhances the JavaScript environment with new functions and variables. If a module was already loaded before, it
     * gets refreshed and moved to the top of the module stack. When a module is loaded, all its dependencies are loaded too. So loading one module might change
     * the whole module stack.
     * 
     * @param name
     *            name of module to load
     * @return loaded module instance
     */
    @Override
    @WrapToJavaScript
    public final AbstractJavaScriptModule loadModule(final String moduleIdentifier) {
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);

        try {
            for (final IConfigurationElement e : config) {
                if (moduleIdentifier.equals(e.getAttribute("name"))) {
                    // try to load dependencies
                    for (final IConfigurationElement element : e.getChildren("dependency")) {
                        if (loadModule(element.getAttribute(CONFIG_DEPENDENCY_NAME)) == null)
                            return null;
                    }

                    AbstractJavaScriptModule module = findModule(moduleIdentifier);
                    if (module == null) {
                        // this is a new module, create instance and add to
                        // modules
                        final Object o = e.createExecutableExtension("class");
                        if (o instanceof AbstractJavaScriptModule) {
                            // initialize module
                            module = (AbstractJavaScriptModule) o;
                            module.initialize(moduleIdentifier, getScriptEngine(), this);
                        }
                    }

                    if (module != null) {
                        // register module in script engine
                        loadModule(module);

                        return module;
                    }
                }
            }
        } catch (final Exception e) {
            // FIXME add log message

            // Logger.error("Could not load module \"" + moduleIdentifier + "\"", e);
        }

        // FIXME add log message
        // Logger.warning("Module \"" + moduleIdentifier + "\" not found");

        return null;
    }

    @SuppressWarnings("unchecked")
    public static final Class<AbstractJavaScriptModule> getModuleClass(final String moduleIdentifier) {
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);

        try {
            for (final IConfigurationElement e : config) {
                if (moduleIdentifier.equals(e.getAttribute("name"))) {
                    final Object o = e.createExecutableExtension("class");
                    if (o instanceof AbstractJavaScriptModule) {
                        return ((Class<AbstractJavaScriptModule>) o.getClass());
                    }
                }
            }
        } catch (final Exception e) {
        }

        return null;
    }

    /**
     * Print to standard output.
     * 
     * @param text
     *            text to write to standard output
     */
    @WrapToJavaScript
    public final void print(final Object text) {
        if (UNDEFINED.equals(text))
            getScriptEngine().getOutputStream().println();

        else
            getScriptEngine().getOutputStream().println(text);
    }

    // FIXME move to rhino bundle
    /**
     * Resolves a jar file and makes its classes available for the ClassLoader.
     * 
     * @param location
     *            location of jar file to register
     */
    // @WrapToJavaScript
    // public final void registerJar(final String location) {
    // final Object currentFile = getScriptEngine().getFileTrace().getTopMostFile();
    // final Object file = ResourceTools.getFile(location, currentFile);
    //
    // final IScriptEngine engine = getScriptEngine();
    // if (engine instanceof RhinoScriptEngine) {
    // if (file != null) {
    // try {
    // if (file instanceof IFile) {
    // final URL url = ((IFile) file).getRawLocationURI().toURL();
    // ((RhinoScriptEngine) engine).registerJar(url);
    // return;
    //
    // } else if (file instanceof File) {
    // final URL url = ((File) file).toURI().toURL();
    // ((RhinoScriptEngine) engine).registerJar(url);
    // return;
    //
    // }
    // } catch (final MalformedURLException e) {
    // // nothing to do
    // }
    // }
    // }
    //
    // throw new RuntimeException("Jar file \"" + location + "\" not found");
    // }

    public void addModuleListener(final IModuleListener listener) {
        mModuleListeners.add(listener);
    }

    public void removeModuleListener(final IModuleListener listener) {
        mModuleListeners.remove(listener);
    }

    private void fireModuleEvent(final AbstractJavaScriptModule module, final int type) {
        for (Object listener : mModuleListeners.getListeners())
            ((IModuleListener) listener).notifyModule(module, type);
    }
}
