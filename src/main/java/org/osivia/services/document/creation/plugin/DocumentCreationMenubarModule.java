package org.osivia.services.document.creation.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.api.menubar.MenubarDropdown;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.menubar.MenubarModule;
import org.osivia.portal.api.urls.PortalUrlType;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;

/**
 * @author Dorian Licois
 */
public class DocumentCreationMenubarModule implements MenubarModule {

    public static final String DOC_TYPE_WINDOW_PARAM = "osivia.services.document.creation.type";

    public static final String DOCX_MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static final String XLSX_MIMETYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final String PPTX_MIMETYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    private static final String DOCUMENT_CREATION_PORTLET = "osivia-services-document-creation-portletInstance";


    /** menubarService */
    private final IMenubarService menubarService;

    /** bundleFactory */
    private final IBundleFactory bundleFactory;


    public DocumentCreationMenubarModule() {
        menubarService = Locator.findMBean(IMenubarService.class, IMenubarService.MBEAN_NAME);

        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }

    @Override
    public void customizeDocument(PortalControllerContext portalControllerContext, List<MenubarItem> menubar,
            DocumentContext<? extends EcmDocument> documentContext) throws PortalException {
        if (documentContext != null && documentContext.getDoc() instanceof Document) {
            String typeName = documentContext.getType().getName();
            Document doc = (Document) documentContext.getDoc();
            String docPath = doc.getPath();
            if (StringUtils.equals(typeName, "Folder")) {
                Bundle bundle = bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
                MenubarDropdown addDropdown = menubarService.getDropdown(portalControllerContext, "ADD");

                NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
                // create WORD
                MenubarItem createWordDocument = new MenubarItem("NEW_WORD_DOCUMENT", bundle.getString("NEW_WORD_DOCUMENT"),
                        DocumentDAO.getInstance().getIcon(DOCX_MIMETYPE), addDropdown, 10, "#", null, null, null);
                // new word doc modal
                createWordDocument.getData().put("target", "#osivia-modal");
                createWordDocument.getData().put("load-url", getDocumentWordCreationPortletUrl(nuxeoController, docPath));
                menubar.add(createWordDocument);

                // create EXCEL
                MenubarItem createExcelDocument = new MenubarItem("NEW_EXCEL_DOCUMENT", bundle.getString("NEW_EXCEL_DOCUMENT"),
                        DocumentDAO.getInstance().getIcon(XLSX_MIMETYPE), addDropdown, 11, "#", null, null, null);
                // new excel doc modal
                createExcelDocument.getData().put("target", "#osivia-modal");
                createExcelDocument.getData().put("load-url", getDocumentExcelCreationPortletUrl(nuxeoController, docPath));
                menubar.add(createExcelDocument);

                // create POWERPOINT
                MenubarItem createPowerpointDocument = new MenubarItem("NEW_POWERPOINT_DOCUMENT", bundle.getString("NEW_POWERPOINT_DOCUMENT"),
                        DocumentDAO.getInstance().getIcon(PPTX_MIMETYPE), addDropdown, 12, "#", null, null, null);
                // new ppt doc modal
                createPowerpointDocument.getData().put("target", "#osivia-modal");
                createPowerpointDocument.getData().put("load-url", getDocumentPowerpointCreationPortletUrl(nuxeoController, docPath));
                menubar.add(createPowerpointDocument);
            }
        }
    }

    /**
     * @param nuxeoController
     * @param currentDocPath
     * @return
     * @throws PortalException
     */
    private String getDocumentWordCreationPortletUrl(NuxeoController nuxeoController, String currentDocPath) throws PortalException {
        return getDocumentCreationPortletUrl(nuxeoController, DOCX_MIMETYPE, currentDocPath);
    }

    /**
     * @param nuxeoController
     * @param currentDocPath
     * @return
     * @throws PortalException
     */
    private String getDocumentExcelCreationPortletUrl(NuxeoController nuxeoController, String currentDocPath) throws PortalException {
        return getDocumentCreationPortletUrl(nuxeoController, XLSX_MIMETYPE, currentDocPath);
    }

    /**
     * @param nuxeoController
     * @param currentDocPath
     * @return
     * @throws PortalException
     */
    private String getDocumentPowerpointCreationPortletUrl(NuxeoController nuxeoController, String currentDocPath) throws PortalException {
        return getDocumentCreationPortletUrl(nuxeoController, PPTX_MIMETYPE, currentDocPath);
    }

    /**
     * @param nuxeoController
     * @param docTypeParam
     * @param currentDocPath
     * @return
     * @throws PortalException
     */
    private String getDocumentCreationPortletUrl(NuxeoController nuxeoController, String docTypeParam, String currentDocPath) throws PortalException {
        Map<String, String> windowProperties = new HashMap<>(2);
        windowProperties.put(DOC_TYPE_WINDOW_PARAM, docTypeParam);
        windowProperties.put(Constants.WINDOW_PROP_URI, currentDocPath);

        return nuxeoController.getPortalUrlFactory().getStartPortletUrl(nuxeoController.getPortalCtx(), DOCUMENT_CREATION_PORTLET, windowProperties,
                PortalUrlType.MODAL);
    }

    @Override
    public void customizeSpace(PortalControllerContext arg0, List<MenubarItem> arg1, DocumentContext<? extends EcmDocument> arg2) throws PortalException {
    }
}
