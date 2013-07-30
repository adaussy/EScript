package com.codeandme.scripting.urlhandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

public class WorkspaceURLConnection extends URLConnection {

    protected WorkspaceURLConnection(final URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {

        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getURL().getHost() + getURL().getFile()));
        try {
            return file.getContents();
        } catch (CoreException e) {
            throw new IOException(e);
        }
    }

}
