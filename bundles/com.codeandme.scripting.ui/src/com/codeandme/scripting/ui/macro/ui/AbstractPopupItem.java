/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-05-17 13:05:43 +0200 (Di, 17 Mai 2011) $
 *     Revision:       $Revision: 303 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.swt/src/com/infineon/swt/AbstractPopupItem.java $
 *******************************************************************************/

package com.codeandme.scripting.ui.macro.ui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

public abstract class AbstractPopupItem {

	public IContributionItem getContribution(final IServiceLocator serviceLocator) {
		final CommandContributionItemParameter contributionParameter = getContributionParameter();
		contributionParameter.serviceLocator = serviceLocator;
		contributionParameter.label = getDisplayName();
		contributionParameter.visibleEnabled = true;

		return new CommandContributionItem(contributionParameter);
	}

	public boolean isVisible() {
		return true;
	}

	public abstract CommandContributionItemParameter getContributionParameter();

	public abstract String getDisplayName();
}
