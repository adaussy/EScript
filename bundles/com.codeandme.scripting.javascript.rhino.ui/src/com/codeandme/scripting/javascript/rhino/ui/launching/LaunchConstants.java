package com.codeandme.scripting.javascript.rhino.ui.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

public final class LaunchConstants {

    public static String WORKSPACE_FILE = "Workspace file";

    /** Do not alter name as rhino launcher depends on it. */
    public static String FILE_PATH = "script";

    public static String STARTUP_CODE = "Startup code";
    public static String LIBRARIES = "Libraries";
    public static String OPTIMIZATION_LEVEL = "Optimization level";
    public static String LEGACY_MODE = "Legacy mode";
    public static String GENERATE_LINE_NUMBER_INFORMATION = "Generate line number information";
    public static String STOP_AT_LAUNCH = "Stop at launch";

    public static int DEFAULT_OPTIMIZATION_LEVEL = -1;
    public static final boolean DEFAULT_LEGACY_MODE = false;

    @Deprecated
    private LaunchConstants() {
    }

    public static Collection<File> unserializeLibraries(final String libraries) {
        final String[] elements = libraries.split(File.pathSeparator);
        final List<File> result = new ArrayList<File>(elements.length);
        for (final String element : elements)
            if (!element.trim().isEmpty())
                result.add(new File(element.trim()));

        return result;
    }

    public static Collection<File> getLibraries(final ILaunchConfiguration configuration) throws CoreException {
        final String librariesString = configuration.getAttribute(LIBRARIES, "");
        return unserializeLibraries(librariesString);
    }
}
