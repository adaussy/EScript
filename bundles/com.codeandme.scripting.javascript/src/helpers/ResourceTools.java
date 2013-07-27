/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2012-08-10 13:17:12 +0200 (Fr, 10 Aug 2012) $
 *     Revision:       $Revision: 1456 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.tools/src/com/infineon/tools/ResourceTools.java $
 *******************************************************************************/

package helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

/**
 * Helper class for resource operations.
 */
public final class ResourceTools {

    public static final String LOCATION_WORKSPACE = "workspace";
    public static final String LOCATION_PROJECT = "project";
    public static final String LOCATION_RELATIVE = "relative";
    public static final String LOCATION_FILE = "file";

    public static final int MODE_NONE = 0;
    public static final int MODE_NEW = 1;
    public static final int MODE_FOLDER = 2;
    public static final int MODE_FILE = 4;

    /**
     * Hidden constructor.
     */
    @Deprecated
    private ResourceTools() {
    }

    /**
     * Create a new folder on the workbench.
     * 
     * @param folder
     *            folder to create
     * @param updateFlags
     *            internal resource flags (see {@link IResource})
     * @param local
     *            local flag for files
     * @return <code>true</code> on success
     * @throws CoreException
     *             thrown when folder cannot be created
     */
    public static boolean createFolder(final IFolder folder, final int updateFlags, final boolean local) throws CoreException {

        if (!folder.isAccessible()) {
            final IContainer parent = folder.getParent();

            if (!parent.isAccessible()) {
                if (parent instanceof IFolder)
                    createFolder((IFolder) parent, updateFlags, local);
                else
                    return false;
            }

            folder.create(updateFlags, local, null);
        }

        return true;
    }

    /**
     * Create a new file on the workbench with no content.
     * 
     * @param file
     *            file to create
     * @param derived
     *            derived flag
     * @throws CoreException
     *             thrown when file cannot be created
     */
    public static void createFile(final IFile file, final boolean derived) throws CoreException {
        if (!file.isAccessible()) {
            if (derived)
                file.create(new ByteArrayInputStream(new byte[0]), IResource.DERIVED, new NullProgressMonitor());
            else
                file.create(new ByteArrayInputStream(new byte[0]), IResource.NONE, new NullProgressMonitor());
        }
    }

    /**
     * Compare IResource modification dates to find newer IResource. If an IResource does not exist it is treated as <i>very old</i>, that means older than any
     * existing IResource could be.
     * 
     * @param first
     *            first IResource to check
     * @param second
     *            second IResource
     * @return <0 if first resource is older >0 if first resource is younger 0 if both resources are of same age
     */
    public static long compareResourceModificationTime(final IResource first, final IResource second) {
        final long firstStamp = first.getModificationStamp();
        final long secondStamp = second.getModificationStamp();

        return firstStamp - secondStamp;
    }

    /**
     * Returns an {@link InputStream} for a given resource within a bundle.
     * 
     * @param bundle
     *            qualified name of the bundle to resolve
     * @param path
     *            full path of the file to load
     * @return input stream to resource
     */
    public static InputStream getResourceStream(final String bundle, final String path) {
        String location = Platform.getBundle(bundle).getLocation();
        try {
            if (location.toLowerCase().endsWith(".jar")) {
                // we need to open a jar file
                final int pos = location.indexOf("file:");
                if (pos != -1) {
                    location = location.substring(pos + 5);

                    final JarFile file = new JarFile(location);
                    if (path.startsWith("/"))
                        return file.getInputStream(file.getEntry(path.substring(1)));
                    else
                        return file.getInputStream(file.getEntry(path));
                }

            } else {
                final URL url = Platform.getBundle(bundle).getResource(path);
                return FileLocator.resolve(url).openStream();
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get a specific project from the workspace.
     * 
     * @param name
     *            project name
     * @return project instance or <code>null</code>
     */
    public static IProject getProject(final String name) {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
    }

    /**
     * Get a {@link File} or {@link IFile} from a given string. New schemes are available for files: <i>workspace://</i> resolves to the current workspace
     * location, <i>project://</i> resolves to the project where <i>parent</i> is stored. Absolute paths may be used as long as relative paths. For the latter
     * <i>parent</i> needs to be defined, as it is the starting point for relative file resolution.
     * 
     * @param location
     *            file location to resolve
     * @param parent
     *            parent file or <code>null</code>
     * @return {@link File}, {@link IFile} or <code>null</code>
     */
    public static Object getContent(final String location, final Object parent) {
        if (location != null) {

            URL url;
            try {
                url = new URL(location);

                // so we have a valid URL
                // try to resolve to a file
                try {
                    File file = new File(url.toURI());
                    if (file != null)
                        return file;
                } catch (Throwable e) {
                }

                // no file, try to get content
                try {
                    return url.openStream();
                } catch (Throwable e) {
                }

            } catch (MalformedURLException e1) {
                // not a (valid) URL
            }

            if (location.startsWith(LOCATION_PROJECT + "://")) {
                // we found a project relative path
                if (parent instanceof IResource)
                    // resolve within project
                    return ((IResource) parent).getProject().getFile(location.substring(LOCATION_PROJECT.length() + 3));

                return null;

            } else {
                if (parent instanceof IResource) {
                    // resolve relative to parent
                    final Path path = new Path(location);
                    if (parent instanceof IContainer)
                        return ((IContainer) parent).getFile(path);
                    else
                        return ((IResource) parent).getParent().getFile(path);

                } else if (parent instanceof File) {
                    final String fullPath = ((File) parent).getParentFile().getPath() + File.separator + location;
                    return new File(fullPath);
                }
            }

            // could not resolve anything, try to simply create a file
            return new File(location);
        }

        // giving up
        return null;
    }

    public static IFolder getFolder(final String location, final IResource parent) {
        if (location != null) {
            final int pos = location.indexOf("://");
            if (pos > 0) {
                final Path path = new Path(location.substring(pos + 3));

                final String locationRoot = location.substring(0, pos).toLowerCase();
                if (LOCATION_WORKSPACE.equals(locationRoot)) {
                    // resolve within workspace
                    return ResourcesPlugin.getWorkspace().getRoot().getFolder(path);

                } else if (LOCATION_PROJECT.equals(locationRoot)) {
                    if (parent != null)
                        // resolve within project
                        return parent.getProject().getFolder(path);

                    return null;
                }

            } else {
                // no absolute location, must be a relative path
                if (parent != null) {
                    // resolve relative to parent
                    final Path path = new Path(location);
                    if (parent instanceof IContainer)
                        return ((IContainer) parent).getFolder(path);
                    else
                        return parent.getParent().getFolder(path);
                }
            }
        }

        // could not resolve yet, try to resolve within workspace
        return ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(location));
    }

    public static String createResourceString(final IFile file, final IResource relative, final String indicator) {
        final StringBuffer result = new StringBuffer();

        if (LOCATION_PROJECT.equals(indicator)) {
            result.append(LOCATION_PROJECT);
            result.append("://");
            result.append(file.getProjectRelativePath().toPortableString());
        }

        return result.toString();
    }
}
