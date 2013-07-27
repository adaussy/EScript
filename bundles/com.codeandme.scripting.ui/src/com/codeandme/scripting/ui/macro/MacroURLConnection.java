package com.codeandme.scripting.ui.macro;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.ui.PlatformUI;

import com.codeandme.scripting.ui.IMacroService;

public class MacroURLConnection extends URLConnection {

    private final Macro mMacro;

    public MacroURLConnection(final URL url, final Macro macro) {
        super(url);
        mMacro = macro;
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        IMacroService macroService = (IMacroService) PlatformUI.getWorkbench().getService(IMacroService.class);

        if (macroService != null)
            return new ByteArrayInputStream(mMacro.getContent().getBytes());

        throw new IOException("Cannot read from macro");
    }
}
