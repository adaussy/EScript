package com.codeandme.scripting.ui.view;

import java.util.ArrayList;
import java.util.Collection;
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

import com.codeandme.scripting.EngineDescription;
import com.codeandme.scripting.IScriptService;
import com.codeandme.scripting.ui.handler.SwitchEngine;

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
        super("menu:" + SwitchEngine.COMMAND_ID + ".popup", null);
    }

    @Override
    public void createContributionItems(final IServiceLocator serviceLocator, final IContributionRoot additions) {

        IScriptService scriptService = (IScriptService) PlatformUI.getWorkbench().getService(IScriptService.class);
        if (scriptService != null) {
            Collection<EngineDescription> engines = scriptService.getEngines();

            final List<CommandContributionItemParameter> items = new ArrayList<CommandContributionItemParameter>();
            for (final EngineDescription description : engines) {

                // set parameter for command
                final HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put(SwitchEngine.PARAMETER_ID, description.getID());

                final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(serviceLocator, null,
                        SwitchEngine.COMMAND_ID, CommandContributionItem.STYLE_PUSH);
                contributionParameter.parameters = parameters;
                contributionParameter.label = description.getName();
                contributionParameter.visibleEnabled = true;
//                contributionParameter.icon = activator.getImageDescriptor("com.infineon.javascript.ui", "/images/library.png", true);

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
