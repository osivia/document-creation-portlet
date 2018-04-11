package org.osivia.services.document.creation.portlet.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.services.document.creation.plugin.DocumentCreationMenubarModule;
import org.osivia.services.document.creation.plugin.DocumentCreationPlugin;
import org.osivia.services.document.creation.portlet.command.UploadFileCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletConfigAware;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.liveedit.OnlyofficeLiveEditHelper;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;

/**
 * DocumentCreationController
 *
 * @author dorian
 */
@Controller
@RequestMapping(value = "VIEW")
public class DocumentCreationController extends CMSPortlet implements PortletContextAware, PortletConfigAware {

    private static final String BLANK_DOCX_PATH = "/WEB-INF/classes/blank.docx";

    private static final String BLANK_XLSX_PATH = "/WEB-INF/classes/blank.xlsx";

    private static final String BLANK_PPTX_PATH = "/WEB-INF/classes/blank.pptx";

    private static final String DEFAULT_VIEW = "view";

    /** Bundle factory. */
    private final IBundleFactory bundleFactory;

    /** Portlet context. */
    private PortletContext portletContext;
    /** Portlet config. */
    private PortletConfig portletConfig;

    /**
     * Default constructor.
     */
    public DocumentCreationController() {
        super();
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }

    /**
     * Post-construct.
     *
     * @throws PortletException
     */
    @PostConstruct
    public void postConstruct() throws PortletException {
        super.init(portletConfig);
    }

    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortletException {
        request.setAttribute("error", request.getParameter("error"));
        return DEFAULT_VIEW;
    }

    @ModelAttribute(value = "docType")
    public String getDocType(PortletRequest request, PortletResponse response) {
        PortalWindow window = WindowFactory.getWindow(request);
        return window.getProperty(DocumentCreationMenubarModule.DOC_TYPE_WINDOW_PARAM);
    }

    @ModelAttribute(value = "docExt")
    public String getDocExt(PortletRequest request, PortletResponse response) {
        PortalWindow window = WindowFactory.getWindow(request);
        String docType = window.getProperty(DocumentCreationMenubarModule.DOC_TYPE_WINDOW_PARAM);
        return getExtensionByDocType(docType);
    }

    @ModelAttribute(value = "docIcon")
    public String getDocIcon(PortletRequest request, PortletResponse response) {
        PortalWindow window = WindowFactory.getWindow(request);
        String docType = window.getProperty(DocumentCreationMenubarModule.DOC_TYPE_WINDOW_PARAM);
        return DocumentDAO.getInstance().getIcon(docType);
    }

    @ModelAttribute(value = "modalTitle")
    public String getModalTitle(PortletRequest request, PortletResponse response) {
        PortalWindow window = WindowFactory.getWindow(request);
        String docType = window.getProperty(DocumentCreationMenubarModule.DOC_TYPE_WINDOW_PARAM);
        return getDocTypeKey(docType);
    }

