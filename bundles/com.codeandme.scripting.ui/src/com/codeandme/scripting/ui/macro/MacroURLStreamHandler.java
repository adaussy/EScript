package com.codeandme.scripting.ui.macro;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.ui.PlatformUI;
import org.osgi.service.url.AbstractURLStreamHandlerService;

import com.codeandme.scripting.ui.IMacroService;

public class MacroURLStreamHandler extends AbstractURLStreamHandlerService {

    public MacroURLStreamHandler() {
    }

    @Override
    public URLConnection openConnection(final URL u) throws IOException {
        IMacroService macroService = (IMacroService) PlatformUI.getWorkbench().getService(IMacroService.class);
        if (macroService != null) {
            Macro macro = macroService.getMacro(u.getHost() + u.getFile());

            if (macro != null)
                return new MacroURLConnection(u, macro);

            throw new IOException("\"" + u.toString() + "\" not found");
        }

        u.getPath();
        // TODO Auto-generated method stub
        return null;
    }

}
