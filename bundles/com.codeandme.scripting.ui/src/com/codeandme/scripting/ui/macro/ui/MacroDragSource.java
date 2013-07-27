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
 *     Head URL:       $URL: https://grzw2b4ph2j.eu.infineon.com/svn/Eclipse_RCP/trunk/bundles/com.infineon.script.macro/src/com/infineon/script/macro/internal/dnd/MacroDragSource.java $
 *******************************************************************************/
package com.codeandme.scripting.ui.macro.ui;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TreeDragSourceEffect;

/**
 * Source for a macro D&D event.
 */
public class MacroDragSource extends TreeDragSourceEffect {

    private final TreeViewer mTree;

    /**
     * Constructor. Gets its macros from a tree selection.
     * 
     * @param tree
     *            tree to read from
     */
    public MacroDragSource(final TreeViewer tree) {
        super(tree.getTree());
        mTree = tree;
    }

    /**
     * Does everything to add drag support to a given tree.
     * 
     * @param treeViewer
     *            tree to add drag support to
     */
    public static final void addDragSupport(final TreeViewer treeViewer) {
        final DragSource source = new DragSource(treeViewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        source.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer() });
        source.addDragListener(new MacroDragSource(treeViewer));
    }

    @Override
    public final void dragSetData(final DragSourceEvent event) {
        LocalSelectionTransfer.getTransfer().setSelection(mTree.getSelection());
    }
}
