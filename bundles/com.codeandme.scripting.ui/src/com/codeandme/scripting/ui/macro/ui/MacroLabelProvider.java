/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-05-09 13:21:28 +0200 (Mo, 09 Mai 2011) $
 *     Revision:       $Revision: 279 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/MacroLabelProvider.java $
 *******************************************************************************/

package com.codeandme.scripting.ui.macro.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.codeandme.scripting.ui.Activator;
import com.codeandme.scripting.ui.macro.Macro;

/**
 * Label provider for macro tree view.
 */
public class MacroLabelProvider extends LabelProvider {

    @Override
    public final Image getImage(final Object element) {
        if (element instanceof Macro)
            return Activator.getImage(Activator.PLUGIN_ID, "/images/macro.gif", true);
        else
            return Activator.getImage(Activator.PLUGIN_ID, "/images/macro_folder.gif", true);
    }
}
