package org.osivia.services.document.creation.plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.DocumentContext;

import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.player.Player;
import org.osivia.portal.core.constants.InternalConstants;

import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoPublicationInfos;
import fr.toutatice.portail.cms.nuxeo.api.liveedit.OnlyofficeLiveEditHelper;
import fr.toutatice.portail.cms.nuxeo.api.player.INuxeoPlayerModule;

public class DocumentCreationPlayer implements INuxeoPlayerModule {

    /** bundleFactory */
    private final IBundleFactory bundleFactory;

    private final Locale locale;

    public DocumentCreationPlayer(Locale locale) {
        this.locale = locale;

        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }

    @Override
    public Player getCMSPlayer(NuxeoDocumentContext documentContext) {

 
        if (documentContext.getDocument() != null && StringUtils.equals(documentContext.getDisplayContext(), DocumentCreationPlugin.ONLYOFFICE_DISPLAYCONTEXT)) {

            Bundle bundle = bundleFactory.getBundle(locale);
            Map<String, String> windowProperties = new HashMap<>();

            windowProperties.put(Constants.WINDOW_PROP_URI, documentContext.getDocument().getPath());
            windowProperties.put("osivia.hideTitle", "1");
            windowProperties.put("osivia.onlyoffice.withLock", Boolean.FALSE.toString());
            windowProperties.put(InternalConstants.PROP_WINDOW_TITLE, bundle.getString("ONLYOFFICE_EDIT"));

            Player onlyofficePlayer = new Player();
            onlyofficePlayer.setWindowProperties(windowProperties);
            onlyofficePlayer.setPortletInstance(OnlyofficeLiveEditHelper.ONLYOFFICE_PORTLET_INSTANCE);
            return onlyofficePlayer;
        }
        return null;
    }


}
