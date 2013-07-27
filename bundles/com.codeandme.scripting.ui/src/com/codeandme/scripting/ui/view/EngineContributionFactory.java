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
import com.codeandme.scripting.ui.handler.SpawnShell;

public class EngineContributionFactory extends AbstractContributionFactory {

    private static EngineContributionFactory mInstance = null;

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
    private static EngineContributionFactory getInstance() {
        if (mInstance == null)
            mInstance = new EngineContributionFactory();

        return mInstance;
    }

    /**
     * Private constructor.
     */
    private EngineContributionFactory() {
        super("menu:" + SpawnShell.COMMAND_ID + ".popup", null);
    }

    @Override
    public void createContributionItems(final IServiceLocator serviceLocator, final IContributionRoot additions) {

        IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
        if (scriptService != null) {
            Map<String, String> engines = scriptService.getEngines();

            final List<CommandContributionItemParameter> items = new ArrayList<CommandContributionItemParameter>();
            for (final String name : engines.keySet()) {

                // set parameter for command
                final HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put(SpawnShell.PARAMETER_ID, engines.get(name));

                final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(serviceLocator, null,
                        SpawnShell.COMMAND_ID, CommandContributionItem.STYLE_PUSH);
                contributionParameter.parameters = parameters;
                contributionParameter.label = name;
                contributionParameter.visibleEnabled = true;
                contributionParameter.icon = SWTTools.getImageDescriptor("com.infineon.javascript.ui", "/images/library.png", true);

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
}
