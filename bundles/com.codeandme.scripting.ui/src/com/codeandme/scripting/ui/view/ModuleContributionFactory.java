package com.codeandme.scripting.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;

import com.codeandme.scripting.IScriptService;
import com.codeandme.scripting.modules.ModuleDefinition;
import com.codeandme.scripting.ui.Activator;
import com.codeandme.scripting.ui.handler.LoadModule;

public class ModuleContributionFactory extends AbstractContributionFactory {

    private static ModuleContributionFactory mInstance = null;

    /**
     * Add context menu for these contribution items.
     */
    public static void addContextMenu() {
        final IMenuService menuService = (IMenuService) PlatformUI.getWorkbench().getService(IMenuService.class);
        menuService.addContributionFactory(getInstance());
    }

    /**
     * Get instance of this factory.
     * 
     * @return factory instance
     */
    private static ModuleContributionFactory getInstance() {
        if (mInstance == null)
            mInstance = new ModuleContributionFactory();

        return mInstance;
    }

    /**
     * Private constructor.
     */
    private ModuleContributionFactory() {
        super("menu:" + LoadModule.COMMAND_ID + ".popup", null);
    }

    @Override
    public void createContributionItems(final IServiceLocator serviceLocator, final IContributionRoot additions) {

        IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
        Map<String, ModuleDefinition> modules = scriptService.getAvailableModules();

        final List<CommandContributionItemParameter> items = new ArrayList<CommandContributionItemParameter>();
        for (final ModuleDefinition definition : modules.values()) {
            if (definition.isVisible()) {

                // set parameter for command
                final HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put(LoadModule.PARAMETER_NAME, definition.getName());

                final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(serviceLocator, null,
                        LoadModule.COMMAND_ID, CommandContributionItem.STYLE_PUSH);
                contributionParameter.parameters = parameters;
                contributionParameter.label = definition.getName();
                contributionParameter.visibleEnabled = true;
                contributionParameter.icon = Activator.getImageDescriptor("/images/library.png");

                items.add(contributionParameter);
            }
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
