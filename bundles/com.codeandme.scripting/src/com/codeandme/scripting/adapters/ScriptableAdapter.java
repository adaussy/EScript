package com.codeandme.scripting.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;

import com.codeandme.scripting.IScriptable;

public class ScriptableAdapter implements IAdapterFactory {

    @Override
    public final Object getAdapter(final Object adaptableObject, final Class adapterType) {
        if (adapterType.equals(IScriptable.class)) {
            if (adaptableObject instanceof IFile) {
                return new IScriptable() {

                    @Override
                    public InputStream getSourceCode() throws Exception {
                        return ((IFile) adaptableObject).getContents();
                    }
                };
            }

            if (adaptableObject instanceof File) {
                return new IScriptable() {

                    @Override
                    public InputStream getSourceCode() throws Exception {
                        return new FileInputStream((File) adaptableObject);
                    }
                };
            }
        }

        return null;
    }

    @Override
    public final Class[] getAdapterList() {
        return new Class[] { IScriptable.class };
    }
}
