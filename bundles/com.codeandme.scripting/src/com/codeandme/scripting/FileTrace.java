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
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script/src/com/infineon/script/FileTrace.java $
 *******************************************************************************/

package com.codeandme.scripting;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

/**
 * File trace containing called files and current line numbers. A file trace is quite similar to a stack trace. It just indicates currently executed files down
 * to the current include level.
 */
public class FileTrace implements Iterable<FileTrace.Trace> {

    /**
     * Trace object. Contains trace data for one file.
     */
    public final class Trace {

        /** Line number currently executed. */
        private int mLineNumber = 0;

        /** File URL. */
        private String mUrl = null;

        /** Optionally execution content (for dynamically generated code). */
        private String mContent = "";

        /**
         * Constructor.
         * 
         * @param url
         *            file URL or <code>null</code>
         * @param lineNumber
         *            line number
         * @param content
         *            script content in case that URL is <code>null</code>
         */
        private Trace(final String url, final int lineNumber, final String content) {
            mUrl = url;
            mLineNumber = lineNumber;
            mContent = content;
        }

        public Trace(final Object reference) {
            if (reference instanceof IFile)
                mUrl = ((IFile) reference).getFullPath().toPortableString();

            else if (reference instanceof File)
                mUrl = ((File) reference).getAbsolutePath();

            else if (reference != null)
                mContent = reference.toString();
        }

        /**
         * Get the line number currently executed.
         * 
         * @return line number
         */
        public int getLineNumber() {
            return mLineNumber;
        }

        /**
         * Get the file currently executed.
         * 
         * @return executed file
         */
        public Object getFile() {
            if (mUrl != null) {
                // first try to resolve the file in workspace
                try {
                    return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(mUrl));
                } catch (final Exception e) {
                }

                // didn't work, resolve from system
                return new File(mUrl);
            }

            return null;
        }

        public String getFileName() {
            // if (mUrl != null)
            return mUrl;

            // return "";
        }

        public void setLineNumber(final int lineNumber) {
            mLineNumber = lineNumber;
        }

        public String getContent() {
            return mContent;
        }
    }

    /** Trace stack. */
    private final List<FileTrace.Trace> mTrace = new ArrayList<FileTrace.Trace>();

    public final void push(final String url, final int lineNumber, final String content) {
        mTrace.add(0, new Trace(url, lineNumber, content));
    }

    @Override
    public final Iterator<FileTrace.Trace> iterator() {
        return mTrace.iterator();
    }

    /**
     * Get the whole file trace.
     * 
     * @return trace stack
     */
    public final List<Trace> getTrace() {
        return mTrace;
    }

    public void push(final Object reference) {
        if (reference instanceof Trace)
            mTrace.add(0, (Trace) reference);

        else
            mTrace.add(0, new Trace(reference));
    }

    public Trace pop() {
        if (!mTrace.isEmpty())
            return mTrace.remove(0);

        return null;
    }

    public Trace peek() {
        if (!mTrace.isEmpty())
            return mTrace.get(0);

        return null;
    }

    public List<Object> getFileStack() {
        List<Object> files = new ArrayList<Object>();

        for (Trace trace : getTrace()) {
            Object file = trace.getFile();
            if ((file instanceof IFile) && (((IFile) file).exists()))
                files.add(file);

            else if ((file instanceof File) && (((File) file).exists()))
                files.add(file);
        }

        return files;
    }

    public Object getTopMostFile() {
        List<Object> list = getFileStack();
        return (list.isEmpty()) ? null : list.get(0);
    }
}
