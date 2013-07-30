package com.codeandme.scripting.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

import com.codeandme.scripting.AbstractScriptEngine;
import com.codeandme.scripting.EngineDescription;
import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineLaunchExtension;
import com.codeandme.scripting.IScriptService;
import com.codeandme.scripting.modules.IModuleWrapper;

public class ScriptService implements IScriptService {

    private static final String EXTENSION_MODULE_ID = "com.codeandme.scripting.language";
    private static final String ENGINE = "engine";
    private static final String LAUNCH_EXTENSION = "launchExtension";
    private static final String MODULE_WRAPPER = "moduleWrapper";
    private static final String ENGINE_ID = "engineID";

    private static ScriptService mInstance = null;

    public static ScriptService getInstance() {
        if (mInstance == null)
            mInstance = new ScriptService();

        return mInstance;
    }

    private ScriptService() {
    }

    @Override
    public IScriptEngine createEngineByID(final String engineID) {
        List<EngineDescription> engines = new ArrayList<EngineDescription>(getEngines());
        for (EngineDescription description : engines) {
            if (description.getID().equals(engineID))
                return createEngine(description);
        }

        return null;
    }

    private IScriptEngine createEngine(final EngineDescription description) {
        try {
            Object engine = description.createEngine();

            if (engine instanceof IScriptEngine) {
                // configure engine
                if (engine instanceof AbstractScriptEngine)
                    ((AbstractScriptEngine) engine).setEngineDescription(description);

                // engine loaded, now load launch extensions
                for (final IScriptEngineLaunchExtension extension : getLaunchExtensions())
                    extension.createEngine((IScriptEngine) engine);

                return (IScriptEngine) engine;
            }
        } catch (CoreException e) {
        }

        return null;
    }

    @Override
    public IScriptEngine createEngine(final String scriptType) {
        List<EngineDescription> engines = new ArrayList<EngineDescription>(getEngines());

        // sort by priority (highest first)
        Collections.sort(engines, new Comparator<EngineDescription>() {

            @Override
            public int compare(final EngineDescription o1, final EngineDescription o2) {
                return o2.getPriority() - o1.getPriority();
            }
        });

        // return first engine where ID matches or (in case no ID is provided)
        // scriptType matches
        for (EngineDescription description : engines) {
            if (description.getSupportedScriptTypes().contains(scriptType)) {
                // engine found, launch
                IScriptEngine engine = createEngine(description);
                if (engine != null)
                    return engine;

                // we could not create engine for some reason, try next one
            }
        }

        return null;
    }

    public static List<IScriptEngineLaunchExtension> getLaunchExtensions() {
        final List<IScriptEngineLaunchExtension> extensions = new LinkedList<IScriptEngineLaunchExtension>();

        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);

        for (final IConfigurationElement e : config) {
            try {
                if (LAUNCH_EXTENSION.equals(e.getName())) {
                    final Object extension = e.createExecutableExtension("class");
                    if (extension instanceof IScriptEngineLaunchExtension)
                        extensions.add((IScriptEngineLaunchExtension) extension);
                }
            } catch (final InvalidRegistryObjectException e1) {
            } catch (final CoreException e1) {
            }
        }

        return extensions;
    }

    @Override
    public Collection<EngineDescription> getEngines() {
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);

        HashSet<EngineDescription> engines = new HashSet<EngineDescription>();
        for (final IConfigurationElement e : config) {
            if (ENGINE.equals(e.getName()))
                engines.add(new EngineDescription(e));
        }

        return engines;
    }

    @Override
    public Collection<IModuleWrapper> getModuleWrappers(final String engineID) {
        final List<IModuleWrapper> wrapper = new LinkedList<IModuleWrapper>();

        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);

        for (final IConfigurationElement e : config) {
            try {
                if (MODULE_WRAPPER.equals(e.getName())) {
                    if (engineID.equals(e.getAttribute(ENGINE_ID))) {

                        final Object extension = e.createExecutableExtension("class");
                        if (extension instanceof IModuleWrapper)
                            wrapper.add((IModuleWrapper) extension);
                    }
                }
            } catch (final InvalidRegistryObjectException e1) {
            } catch (final CoreException e1) {
            }
        }

        return wrapper;
    }
}