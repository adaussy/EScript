package com.codeandme.scripting.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.ListenerList;

import com.codeandme.scripting.IModifiableScriptEngine;
import com.codeandme.scripting.service.ModuleDefinition;
import com.codeandme.scripting.service.ScriptService;

/**
 * The RhinoEnvironment provides base functions for all JavaScript interpreters. It is automatically loaded by any interpreter upon startup.
 */
public class EnvironmentModule extends AbstractScriptModule implements IScriptModule {

    public static final String NAME = "Environment";

    private final List<IScriptModule> mModules = new ArrayList<IScriptModule>();

    private final ListenerList mModuleListeners = new ListenerList();

    public EnvironmentModule() {
        super();
    }

    public EnvironmentModule(final String bootstrap) {

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
    @WrapToScript
    public final IScriptModule loadModule(final String moduleIdentifier) {
        boolean reLoaded = false;
        IScriptModule module = findModule(moduleIdentifier);
        if (module == null) {
            // not yet loaded
            Map<String, ModuleDefinition> availableModules = ScriptService.getAvailableModules();

            ModuleDefinition definition = availableModules.get(moduleIdentifier);
            if (definition != null) {
                // module exists

                // load dependencies
                for (String dependencyName : definition.getDependencies()) {
                    if (findModule(dependencyName) == null) {
                        // dependency not loaded yet
                        if (loadModule(dependencyName) == null)
                            // could not load dependency, bail out
                            return null;
                    }
                }

                // register new variable in script engine
                if (getScriptEngine() instanceof IModifiableScriptEngine) {
                    // engine supports direct setting of variables
                    IScriptModule instance = definition.getModuleInstance();
                    if (instance != null)
                        ((IModifiableScriptEngine) getScriptEngine()).setVariable(getRegisteredModuleName(definition.getName()), instance);

                    else
                        // could not create instance, bail out
                        return null;

                } else
                    // engine does not support direct setting of variables, bail out

                    // start DEBUG, might be used for future engines that do not support direct setting of variables, might be deleted savely
                    // String newClass = getWrapper().classInstantiation(definition.getModuleClass(), new String[0]);
                    // String code = getWrapper().getVariableDefinition(getRegisteredModuleName(definition.getName()), newClass);
                    // getScriptEngine().inject(code);
                    // end DEBUG
                    return null;

            } else
                // module does not exist, cannot load
                return null;

        } else {
            // module was loaded before
            reLoaded = true;

            // move module up to first position
            for (IScriptModule existingModule : mModules) {
                if (existingModule.getModuleName().equals(moduleIdentifier)) {
                    mModules.remove(existingModule);
                    addModule(existingModule);
                    break;
                }
            }
        }

        // load wrappers after adding the module to the list. This way a module can add an IScriptFunctionModifier which will be applied to itself
        if ((mModules.isEmpty()) && (NAME.equals(moduleIdentifier))) {
            // environment module is loaded, as it cannot register on itself, help it a little
            createWrappers(this, false);

        } else {
            // some other module is loaded
            createWrappers(mModules.get(0), reLoaded);

            // notify listeners
            fireModuleEvent(module, reLoaded ? IModuleListener.RELOADED : IModuleListener.LOADED);
        }

        return module;
    }

    public static String getRegisteredModuleName(final String moduleName) {
        return "__MOD_" + moduleName;
    }

    // /**
    // * Get a map of all available extension modules. This includes both loaded and unloaded modules. The map contains of paisr
    // *
    // * @return list of extension modules
    // */
    // public static Map<String, Class<? extends IScriptModule>> getAvailableModules(final boolean findHidden) {
    // final Map<String, Class<? extends IScriptModule>> modules = new HashMap<String, Class<? extends IScriptModule>>();
    //
    // final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);
    // for (final IConfigurationElement element : elements) {
    //
    // // only add items marked as visible
    // if (Boolean.parseBoolean(element.getAttribute("visible")) || findHidden) {
    // try {
    // final Object o = element.createExecutableExtension("class");
    // if (o instanceof IScriptModule) {
    // final String name = element.getAttribute("name");
    // modules.put(name, ((IScriptModule) o).getClass());
    // }
    // } catch (final CoreException e) {
    // // FIXME add log message
    // // Logger.warning("Could not load module.", e);
    // }
    // }
    // }
    //
    // return modules;
    // }

    /**
     * Create JavaScript wrapper functions for autoload methods. Adds code of following style: <code>function {name} (a, b, c, ...) {
     * __module.{name}(a, b, c, ...);
     * }</code>
     * 
     * @param module
     *            module instance to create wrappers for
     * @param reload
     *            flag indicating that the module was already loaded
     */
    private void createWrappers(final IScriptModule module, final boolean reload) {
        // script code to inject
        StringBuffer scriptCode = new StringBuffer();

        // use reflection to access methods
        for (final Method method : module.getClass().getMethods()) {
            if (useAutoLoader(method)) {

                String preExecutionCode = getPreExecutionCode(method);
                String postExecutionCode = getPostExecutionCode(method);

                Set<String> methodNames = new HashSet<String>();
                methodNames.add(method.getName());
                methodNames.addAll(Arrays.asList(method.getAnnotation(WrapToScript.class).alias().split(WrapToScript.DELIMITER)));

                String code = getWrapper().createFunctionWrapper(getRegisteredModuleName(module.getModuleName()), method, methodNames,
                        IScriptFunctionModifier.RESULT_NAME, preExecutionCode, postExecutionCode);

                scriptCode.append(code);
                scriptCode.append('\n');
            }
        }

        // use reflection to access static members
        if (!reload) {
            Field[] declaredFields = module.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    if (field.getAnnotation(WrapToScript.class) != null)
                        scriptCode.append(getWrapper().getConstantDefinition(field.getName().toUpperCase(), field));
                }
            }
        }

