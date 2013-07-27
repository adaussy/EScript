/*******************************************************************************
 * Copyright (c) 2010 Infineon Technologies Austria AG
 *
 * Contributors:
 *     Christian Pontesegger - initial version
 *     
 * Version Control:
 *     Last edited by: $Author: pontesegger $
 *     Date:           $Date: 2011-05-09 13:21:28 +0200 (Mo, 09 Mai 2011) $
 *     Revision:       $Revision: 279 $
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/MacroContentProvider.java $
 *******************************************************************************/

package com.codeandme.scripting.ui.macro.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;

import tools.AdvancedTreeNode;
import tools.AdvancedTreeNodeContentProvider;

import com.codeandme.scripting.ui.IMacroService;
import com.codeandme.scripting.ui.macro.Macro;

/**
 * Content provider for macro tree view.
 */
public class MacroContentProvider extends AdvancedTreeNodeContentProvider<Macro> implements ITreeContentProvider {

    @Override
    public final void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        getRoot().clear();

        IMacroService macroService = (IMacroService) PlatformUI.getWorkbench().getService(IMacroService.class);

        if (macroService != null) {
            for (final Macro macro : macroService.getMacros()) {
                final AdvancedTreeNode<Macro> node = getRoot().findNode(macro.getPath(), true);
                node.addLeaf(macro);
            }
        }
    }
}
