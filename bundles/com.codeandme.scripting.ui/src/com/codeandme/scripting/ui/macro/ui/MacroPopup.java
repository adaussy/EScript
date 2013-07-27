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
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/swt/MacroPopup.java $
 *******************************************************************************/

package com.codeandme.scripting.ui.macro.ui;

import java.util.HashMap;

import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import com.codeandme.scripting.ui.Activator;
import com.codeandme.scripting.ui.handler.Run;
import com.codeandme.scripting.ui.macro.Macro;

public class MacroPopup extends AbstractPopupItem {

    private final Macro mMacro;

    public MacroPopup(final Macro macro) {
        mMacro = macro;
    }

    @Override
    public CommandContributionItemParameter getContributionParameter() {
        final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(null, null, Run.COMMAND_ID,
                CommandContributionItem.STYLE_PUSH);

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(Run.PARAMETER_NAME, mMacro.getName());

        contributionParameter.parameters = parameters;

        contributionParameter.icon = Activator.getImageDescriptor("com.infineon.script.macro", "/images/macro.gif");

        return contributionParameter;
    }

    @Override
    public String getDisplayName() {
        return mMacro.getBaseName();
    }
}
