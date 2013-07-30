package com.codeandme.scripting.ruby.jruby;

import java.io.InputStream;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

import com.codeandme.scripting.AbstractScriptEngine;
import com.codeandme.scripting.IModifiableScriptEngine;

public class JRubyScriptEngine extends AbstractScriptEngine implements IModifiableScriptEngine {

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
        mEngine = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);

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

    @Override
    public void setVariable(final String name, final Object content) {
        mEngine.put(name.startsWith("$") ? name : "$" + name, content);
    }

    @Override
    public Object getVariable(final String name) {
        return mEngine.get(name.startsWith("$") ? name : "$" + name);
    }
}
