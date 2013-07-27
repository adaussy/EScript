/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2012-05-11 10:32:42 +0200 (Fr, 11 Mai 2012) $
 *     Revision:       $Revision: 791 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.javascript/src/com/infineon/javascript/internal/RhinoClassLoader.java $
 *******************************************************************************/

package com.codeandme.scripting.javascript.rhino;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A class loader for the Rhino runtime. This class loader will find classes from javascript and from the RCP. As it needs to use <i>buddy class loading</i>
 * creating new objects might be an expensive operation. Activate this class loader like this:
 * <code>ContextFactory.getGlobal().initApplicationClassLoader(new RhinoClassLoader());</code>
 */
public class RhinoClassLoader extends BundleProxyClassLoader {

    private static Map<RhinoScriptEngine, URLClassLoader> REGISTERED_JARS = new HashMap<RhinoScriptEngine, URLClassLoader>();

    /**
     * Constructor for Rhino class loader.
     */
    public RhinoClassLoader() {
        super(Platform.getBundle("org.mozilla.javascript"), RhinoClassLoader.class.getClassLoader());
    }

    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        // try to load from jars
        final Job currentJob = Job.getJobManager().currentJob();
        final URLClassLoader classLoader = REGISTERED_JARS.get(currentJob);
        if (classLoader != null) {
            Class<?> clazz = classLoader.loadClass(name);
            if (clazz != null)
                return clazz;
        }

        // not found in jars, delegate to eclipse loader
        return super.findClass(name);
    }

    public static void registerURL(final RhinoScriptEngine engine, final URL url) {
        if (!REGISTERED_JARS.containsKey(engine))
            REGISTERED_JARS.put(engine, URLClassLoader.newInstance(new URL[] { url }));

        else {
            List<URL> urls = Arrays.asList(REGISTERED_JARS.get(engine).getURLs());
            urls.add(url);
            REGISTERED_JARS.put(engine, URLClassLoader.newInstance(urls.toArray(new URL[urls.size()])));
        }
    }

    public static void unregisterEngine(final RhinoScriptEngine engine) {
        REGISTERED_JARS.remove(engine);
    }
}
