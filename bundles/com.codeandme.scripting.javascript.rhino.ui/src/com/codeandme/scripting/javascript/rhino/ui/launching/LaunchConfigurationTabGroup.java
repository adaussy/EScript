package com.codeandme.scripting.javascript.rhino.ui.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com.codeandme.scripting.javascript.rhino.ui.launching.tabs.EngineTab;
import com.codeandme.scripting.javascript.rhino.ui.launching.tabs.GlobalTab;
import com.codeandme.scripting.javascript.rhino.ui.launching.tabs.LibrariesTab;

public class LaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public LaunchConfigurationTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs(new ILaunchConfigurationTab[] {new GlobalTab(), new LibrariesTab(), new EngineTab(), new CommonTab()});
	}
}
