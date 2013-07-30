/*******************************************************************************
 * Copyright (c) 2013 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/
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
