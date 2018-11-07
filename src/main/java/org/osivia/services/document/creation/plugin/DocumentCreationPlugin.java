package org.osivia.services.document.creation.plugin;

import java.util.List;

import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.menubar.MenubarModule;
import org.osivia.portal.api.player.IPlayerModule;

import fr.toutatice.portail.cms.nuxeo.api.domain.AbstractPluginPortlet;

/**
 * DocumentCreationPlugin
 *
 * @author dorian
 *
 */
public class DocumentCreationPlugin extends AbstractPluginPortlet {

    private static final String PLUGIN_NAME = "document-creation.plugin";

    public static final String ONLYOFFICE_DISPLAYCONTEXT = "onlyoffice";

    @Override
    protected void customizeCMSProperties(CustomizationContext context) {
        // Menubar Formater
        customizeMenubarModules(context);

        // Players
        customizePlayers(context);
        
    }

    @Override
    protected String getPluginName() {
        return PLUGIN_NAME;
    }

    private void customizeMenubarModules(CustomizationContext context) {
        /// Menubar modules
        List<MenubarModule> modules = getMenubarModules(context);

        MenubarModule mbModule = new DocumentCreationMenubarModule();
        modules.add(mbModule);
    }

    private void customizePlayers(CustomizationContext context) {
        // Players
        List<IPlayerModule> players = getPlayers(context);
        players.add(new DocumentCreationPlayer(context.getLocale()));
    }


}
