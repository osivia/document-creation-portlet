package org.osivia.services.document.creation.portlet.command;

import java.io.InputStream;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.nuxeo.ecm.automation.client.model.StreamBlob;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * @author Dorian Licois
 */
public class UploadFileCommand implements INuxeoCommand {

    private final InputStream inputStream;

    private final String filename;

    private final String mimeType;

    private final String parentId;


    public UploadFileCommand(InputStream inputStream, String filename, String mimeType, String parentId) {
        this.inputStream = inputStream;
        this.filename = filename;
        this.mimeType = mimeType;
        this.parentId = parentId;
    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        Blob blob = new StreamBlob(inputStream, filename, mimeType);
        // Operation request
        OperationRequest operationRequest = nuxeoSession.newRequest("FileManager.Import").setInput(blob);
        operationRequest.setContextProperty("currentDocument", parentId);

        return operationRequest.execute();
    }

    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(" : ");
        builder.append(this.parentId);
        builder.append(" : ");
        builder.append(this.filename);
        builder.append(" : ");
        builder.append(this.inputStream);
        builder.append(" : ");
        builder.append(System.currentTimeMillis()); // ID must always be unique for upload
        return builder.toString();
    }
}
