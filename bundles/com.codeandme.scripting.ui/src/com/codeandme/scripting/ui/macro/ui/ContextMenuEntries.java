/*******************************************************************************
 * Copyright (c) 2011 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-05-13 14:18:57 +0200 (Fr, 13 Mai 2011) $
 *     Revision:       $Revision: 293 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/ContextMenuEntries.java $
 *******************************************************************************/

package com.codeandme.scripting.ui.macro.ui;

import java.util.HashMap;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

import com.codeandme.scripting.ui.handler.Delete;
import com.codeandme.scripting.ui.handler.Edit;
import com.codeandme.scripting.ui.handler.Rename;
import com.codeandme.scripting.ui.handler.Run;
import com.codeandme.scripting.ui.macro.Macro;

/**
 * Provide context menu entries for macros.
 */
public class ContextMenuEntries extends AbstractContributionFactory {

    /**
     * Constructor.
     * 
     * @param location
     *            location to add factory to.
     */
    public ContextMenuEntries(final String location) {
        super(location, null);
    }

    @Override
    public final void createContributionItems(final IServiceLocator serviceLocator, final IContributionRoot additions) {
        final ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
        if (selection instanceof IStructuredSelection) {
            if (!((IStructuredSelection) selection).isEmpty()) {

                final StringBuffer names = new StringBuffer();
                for (final Object object : ((IStructuredSelection) selection).toArray()) {
                    if (object instanceof Macro)
                        names.append(((Macro) object).getName()).append(';');
                }

                if (names.length() > 0) {
                    names.deleteCharAt(names.length() - 1);
                    final HashMap<String, String> parameters = new HashMap<String, String>();

                    // add "run" entry
                    parameters.put(Run.PARAMETER_NAME, names.toString());
                    final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(serviceLocator, null, Run.COMMAND_ID,
                            CommandContributionItem.STYLE_PUSH);
                    contributionParameter.label = "Run";
                    contributionParameter.visibleEnabled = true;
                    contributionParameter.parameters = parameters;
                    CommandContributionItem contribution = new CommandContributionItem(contributionParameter);
                    additions.addContributionItem(contribution, null);

                    // add separator
                    additions.addContributionItem(new Separator(), null);

                    // add "edit" entry
                    parameters.clear();
                    parameters.put(Edit.PARAMETER_NAME, names.toString());
                    contributionParameter.commandId = Edit.COMMAND_ID;
                    contributionParameter.label = "Edit";
                    contribution = new CommandContributionItem(contributionParameter);
                    additions.addContributionItem(contribution, null);

                    // add "rename" entry
                    parameters.clear();
                    parameters.put(Rename.PARAMETER_NAME, names.toString());
                    contributionParameter.commandId = Rename.COMMAND_ID;
                    contributionParameter.label = "Rename";
                    contribution = new CommandContributionItem(contributionParameter);
                    additions.addContributionItem(contribution, null);

                    // add "delete" entry
                    parameters.clear();
                    parameters.put(Delete.PARAMETER_NAME, names.toString());
                    contributionParameter.commandId = Delete.COMMAND_ID;
                    contributionParameter.label = "Delete";
                    contribution = new CommandContributionItem(contributionParameter);
                    additions.addContributionItem(contribution, null);
                }
            }
        }
    }
}
