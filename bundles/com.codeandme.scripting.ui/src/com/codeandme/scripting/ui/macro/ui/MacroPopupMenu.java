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
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/swt/MacroPopupMenu.java $
 *******************************************************************************/

package com.codeandme.scripting.ui.macro.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

import com.codeandme.scripting.ui.Activator;

public class MacroPopupMenu extends AbstractPopupMenu {

    private final List<AbstractPopupItem> mItems = new ArrayList<AbstractPopupItem>();

    public MacroPopupMenu(final String name) {
        super(name);
    }

    public void addItem(final AbstractPopupItem item) {
        mItems.add(item);
    }

    @Override
    protected void populate() {
        for (final AbstractPopupItem item : mItems)
            addPopup(item);
    }

    /**
     * @param segment
     * @return
     */
    public boolean hasSubMenu(final String name) {
        for (final AbstractPopupItem item : mItems) {
            if (item.getDisplayName().equals(name))
                return true;
        }

        return false;
    }

    /**
     * @param segment
     * @return
     */
    public MacroPopupMenu getSubMenu(final String name) {
        for (final AbstractPopupItem item : mItems) {
            if ((item.getDisplayName().equals(name)) && (item instanceof MacroPopupMenu))
                return (MacroPopupMenu) item;
        }

        return null;
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return Activator.getImageDescriptor("com.infineon.script.macro", "/images/macro_folder.gif");
    }
}
