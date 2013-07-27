package com.codeandme.scripting.javascript;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.url.AbstractURLStreamHandlerService;

public class WorkspaceURLStreamHandlerService extends AbstractURLStreamHandlerService {

    public WorkspaceURLStreamHandlerService() {
    }

    @Override
    public URLConnection openConnection(final URL u) throws IOException {
        return new WorkspaceURLConnection(u);
    }
}
