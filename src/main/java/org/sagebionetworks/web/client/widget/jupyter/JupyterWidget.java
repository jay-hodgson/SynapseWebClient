package org.sagebionetworks.web.client.widget.jupyter;

import org.sagebionetworks.repo.model.FileEntity;
import org.sagebionetworks.repo.model.file.ExternalFileHandle;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.RequestBuilderWrapper;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.shared.WebConstants;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class JupyterWidget implements JupyterWidgetView.Presenter {

	JupyterWidgetView view;
	RequestBuilderWrapper requestBuilder;
	SynapseAlert synAlert;
	SynapseJSNIUtils synapseJSNIUtils;
	AuthenticationController authenticationController;
	String url;
	
	@Inject
	public JupyterWidget(JupyterWidgetView view,
			RequestBuilderWrapper requestBuilder,
			SynapseAlert synAlert,
			SynapseJSNIUtils synapseJSNIUtils,
			AuthenticationController authenticationController) {
		this.view = view;
		this.requestBuilder = requestBuilder;
		this.synAlert = synAlert;
		this.synapseJSNIUtils = synapseJSNIUtils;
		this.authenticationController = authenticationController;
		view.setSynAlert(synAlert.asWidget());
		view.setPresenter(this);
	}
	
	public void configure(FileEntity fileEntity, FileHandle fileHandle) {
		synAlert.clear();
		view.setAlertVisible(true);
		url = getDirectDownloadURL(fileEntity, fileHandle);
	}

	public String getDirectDownloadURL(FileEntity fileEntity, FileHandle fileHandle) {
		if (fileHandle instanceof ExternalFileHandle) {
			return ((ExternalFileHandle) fileHandle).getExternalURL();
		} else {
			boolean preview = false;
			boolean proxy = true;
			return DisplayUtils.createFileEntityUrl(synapseJSNIUtils.getBaseFileHandleUrl(), fileEntity.getId(), fileEntity.getVersionNumber(), preview, proxy, authenticationController.getCurrentXsrfToken());	
		}
	}
	
	@Override
	public void showContent() {
		//get the file content
		view.setAlertVisible(false);
		synAlert.clear();
		requestBuilder.configure(RequestBuilder.GET, url);
		requestBuilder.setHeader(WebConstants.CONTENT_TYPE, WebConstants.TEXT_PLAIN_CHARSET_UTF8);
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					int statusCode = response.getStatusCode();
					if (statusCode == Response.SC_OK) {
						String json = response.getText();
						view.loadJupyterNotebook(json);
					} else {
						onError(null, new IllegalArgumentException("Unable to retrieve file content.  Reason: " + response.getStatusText()));
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					synAlert.handleException(exception);
				}
			});
		} catch (final Exception e) {
			synAlert.handleException(e);
		}
	}

	public Widget asWidget() {
		return view.asWidget();
	}
	
	public void setVisible(boolean visible) {
		view.setVisible(visible);
	}
	
}
