package com.codeandme.scripting.service;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.IScriptEngineLaunchExtension;
import com.codeandme.scripting.IScriptService;

public class ScriptService implements IScriptService {

    private static final String PRIORITY = "priority";
    private static final String EXTENSION_MODULE_ID = "com.codeandme.scripting.language";
    private static final String SUPPORTED_SCRIPT_TYPE = "supportedScriptType";
    private static final String TYPE = "type";
    private static final String ENGINE = "engine";
    private static final String LAUNCH_EXTENSION = "launchExtension";
    private static final String ID = "id";

    private static ScriptService mInstance = null;

    public static ScriptService getInstance() {
        if (mInstance == null)
            mInstance = new ScriptService();

        return mInstance;
    }

    private ScriptService() {
    }

    @Override
    public IScriptEngine createEngine(final String scriptType) {
        return createEngine(scriptType, null);
    }

    @Override
    public IScriptEngine createEngine(final String scriptType, final String engineID) {
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_MODULE_ID);

        // find first engine supporting type
        if (scriptType != null) {
            IConfigurationElement candidate = null;
            int candidatePriority = -1;

            for (final IConfigurationElement e : config) {
                if (ENGINE.equals(e.getName())) {
                    if ((engineID == null) || (engineID.equals(e.getAttribute(ID)))) {

                        for (final IConfigurationElement child : e.getChildren(SUPPORTED_SCRIPT_TYPE)) {
                            if (scriptType.equals(child.getAttribute(TYPE))) {
                                // engine found, check its priority
                                int priority;
                                try {
                                    priority = Integer.parseInt(e.getAttribute(PRIORITY).toString());
                                } catch (Throwable e1) {
                                    priority = 0;
                                }

                                if (priority > candidatePriority) {
                                    // higher priority detected
                                    candidate = e;
                                    candidatePriority = priority;
                                }
                            }
                        }
                    }
                }
            }

            if (candidate != null)
                return createEngine(candidate);
        }

        return null;
    }

    private static IScriptEngine createEngine(final IConfigurationElement e) {
        try {
            final Object engine = e.createExecutableExtension("class");
            if (engine instanceof IScriptEngine) {

                ((IScriptEngine) engine).setEngineID(e.getAttribute(ID));

                // engine loaded, now load launch extensions
                for (final IScriptEngineLaunchExtension extension : getLaunchExtensions())
                    extension.createEngine((IScriptEngine) engine);

                return (IScriptEngine) engine;
            }
            // TODO log error

        } catch (final CoreException e1) {
            // TODO log error
        }

        return null;
    }

    private static List<IScriptEngineLaunchExtension> getLaunchExtensions() {
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
}