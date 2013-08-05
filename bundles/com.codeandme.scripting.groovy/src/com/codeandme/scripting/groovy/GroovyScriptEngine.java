package com.codeandme.scripting.groovy;

import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.codeandme.scripting.AbstractScriptEngine;

public class GroovyScriptEngine extends AbstractScriptEngine {

    private GroovyShell mEngine;

    public GroovyScriptEngine() {
        super("Groovy");
    }

    @Override
    public void terminateCurrent() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean setupEngine() {
        mEngine = new GroovyShell();

        setOutputStream(getOutputStream());
        setErrorStream(getErrorStream());

        return true;
    }

    @Override
    public void setOutputStream(final PrintStream outputStream) {
        if (mEngine != null)
            mEngine.setProperty("out", outputStream);

        super.setOutputStream(outputStream);
    }

    @Override
    public void setErrorStream(final PrintStream errorStream) {
        if (mEngine != null)
            mEngine.setProperty("err", errorStream);

        super.setOutputStream(errorStream);
    }

    @Override
    protected boolean teardownEngine() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected Object execute(final InputStream code, final Object reference, final String fileName) throws Exception {
        InputStreamReader reader = null;
        Object result = null;
        try {
            reader = new InputStreamReader(code);
            if ((fileName == null) || (fileName.isEmpty()))
                result = mEngine.evaluate(reader);

            else
                result = mEngine.evaluate(reader, fileName);

        } catch (Exception e) {
            throw e;
        } finally {
            // gracefully close reader
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
            }
        }

        return result;
    }
}