    private String getDocTypeKey(String docType) {
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.DOCX_MIMETYPE)) {
            return "NEW_WORD_DOCUMENT";
        }
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.XLSX_MIMETYPE)) {
            return "NEW_EXCEL_DOCUMENT";
        }
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.PPTX_MIMETYPE)) {
            return "NEW_POWERPOINT_DOCUMENT";
        }
        return "NEW_WORD_DOCUMENT";
    }

    private InputStream getBlankStreamByDocType(String docType) {
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.DOCX_MIMETYPE)) {
            return portletContext.getResourceAsStream(BLANK_DOCX_PATH);
        }
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.XLSX_MIMETYPE)) {
            return portletContext.getResourceAsStream(BLANK_XLSX_PATH);
        }
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.PPTX_MIMETYPE)) {
            return portletContext.getResourceAsStream(BLANK_PPTX_PATH);
        }
        return portletContext.getResourceAsStream(BLANK_DOCX_PATH);
    }

    private String getExtensionByDocType(String docType) {
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.DOCX_MIMETYPE)) {
            return ".docx";
        }
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.XLSX_MIMETYPE)) {
            return ".xlsx";
        }
        if (StringUtils.equals(docType, DocumentCreationMenubarModule.PPTX_MIMETYPE)) {
            return ".pptx";
        }
        return ".docx";
    }

    @ActionMapping(name = "newDoc")
    public void newWordDoc(ActionRequest request, ActionResponse response) throws PortletException {

        NuxeoController nuxeoController = new NuxeoController(request, response, getPortletContext());
        Bundle bundle = bundleFactory.getBundle(request.getLocale());
        PortalWindow window = WindowFactory.getWindow(request);

        Document createdDocument = null;
        try {

            String docType = window.getProperty(DocumentCreationMenubarModule.DOC_TYPE_WINDOW_PARAM);
            String path = window.getProperty(Constants.WINDOW_PROP_URI);

            if (StringUtils.isNotEmpty(path)) {

                // Computed path
                path = nuxeoController.getComputedPath(path);
                NuxeoDocumentContext documentContext = nuxeoController.getDocumentContext(path);
                Document currentDocument = documentContext.getDoc();
                nuxeoController.setCurrentDoc(currentDocument);

                // the new document name
                String newDocName = request.getParameter("newDocName");

                if (StringUtils.isNotBlank(newDocName)) {
                    String newDocExtension = getExtensionByDocType(docType);

                    // the new document sample
                    InputStream blankStreamByDocType = getBlankStreamByDocType(docType);

                    // create document from sample
                    UploadFileCommand uploadFileCommand = new UploadFileCommand(blankStreamByDocType, newDocName + newDocExtension, docType,
                            currentDocument.getId());
                    createdDocument = (Document) nuxeoController.executeNuxeoCommand(uploadFileCommand);
                    addNotification(nuxeoController.getPortalCtx(), "NEW_DOC_CREATED_SUCCESS", NotificationsType.SUCCESS);
                } else {
                    response.setRenderParameter("error", getBundleFactory().getBundle(request.getLocale()).getString("NEW_DOC_TITLE_REQUIRED"));
                }
            }
        } catch (Exception e) {
            addNotification(nuxeoController.getPortalCtx(), "NEW_DOC_CREATED_ERROR", NotificationsType.ERROR);
            throw new PortletException(e);
        }

        if(createdDocument!=null) {
            // si plugin onlyoffice installé
            boolean isOnlyofficeRegistered = nuxeoController.getNuxeoCMSService().getCMSCustomizer().getCustomizationService().isPluginRegistered(OnlyofficeLiveEditHelper.ONLYOFFICE_PLUGIN_NAME)        ;
            if (isOnlyofficeRegistered) {
                // rediriger vers onlyoffice avec path du doc créé
                try {
                    Map<String, String> windowProperties = new HashMap<>();
                    windowProperties.put(Constants.WINDOW_PROP_URI, createdDocument.getPath());
                    windowProperties.put("osivia.hideTitle", "1");
                    windowProperties.put("osivia.onlyoffice.withLock", Boolean.TRUE.toString());
                    windowProperties.put(InternalConstants.PROP_WINDOW_TITLE, bundle.getString("ONLYOFFICE_EDIT"));

                    String onlyofficePortlerUrl = nuxeoController.getPortalUrlFactory().getCMSUrl(nuxeoController.getPortalCtx(), null,
                            createdDocument.getPath(), null, null, DocumentCreationPlugin.ONLYOFFICE_DISPLAYCONTEXT, null, null, null, null);

                    response.sendRedirect(onlyofficePortlerUrl);
                } catch (IOException e) {
                    throw new PortletException(e);
                }
            }
        }
    }

    @Override
    public void setPortletConfig(PortletConfig portletConfig) {
        this.portletConfig = portletConfig;
    }

    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }
}
