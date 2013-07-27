package com.codeandme.scripting.javascript.rhino.debugger;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLoadBreakpoint;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptLoadBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.internal.rhino.debugger.RhinoDebuggerImpl;
import org.eclipse.wst.jsdt.debug.internal.rhino.transport.RhinoTransportService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebuggableScript;

public class InfineonDebuggerImpl extends RhinoDebuggerImpl {

    private String mStep = null;
    private String mIncludeStep = null;
    private boolean mInclude = false;
    private boolean mFirstContinue = true;
    private IFile mFile = null;
    private final boolean mStopAtLaunch;
    private static IJavaScriptLoadBreakpoint allLoadsBreakpoint = null;

    public InfineonDebuggerImpl(final IFile file, final boolean stopAtLaunch, final String address) {
        super(new RhinoTransportService(), address, false, false);
        mFile = file;
        mStopAtLaunch = stopAtLaunch;
    }

    @Override
    public void handleCompilationDone(final Context context, final DebuggableScript script, final String source) {

        // Ignore unnamed script files for the debug process
        if (!script.getSourceName().equals("unnamed script")) {

            if (mStep != null) {
                mIncludeStep = mStep;
                mInclude = true;
            }

            if ((mFirstContinue == true) && (mStopAtLaunch == true)) {

                IJavaScriptLoadBreakpoint breakpoint = null;
                try {
                    HashMap map = new HashMap();
                    map.put(JavaScriptLoadBreakpoint.GLOBAL_SUSPEND, Boolean.TRUE);
                    breakpoint = JavaScriptDebugModel.createScriptLoadBreakpoint(ResourcesPlugin.getWorkspace().getRoot(), -1, -1, map, false);
                    breakpoint.setPersisted(false); // do not persist - https://bugs.eclipse.org/bugs/show_bug.cgi?id=323152
                } catch (DebugException e) {
                    JavaScriptDebugPlugin.log(e);
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (breakpoint != null) {
                    // notify all the targets
                    IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
                    for (int i = 0; i < targets.length; i++) {
                        if (targets[i] instanceof JavaScriptDebugTarget) {
                            ((JavaScriptDebugTarget) targets[i]).breakpointAdded(breakpoint);
                            allLoadsBreakpoint = breakpoint;
                        }
                    }
                }
                mFirstContinue = false;
            } else {

                if (allLoadsBreakpoint != null) {
                    // notify all the targets
                    IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
                    for (int i = 0; i < targets.length; i++) {
                        if (targets[i] instanceof JavaScriptDebugTarget) {
                            ((JavaScriptDebugTarget) targets[i]).breakpointRemoved(allLoadsBreakpoint, null);
                        }
                    }
                    try {
                        allLoadsBreakpoint.delete();
                    } catch (CoreException e) {
                        JavaScriptDebugPlugin.log(e);
                    } finally {
                        allLoadsBreakpoint = null;
                    }
                }
            }

            super.handleCompilationDone(context, script, source);

        }

    }

    @Override
    public synchronized void resume(final Long threadId, String stepType) {
        // Replace the step type, if a included File set it to null
        mStep = stepType;
        if (mInclude) {
            stepType = mIncludeStep;
            mInclude = false;
        }

        super.resume(threadId, stepType);
    }
}
