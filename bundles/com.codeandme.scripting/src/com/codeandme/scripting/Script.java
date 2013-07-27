/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2012-04-05 10:37:20 +0200 (Do, 05 Apr 2012) $
 *     Revision:       $Revision: 645 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script/src/com/infineon/script/Script.java $
 *******************************************************************************/

package com.codeandme.scripting;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

/**
 * Scriptable object. Consists of scriptable data and a result container.
 */
public class Script {
    /** command to be executed. */
    private final Object mCommand;

    /** script result returned from command. */
    private final ScriptResult mResult;

    /**
     * Constructor.
     * 
     * @param command
     *            command (sequence) to be executed
     */
    public Script(final Object command) {
        mCommand = command;
        mResult = new ScriptResult();
    }

    /**
     * Get the scriptable data.
     * 
     * @return scriptable data
     * @throws CoreException
     * @throws FileNotFoundException
     */
    public InputStream getCode() throws Exception {
        if (mCommand instanceof String)
            return new ByteArrayInputStream(((String) mCommand).getBytes());

        if (mCommand instanceof InputStream)
            return (InputStream) mCommand;

        // if we already have a scriptable
        if (mCommand instanceof IScriptable)
            return ((IScriptable) mCommand).getSourceCode();

        // try to adapt to scriptable
        Object scriptable = Platform.getAdapterManager().getAdapter(mCommand, IScriptable.class);
        if (scriptable != null)
            return ((IScriptable) scriptable).getSourceCode();

        // last resort, convert to String
        if (mCommand != null)
            return new ByteArrayInputStream(mCommand.toString().getBytes());

        return null;
    }

    public final Object getCommand() {
        return mCommand;
    }

    /**
     * Get execution result.
     * 
     * @return execution result.
     */
    public final ScriptResult getResult() {
        return mResult;
    }

    /**
     * Set the execution result.
     * 
     * @param result
     *            execution result
     */
    public final void setResult(final Object result) {
        mResult.setResult(result);
    }

    /**
     * Set an execution exception.
     * 
     * @param e
     *            exception
     */
    public final void setException(final Exception e) {
        mResult.setException(e);
    }

    public Object getFile() {
        if ((mCommand instanceof IFile) || (mCommand instanceof File))
            return mCommand;

        return null;
    }
}
