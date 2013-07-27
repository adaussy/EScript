/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2012-07-17 15:58:21 +0200 (Di, 17 Jul 2012) $
 *     Revision:       $Revision: 1307 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.swt/src/com/infineon/swt/AbstractPopupMenu.java $
 *******************************************************************************/

package com.codeandme.scripting.ui.macro.ui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

public abstract class AbstractPopupMenu extends AbstractPopupItem implements IMenuListener {

	private final MenuManager mMenuManager;
	private IServiceLocator mServiceLocator;
	private boolean mSeparatorRequested = false;

	public AbstractPopupMenu(final String name) {
		super();

		mMenuManager = new MenuManager(name, getImageDescriptor(), null);
		mMenuManager.setRemoveAllWhenShown(true);
		mMenuManager.addMenuListener(this);
	}

	@Override
	public final IContributionItem getContribution(final IServiceLocator serviceLocator) {
		mServiceLocator = serviceLocator;
		return mMenuManager;
	}

	@Override
	public final void menuAboutToShow(final IMenuManager manager) {
		mSeparatorRequested = false;
		populate();
	}

	protected final void addPopup(final AbstractPopupItem item) {
		if (item == null)
			mSeparatorRequested = true;

		else if (item.isVisible()) {
			if (mSeparatorRequested) {
				mMenuManager.add(new Separator());
				mSeparatorRequested = false;
			}

			mMenuManager.add(item.getContribution(mServiceLocator));
		}
	}

	protected final void addSeparator() {
		addPopup(null);
	}

	@Override
	public CommandContributionItemParameter getContributionParameter() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return mMenuManager.getMenuText();
	}

	protected ImageDescriptor getImageDescriptor() {
		return null;
	}

	protected abstract void populate();
}
