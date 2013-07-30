package com.codeandme.scripting.ruby.jruby;

import java.io.InputStream;

import org.jruby.embed.ScriptingContainer;

import com.codeandme.scripting.AbstractScriptEngine;

public class JRubyScriptEngine extends AbstractScriptEngine {

    private ScriptingContainer mEngine;

    public JRubyScriptEngine() {
        super("JRuby");
    }

    @Override
    public Object getExecutedFile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void terminateCurrent() {
    }

    @Override
    protected boolean setupEngine() {
        mEngine = new ScriptingContainer();

        mEngine.setOutput(getOutputStream());
        mEngine.setError(getErrorStream());
        mEngine.setInput(getInputStream());

        return true;
    }

    @Override
    protected boolean teardownEngine() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Object execute(final InputStream code, final Object reference, final String fileName) throws Exception {
        return mEngine.runScriptlet(code, fileName);
    }
}
