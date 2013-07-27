/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-09-23 11:32:45 +0200 (Fr, 23 Sep 2011) $
 *     Revision:       $Revision: 398 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script/src/com/infineon/script/ScriptResult.java $
 *******************************************************************************/

package com.codeandme.scripting;

/**
 * A ScriptResult is a container for a script execution. As execution often occurs detached from the System thread, the result object contains an indicator for
 * pending and finished results. Results itself may contain an object or an exception.
 */
public class ScriptResult {

    /** script execution result. */
    private Object mResult = null;

    /** script execution exception. */
    private Throwable mException = null;

    private boolean mIsDone = false;

    /**
     * Constructor of a pending execution.
     */
    public ScriptResult() {
    }

    /**
     * Constructor for a finished execution.
     * 
     * @param result
     *            result of execution
     */
    public ScriptResult(final Object result) {
        mResult = result;
    }

    /**
     * Verify that this ScriptResult is processed. If the result is ready, execution of the underlying script is done.
     * 
     * @return true when processing is done
     */
    public final synchronized boolean isReady() {
        return mIsDone;
    }

    /**
     * Get the result value stored.
     * 
     * @return result value
     */
    public final synchronized Object getResult() {
        return mResult;
    }

    /**
     * Set the result to be stored.
     * 
     * @param result
     *            object to be stored
     */
    final synchronized void setResult(final Object result) {
        mResult = result;
        mIsDone = true;
        notifyAll();
    }

    /**
     * Set an exception to be stored for this result.
     * 
     * @param e
     *            exception to be stored
     */
    final synchronized void setException(final Throwable e) {
        mException = e;
        mIsDone = true;
        notifyAll();
    }

    /**
     * Get the exception stored within this result.
     * 
     * @return stored exception or null
     */
    public final synchronized Throwable getException() {
        return mException;
    }

    @Override
    public final String toString() {
        if (mException != null)
            return "Exception: " + mException.getLocalizedMessage();

        return ((mResult != null) ? mResult.toString() : "[null]");
    }

    /**
     * Checks whether this result contains an exception.
     * 
     * @return true when this result contains an exception
     */
    public final boolean hasException() {
        return (mException != null);
    }
}
