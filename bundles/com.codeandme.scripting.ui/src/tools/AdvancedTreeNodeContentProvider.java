/*******************************************************************************
 * Copyright (c) 2010 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-04-22 12:31:45 +0200 (Fr, 22 Apr 2011) $
 *     Revision:       $Revision: 270 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.swt/src/com/infineon/swt/jface/AdvancedTreeNodeContentProvider.java $
 *******************************************************************************/

package tools;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class AdvancedTreeNodeContentProvider<T> implements ITreeContentProvider {

	private final AdvancedTreeNode<T> mRoot = new AdvancedTreeNode<T>();

	public AdvancedTreeNode<T> getRoot() {
		return mRoot;
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getRoot().getChildren().toArray();
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// nothing to do
	}

	@Override
	public void dispose() {
		// nothing to do
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		if (parentElement instanceof AdvancedTreeNode<?>) {
			if (!((AdvancedTreeNode<T>) parentElement).isLoaded())
				loadNode((AdvancedTreeNode<T>) parentElement);

			return ((AdvancedTreeNode<?>) parentElement).getChildren().toArray();
		}

		return null;
	}

	protected void loadNode(final AdvancedTreeNode<T> element) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Object getParent(final Object element) {
		if (element instanceof AdvancedTreeNode<?>)
			return ((AdvancedTreeNode<?>) element).getParent();

		return AdvancedTreeNode.findParentNode(getRoot(), (T) element);
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof AdvancedTreeNode<?>) {
			if (!((AdvancedTreeNode<T>) element).isLoaded())
				loadNode((AdvancedTreeNode<T>) element);

			return !((AdvancedTreeNode<?>) element).getChildren().isEmpty();
		}

		return false;
	}
}
