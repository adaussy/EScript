package com.codeandme.scripting.javascript.rhino.ui.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

public class JavaScriptFileContentProvider extends BaseWorkbenchContentProvider
		implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object element) {
		// remove all non-JS file entries
		List<Object> fixedElements = Arrays.asList(super.getChildren(element));
		List<Object> elements = new ArrayList<Object>(fixedElements);

		for (Object entry : fixedElements) {
			if ((entry instanceof IFile)
					&& !("js".equalsIgnoreCase(((IFile) entry)
							.getFileExtension())))
				elements.remove(entry);
		}

		return elements.toArray();
	}
}
