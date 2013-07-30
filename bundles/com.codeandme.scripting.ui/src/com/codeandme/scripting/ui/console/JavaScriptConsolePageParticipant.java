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
package com.codeandme.scripting.ui.console; 
 
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;
 
/** 
 * Creates and manages JavaScript console specific actions 
 *  
 */ 
public class JavaScriptConsolePageParticipant implements IConsolePageParticipant { 
     
    // actions 
    private JavaScriptConsoleRemoveAction fRemoveTerminated; 
    private JavaScriptConsoleRemoveAllAction fRemoveAllTerminated; 
                  
    /* (non-Javadoc) 
     * @see org.eclipse.ui.console.IConsolePageParticipant#init(IPageBookViewPage, IConsole) 
     */ 
    public void init(IPageBookViewPage page, IConsole console) { 
    	IPageBookViewPage fPage = page; 
    	JavaScriptConsole fConsole = (JavaScriptConsole) console; 

        fRemoveTerminated = new JavaScriptConsoleRemoveAction(fConsole); 
        fRemoveAllTerminated = new JavaScriptConsoleRemoveAllAction(); 

        // contribute to toolbar 
        IActionBars actionBars = fPage.getSite().getActionBars(); 
        configureToolBar(actionBars.getToolBarManager()); 

    } 
     
    /* (non-Javadoc) 
     * @see org.eclipse.ui.console.IConsolePageParticipant#dispose() 
     */ 
    public void dispose() {
    	fRemoveTerminated = null;
    	fRemoveAllTerminated = null;
    } 
 
    /** 
     * Contribute actions to the toolbar 
     */ 
    protected void configureToolBar(IToolBarManager mgr) { 
        mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, fRemoveTerminated); 
        mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, fRemoveAllTerminated); 
    } 
 
    /* (non-Javadoc) 
     * @see org.eclipse.ui.console.IConsolePageParticipant#activated() 
     */ 
    public void activated() { } 
 
    /* (non-Javadoc) 
     * @see org.eclipse.ui.console.IConsolePageParticipant#deactivated() 
     */ 
    public void deactivated() {
    	
    }

	@Override
	public Object getAdapter(Class arg0) {
		return null;
	}

}