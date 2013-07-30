package com.codeandme.scripting.javascript.rhino.ui.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

import com.codeandme.scripting.javascript.modules.AbstractJavaScriptModule;
import com.codeandme.scripting.javascript.rhino.modules.EnvironmentModule;
import com.codeandme.scripting.javascript.rhino.ui.Activator;

public class MolduleContributionFactory extends ExtensionContributionFactory {

    public MolduleContributionFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createContributionItems(final IServiceLocator serviceLocator, final IContributionRoot additions) {

        final Map<String, Class<? extends AbstractJavaScriptModule>> modules = EnvironmentModule.getAvailableModules(false);

        final List<CommandContributionItemParameter> items = new ArrayList<CommandContributionItemParameter>();
        for (final String moduleName : modules.keySet()) {

            // set parameter for command
            final HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put(LoadModule.PARAMETER_NAME, moduleName);

            final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(serviceLocator, null, LoadModule.COMMAND_ID,
                    CommandContributionItem.STYLE_PUSH);
            contributionParameter.parameters = parameters;
            contributionParameter.label = moduleName;
            contributionParameter.visibleEnabled = true;
            contributionParameter.icon = Activator.getImageDescriptor("/images/library.png");

            items.add(contributionParameter);
        }

        // sort contributions
        Collections.sort(items, new Comparator<CommandContributionItemParameter>() {

            @Override
            public int compare(final CommandContributionItemParameter o1, final CommandContributionItemParameter o2) {
                return o1.label.compareTo(o2.label);
            }
        });

        for (final CommandContributionItemParameter item : items)
            additions.addContributionItem(new CommandContributionItem(item), null);
    }
}
