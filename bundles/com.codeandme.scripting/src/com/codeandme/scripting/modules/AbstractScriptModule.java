package com.codeandme.scripting.modules;

import java.util.Map;

import org.eclipse.core.runtime.jobs.Job;

import com.codeandme.scripting.IModifiableScriptEngine;
import com.codeandme.scripting.IScriptEngine;
import com.codeandme.scripting.ScriptResult;
import com.codeandme.scripting.service.ModuleDefinition;
import com.codeandme.scripting.service.ScriptService;

public abstract class AbstractScriptModule implements IScriptModule {
    private String mName = "(undefined)";
    private IScriptEngine mEngine = null;
    private EnvironmentModule mEnvironmentModule = null;

    public AbstractScriptModule() {
        Map<String, ModuleDefinition> modules = ScriptService.getAvailableModules();
        for (ModuleDefinition definition : modules.values()) {
            if (definition.getModuleClassName().equals(this.getClass().getName())) {
                mName = definition.getName();
                break;
            }
        }

        // detect engine
        final Job currentJob = Job.getJobManager().currentJob();
        if (currentJob instanceof IScriptEngine) {
            mEngine = (IScriptEngine) currentJob;

            // find environment module
            if (mEngine instanceof IModifiableScriptEngine) {
                // engine supports direct reading of variables
                Object module = ((IModifiableScriptEngine) mEngine).getVariable(getWrapper().getEnvironmentModuleName());
                if (module instanceof EnvironmentModule) {
                    mEnvironmentModule = (EnvironmentModule) module;
                    mEnvironmentModule.addModule(this);
                }

            } else {
                // need to query engine by executing code
                ScriptResult result = mEngine.inject(getWrapper().getEnvironmentModuleName());
                if (result.getResult() instanceof EnvironmentModule) {
                    mEnvironmentModule = (EnvironmentModule) result.getResult();
                    mEnvironmentModule.addModule(this);
                }
            }
        }
    }

    @Override
    public IScriptEngine getScriptEngine() {
        return mEngine;
    }

    @Override
    public String getModuleName() {
        return mName;
    }

    public IModuleWrapper getWrapper() {
        return BootStrapper.getWrapper(getScriptEngine().getID());
    }
}
