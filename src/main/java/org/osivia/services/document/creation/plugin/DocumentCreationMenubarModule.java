package org.osivia.services.document.creation.plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.api.menubar.MenubarDropdown;
import org.osivia.portal.api.menubar.MenubarGroup;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.menubar.MenubarModule;
import org.osivia.portal.api.urls.PortalUrlType;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoPublicationInfos;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.tag.INuxeoTagService;

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

    /** Nuxeo service. */
    private final INuxeoService nuxeoService;


    public DocumentCreationMenubarModule() {
        menubarService = Locator.findMBean(IMenubarService.class, IMenubarService.MBEAN_NAME);

        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

        // Nuxeo service
        this.nuxeoService = Locator.findMBean(INuxeoService.class, INuxeoService.MBEAN_NAME);
    }

    @Override
    public void customizeDocument(PortalControllerContext portalControllerContext, List<MenubarItem> menubar, DocumentContext documentContext)
            throws PortalException {

        if (documentContext != null && documentContext.getDocument() instanceof Document) {
            if (documentContext.getDocumentType() != null) {
                String typeName = documentContext.getDocumentType().getName();
                Document doc = (Document) documentContext.getDocument();
                String docPath = doc.getPath();
                if (StringUtils.equals(typeName, "Folder")) {
                    NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
                    NuxeoPublicationInfos publicationInfos = ((NuxeoDocumentContext) documentContext).getPublicationInfos();
                    boolean acceptFiles = publicationInfos.getSubtypes().contains("File");

                    if (acceptFiles) {
                        // Tag service
                        INuxeoTagService tagService = nuxeoService.getTagService();
                        // Internationalization bundle
                        Bundle bundle = bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

                        // Add dropdown
                        MenubarDropdown addDropdown = getAddDropdown(portalControllerContext, bundle);

                        // create WORD
                        Element docxIcon;
                        try {
                            docxIcon = tagService.getMimeTypeIcon(nuxeoController, DOCX_MIMETYPE, null);
                        } catch (IOException e) {
                            throw new PortalException(e);
                        }
                        MenubarItem createWordDocument = new MenubarItem("NEW_WORD_DOCUMENT", bundle.getString("NEW_WORD_DOCUMENT"),
                                null, addDropdown, 10, "javascript:;", null, null, null);
                        //createWordDocument.setDivider(true);
                        createWordDocument.setCustomizedIcon(docxIcon);
                        // new word doc modal
                        createWordDocument.getData().put("target", "#osivia-modal");
                        createWordDocument.getData().put("load-url", getDocumentWordCreationPortletUrl(nuxeoController, docPath));
                        menubar.add(createWordDocument);

                        // create EXCEL
                        Element xlsxIcon;
                        try {
                            xlsxIcon = tagService.getMimeTypeIcon(nuxeoController, XLSX_MIMETYPE, null);
                        } catch (IOException e) {
                            throw new PortalException(e);
                        }
                        MenubarItem createExcelDocument = new MenubarItem("NEW_EXCEL_DOCUMENT", bundle.getString("NEW_EXCEL_DOCUMENT"),
                                null, addDropdown, 11, "#", null, null, null);
                        createExcelDocument.setCustomizedIcon(xlsxIcon);
                        // new excel doc modal
                        createExcelDocument.getData().put("target", "#osivia-modal");
                        createExcelDocument.getData().put("load-url", getDocumentExcelCreationPortletUrl(nuxeoController, docPath));
                        menubar.add(createExcelDocument);

                        // create POWERPOINT
                        Element pptxIcon;
                        try {
                            pptxIcon = tagService.getMimeTypeIcon(nuxeoController, PPTX_MIMETYPE, null);
                        } catch (IOException e) {
                            throw new PortalException(e);
                        }
                        MenubarItem createPowerpointDocument = new MenubarItem("NEW_POWERPOINT_DOCUMENT", bundle.getString("NEW_POWERPOINT_DOCUMENT"),
                                null, addDropdown, 12, "#", null, null, null);
                        createPowerpointDocument.setCustomizedIcon(pptxIcon);
                        // new ppt doc modal
                        createPowerpointDocument.getData().put("target", "#osivia-modal");
                        createPowerpointDocument.getData().put("load-url", getDocumentPowerpointCreationPortletUrl(nuxeoController, docPath));
                        menubar.add(createPowerpointDocument);
                    }
                }
            }
        }
    }

    /**
     * Retrieve or build the add menu dropdown
     *
     * @param portalControllerContext
     * @param bundle
     * @return
     */
    private MenubarDropdown getAddDropdown(PortalControllerContext portalControllerContext, Bundle bundle) {
        MenubarDropdown addDropdown = menubarService.getDropdown(portalControllerContext, "ADD");
        if (addDropdown == null) {
            addDropdown = new MenubarDropdown("ADD", bundle.getString("ADD"), "halflings halflings-plus", MenubarGroup.CMS, 2);
            this.menubarService.addDropdown(portalControllerContext, addDropdown);
        }
        return addDropdown;
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
    public void customizeSpace(PortalControllerContext portalControllerContext, List<MenubarItem> menubar, DocumentContext spaceDocumentContext)
            throws PortalException {

        
    }


}
