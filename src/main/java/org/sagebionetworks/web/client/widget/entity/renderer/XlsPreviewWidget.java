package org.sagebionetworks.web.client.widget.entity.renderer;
import static org.sagebionetworks.web.client.ClientProperties.MB;

import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.repo.model.file.FileResult;
import org.sagebionetworks.web.client.ClientProperties;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.resources.ResourceLoader;
import org.sagebionetworks.web.client.widget.asynch.PresignedURLAsyncHandler;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;
import com.google.inject.Inject;

public class XlsPreviewWidget implements IsWidget, HtmlPreviewView.Presenter {
	ResourceLoader resourceLoader;
	XlsPreviewView view;
	PresignedURLAsyncHandler presignedURLAsyncHandler;
	SynapseJSNIUtils jsniUtils;
	SynapseAlert synAlert;
	GWTWrapper gwt;
	FileHandleAssociation fha;
	public static final double MAX_XLS_FILE_SIZE = 6 * MB;
	public static String friendlyMaxFileSize = null;

	@Inject
	public XlsPreviewWidget(
			XlsPreviewView view,
			PresignedURLAsyncHandler presignedURLAsyncHandler,
			SynapseJSNIUtils jsniUtils,
			SynapseAlert synAlert,
			GWTWrapper gwt, 
			ResourceLoader resourceLoader) {
		this.view = view;
		this.presignedURLAsyncHandler = presignedURLAsyncHandler;
		this.jsniUtils = jsniUtils;
		this.synAlert = synAlert;
		this.gwt = gwt;
		this.resourceLoader = resourceLoader;
		view.setSynAlert(synAlert);
		if (friendlyMaxFileSize == null) {
			friendlyMaxFileSize = gwt.getFriendlySize(MAX_XLS_FILE_SIZE, true);
		}
	}
	
	public void configure(String synapseId, FileHandle fileHandle) {
		if (!resourceLoader.isLoaded(ClientProperties.SHEETS_JS)) {
			resourceLoader.requires(ClientProperties.SHEETS_JS, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					synAlert.handleException(caught);
				}
				
				@Override
				public void onSuccess(Void result) {
					configure(synapseId, fileHandle);
				}
			});
			return;
		}
		
		fha = new FileHandleAssociation();
		fha.setAssociateObjectId(synapseId);
		fha.setAssociateObjectType(FileHandleAssociateType.FileEntity);
		fha.setFileHandleId(fileHandle.getId());
		if (fileHandle.getContentSize() != null && fileHandle.getContentSize() < MAX_XLS_FILE_SIZE) {
			refreshContent();
		} else {
			view.setLoadingVisible(false);
			synAlert.showError("The preview was not shown because the size (" + gwt.getFriendlySize(fileHandle.getContentSize().doubleValue(), true) + ") exceeds the maximum preview size (" + friendlyMaxFileSize + ")");
		}
	}
	
	public void refreshContent() {
		view.setLoadingVisible(true);
		presignedURLAsyncHandler.getFileResult(fha, new AsyncCallback<FileResult>() {
			@Override
			public void onSuccess(FileResult fileResult) {
				setPresignedUrl(fileResult.getPreSignedURL());
			}
			
			@Override
			public void onFailure(Throwable ex) {
				view.setLoadingVisible(false);
				synAlert.handleException(ex);
			}
		});
	}
	
	public void setPresignedUrl(String url) {
		synAlert.clear();
		XMLHttpRequest req = gwt.createXMLHttpRequest();
		req.open("GET", url);
		req.setResponseType(ResponseType.ArrayBuffer);
		req.setOnReadyStateChange(xhr -> {
			if (xhr.getReadyState() == 4) { //XMLHttpRequest.DONE=4, posts suggest this value is not resolved in some browsers
				if (xhr.getStatus() == 200) { //OK
					//done
					view.setLoadingVisible(false);
					String html = jsniUtils.xlsToHtml(req.getResponseArrayBuffer());
					html = html.replace("<table>", "<table class=\"markdowntable\">");
					view.setHtml(jsniUtils.sanitizeHtml(html));
				} else {
					view.setLoadingVisible(false);
					synAlert.showError(xhr.getStatusText());
				}
			}
		});

		req.send();
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
	
	@Override
	public void onShowFullContent() {
	}
	
}