        // execute code
        getScriptEngine().inject(scriptCode.toString());
    }

    /**
     * Verify that a method is treated by the autoloader. This is the case when the method is marked by an @WrapToJavaScript annotation.
     * 
     * @param method
     *            method to be evaluated
     * @return true when autoloader should handle this method
     */
    private static boolean useAutoLoader(final Method method) {
        return (method.getAnnotation(WrapToScript.class) != null);
    }

    /**
     * Resolves a loaded module and returns the Java instance. Will only query previously loaded modules.
     * 
     * @param name
     *            name of the module to resolve
     */
    @WrapToScript
    public final IScriptModule findModule(final String name) {
        for (final IScriptModule module : getLoadedModules()) {
            if (module.getModuleName().equals(name))
                return module;
        }

        return null;
    }

    public final List<IScriptModule> getLoadedModules() {
        return mModules;
    }

    // /**
    // * Execute JavaScript code. This method executes JavaScript code directly in the Rhino interpreter. Execution is done in the same thread as the caller
    // * thread.
    // *
    // * @param data
    // * JavaScript code to be interpreted
    // * @return result of code execution
    // */
    // @WrapToScript
    // public final Object execute(final Object data) {
    // return getScriptEngine().inject(data);
    // }
    //
    // /**
    // * Terminates script execution immediately. Code following this command will not be executed anymore.
    // *
    // * @param value
    // * return code
    // */
    // @WrapToScript
    // public final void exit(final Object value) {
    // throw new ExitException(value);
    // }
    //
    // /**
    // * Terminates execution of the current piece of code. Eg forces an include command to return.
    // *
    // * @param value
    // * return value
    // */
    // @WrapToScript
    // public final void stepUp(final Object value) {
    // throw new BreakException(value);
    // }
    //
    // /**
    // * Include and execute a JavaScript file. Quite similar to execute(Object) a source file is opened and its content is executed. Multiple sources are
    // * available: "workspace://" opens a file relative to the workspace root, "project://" opens a file relative to the current project, "file://" opens a
    // file
    // * from the file system.
    // *
    // * @param filename
    // * name of file to be included
    // * @return result of include operation
    // * @throws Throwable
    // */
    // @WrapToScript
    // public final Object include(final String filename) throws Throwable {
    // final Object currentFile = getScriptEngine().getFileTrace().getTopMostFile();
    // Object content = ResourceTools.getContent(filename, currentFile);
    //
    // // maybe someone forgot to add the file extension, lets check
    // if ((content == null) && (!filename.toLowerCase().endsWith(".js")))
    // content = ResourceTools.getContent(filename + ".js", currentFile);
    //
    // // execute code
    // ScriptResult scriptResult = null;
    // if (((content instanceof File) && (((File) content).exists())) || ((content instanceof IFile) && (((IFile) content).exists()))
    // || (content instanceof InputStream)) {
    //
    // scriptResult = getScriptEngine().inject(content);
    //
    // if (scriptResult.hasException())
    // throw scriptResult.getException();
    //
    // } else
    // throw new RuntimeException("cannot resolve \"" + filename + "\"");
    //
    // return scriptResult.getResult();
    // }
    //
    // /**
    // * Resolves a {@link File} object from a given string. If called from a script, a relative path will be resolved starting from the calling script
    // location.
    // *
    // * @param filename
    // * name of the file to resolve
    // * @return file instance. Might return an instance of a non-existing file
    // */
    // @WrapToScript
    // public final File resolveFile(final String filename) {
    // final Object currentFile = getScriptEngine().getFileTrace().getTopMostFile();
    // final Object file = ResourceTools.getContent(filename, currentFile);
    //
    // if (file instanceof IResource)
    // return ((IResource) file).getRawLocation().toFile();
    //
    // else if (file instanceof File)
    // return (File) file;
    //
    // return null;
    // }
    //
    // /**
    // * List all available (visible) modules. Returns a list of visible modules. Loaded modules are indicated.
    // *
    // * @return string containing module information
    // */
    // @WrapToScript
    // public final String listModules() {
    // final Map<String, Class<? extends IScriptModule>> modules = getAvailableModules(false);
    //
    // final StringBuffer output = new StringBuffer();
    //
    // // add header
    // output.append("available modules\n=================\n\n");
    //
    // // add modules
    // for (final String moduleName : modules.keySet()) {
    // output.append('\t');
    //
    // output.append(moduleName);
    // if (findModule(moduleName) != null)
    // output.append(" [LOADED]");
    //
    // output.append('\n');
    // }
    //
    // // write to default output
    // getScriptEngine().getOutputStream().print(output);
    //
    // return output.toString();
    // }
    //
    private String getPreExecutionCode(final Method method) {
        final StringBuffer code = new StringBuffer();

        for (final IScriptModule module : getLoadedModules()) {
            if (module instanceof IScriptFunctionModifier)
                code.append(((IScriptFunctionModifier) module).getPreExecutionCode(method));
        }

        return code.toString();
    }

    private String getPostExecutionCode(final Method method) {
        final StringBuffer code = new StringBuffer();

        for (final IScriptModule module : getLoadedModules()) {
            if (module instanceof IScriptFunctionModifier)
                code.append(((IScriptFunctionModifier) module).getPostExecutionCode(method));
        }

        return code.toString();
    }

    // @SuppressWarnings("unchecked")
    // public static final Class<IScriptModule> getModuleClass(final String moduleIdentifier) {
    // final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);
    //
    // try {
    // for (final IConfigurationElement e : config) {
    // if (moduleIdentifier.equals(e.getAttribute("name"))) {
    // final Object o = e.createExecutableExtension("class");
    // if (o instanceof IScriptModule) {
    // return ((Class<IScriptModule>) o.getClass());
    // }
    // }
    // }
    // } catch (final Exception e) {
    // }
    //
    // return null;
    // }
    //
    // /**
    // * Print to standard output.
    // *
    // * @param text
    // * text to write to standard output
    // */
    // @WrapToScript
    // public final void print(final Object text) {
    // if (UNDEFINED.equals(text))
    // getScriptEngine().getOutputStream().println();
    //
    // else
    // getScriptEngine().getOutputStream().println(text);
    // }
    //
    // // FIXME move to rhino bundle
    // /**
    // * Resolves a jar file and makes its classes available for the ClassLoader.
    // *
    // * @param location
    // * location of jar file to register
    // */
    // // @WrapToJavaScript
    // // public final void registerJar(final String location) {
    // // final Object currentFile = getScriptEngine().getFileTrace().getTopMostFile();
    // // final Object file = ResourceTools.getFile(location, currentFile);
    // //
    // // final IScriptEngine engine = getScriptEngine();
    // // if (engine instanceof RhinoScriptEngine) {
    // // if (file != null) {
    // // try {
    // // if (file instanceof IFile) {
    // // final URL url = ((IFile) file).getRawLocationURI().toURL();
    // // ((RhinoScriptEngine) engine).registerJar(url);
    // // return;
    // //
    // // } else if (file instanceof File) {
    // // final URL url = ((File) file).toURI().toURL();
    // // ((RhinoScriptEngine) engine).registerJar(url);
    // // return;
    // //
    // // }
    // // } catch (final MalformedURLException e) {
    // // // nothing to do
    // // }
    // // }
    // // }
    // //
    // // throw new RuntimeException("Jar file \"" + location + "\" not found");
    // // }

    public void addModuleListener(final IModuleListener listener) {
        mModuleListeners.add(listener);
    }

    public void removeModuleListener(final IModuleListener listener) {
        mModuleListeners.remove(listener);
    }

    private void fireModuleEvent(final IScriptModule module, final int type) {
        for (Object listener : mModuleListeners.getListeners())
            ((IModuleListener) listener).notifyModule(module, type);
    }

    void addModule(final IScriptModule module) {
        // check if a module with same name is already registered
        for (IScriptModule mod : mModules) {
            if (mod.getModuleName().equals(module.getModuleName())) {
                // found, remove module as it gets replaced
                mModules.remove(mod);
            }
        }

        mModules.add(0, module);
    }
}
