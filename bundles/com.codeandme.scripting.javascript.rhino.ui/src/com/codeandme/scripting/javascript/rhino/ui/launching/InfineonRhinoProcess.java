package com.codeandme.scripting.javascript.rhino.ui.launching;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.wst.jsdt.debug.internal.core.model.JavaScriptDebugTarget;
import org.eclipse.wst.jsdt.debug.internal.rhino.ui.launching.RhinoProcess;
import org.mozilla.javascript.Context;

import com.codeandme.scripting.javascript.rhino.ObservingContextFactory;
import com.codeandme.scripting.javascript.rhino.debugger.IContextConsumer;

public class InfineonRhinoProcess extends RhinoProcess implements IContextConsumer {

    private JavaScriptDebugTarget mTarget;
    private Context mContext;

    /**
     * 
     * @param launch
     *            contains some initial parameters for launch
     * @param name
     * @param errorStreamIn
     * @param outputStreamIn
     * @param inputStreamOut
     * 
     */
    public InfineonRhinoProcess(final ILaunch launch, final String name, final PipedOutputStream inputStreamOut, final PipedInputStream outputStreamIn,
            final PipedInputStream errorStreamIn) {
        super(launch, new Process() {
            @Override
            public int waitFor() throws InterruptedException {
                return 0;
            }

            @Override
            public OutputStream getOutputStream() {
                return inputStreamOut;
            }

            @Override
            public InputStream getInputStream() {
                return outputStreamIn;
            }

            @Override
            public InputStream getErrorStream() {
                return errorStreamIn;
            }

            @Override
            public int exitValue() {
                return 0;
            }

            @Override
            public void destroy() {
            }
        }, name);
    }

    @Override
    public void terminate() throws DebugException {

        if ((mContext.getFactory() instanceof ObservingContextFactory)) {

            ((ObservingContextFactory) mContext.getFactory()).terminate(mContext);
            mTarget.resume();
        }
        super.terminate();
    }

    @Override
    public void setContext(final Context context) {

        mContext = context;
    }

    public void setTarget(final JavaScriptDebugTarget target) {

        mTarget = target;
    }

}
