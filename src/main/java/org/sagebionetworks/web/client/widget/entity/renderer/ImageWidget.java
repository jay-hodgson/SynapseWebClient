package org.sagebionetworks.web.client.widget.entity.renderer;

import static com.google.common.util.concurrent.Futures.getDone;
import static com.google.common.util.concurrent.Futures.whenAllComplete;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.sagebionetworks.repo.model.EntityBundle.ENTITY;
import static org.sagebionetworks.repo.model.EntityBundle.FILE_HANDLES;

import java.util.List;
import java.util.Map;

import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.repo.model.FileEntity;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.repo.model.file.FileResult;
import org.sagebionetworks.repo.model.file.PreviewFileHandle;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.SynapseJavascriptClient;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.utils.FutureUtils;
import org.sagebionetworks.web.client.widget.WidgetRendererPresenter;
import org.sagebionetworks.web.client.widget.asynch.PresignedURLAsyncHandler;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.shared.WidgetConstants;
import org.sagebionetworks.web.shared.WikiPageKey;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ImageWidget implements ImageWidgetView.Presenter, WidgetRendererPresenter {
	
	private ImageWidgetView view;
	private Map<String,String> descriptor;
	AuthenticationController authenticationController;
	public static final String MAX_WIDTH_NONE = "max-width-none";
	PresignedURLAsyncHandler presignedURLAsyncHandler;
	SynapseJavascriptClient jsClient;
	SynapseAlert synAlert;
	WikiPageKey wikiKey;
	
	@Inject
	public ImageWidget(ImageWidgetView view, 
			AuthenticationController authenticationController,
			PresignedURLAsyncHandler presignedURLAsyncHandler,
			SynapseJavascriptClient jsClient,
			SynapseAlert synAlert) {
		this.view = view;
		this.authenticationController = authenticationController;
		this.presignedURLAsyncHandler = presignedURLAsyncHandler;
		this.jsClient = jsClient;
		this.synAlert = synAlert;
		view.setPresenter(this);
		view.setSynAlert(synAlert);
	}
	
	@Override
	public void handleLoadingError(String error) {
		synAlert.showError(error);
	}
	
	private void loadFromFileHandleAssociation(FileHandleAssociation fha, FileHandleAssociation previewFha) {
		ListenableFuture<FileResult> fileResultFuture;
		ListenableFuture<FileResult> previewFileResultFuture;
		fileResultFuture = presignedURLAsyncHandler.getFileResult(fha);
		if (previewFha != null) {
			previewFileResultFuture = presignedURLAsyncHandler.getFileResult(previewFha);
		} else {
			previewFileResultFuture = FutureUtils.getDoneFuture(new FileResult());
		}
		
		FluentFuture.from(whenAllComplete(fileResultFuture, previewFileResultFuture)
				.call(() -> {
							// Retrieve the resolved values from the futures
							FileResult fileResult = getDone(fileResultFuture);
							FileResult previewFileResult = getDone(previewFileResultFuture);
							
							view.configure(fileResult.getPreSignedURL(),
									previewFileResult.getPreSignedURL(),
									descriptor.get(WidgetConstants.IMAGE_WIDGET_FILE_NAME_KEY),
									descriptor.get(WidgetConstants.IMAGE_WIDGET_SCALE_KEY),
									descriptor.get(WidgetConstants.IMAGE_WIDGET_ALIGNMENT_KEY),
									descriptor.get(WidgetConstants.IMAGE_WIDGET_SYNAPSE_ID_KEY), 
									authenticationController.isLoggedIn());
							
							return null;
						},
						directExecutor())
				).catching(
						Throwable.class,
						e -> {
							synAlert.handleException(e);
							return null;
						},
						directExecutor()
				);
	}
	
	@Override
	public void configure(final WikiPageKey wikiKey, final Map<String, String> widgetDescriptor, Callback widgetRefreshRequired, Long wikiVersionInView) {
		this.descriptor = widgetDescriptor;
		this.wikiKey = wikiKey;
		String synapseId = descriptor.get(WidgetConstants.IMAGE_WIDGET_SYNAPSE_ID_KEY);
		Long version = null;
		if (descriptor.containsKey(WidgetConstants.WIDGET_ENTITY_VERSION_KEY)) {
			version = Long.parseLong(descriptor.get(WidgetConstants.WIDGET_ENTITY_VERSION_KEY));
		}
		synAlert.clear();		
		if (synapseId != null) {
			// get the file entity
			int mask = ENTITY | FILE_HANDLES;
			jsClient.getEntityBundleForVersion(synapseId, version, mask, new AsyncCallback<EntityBundle>() {
				@Override
				public void onSuccess(EntityBundle entityBundle) {
					if (entityBundle.getEntity() instanceof FileEntity) {
						FileEntity file = (FileEntity)entityBundle.getEntity();
						PreviewFileHandle preview = DisplayUtils.getPreviewFileHandle(entityBundle);
						FileHandleAssociation previewFha = null;
						if (preview != null) {
							// immediately load from the preview
							previewFha = new FileHandleAssociation();
							previewFha.setAssociateObjectId(file.getId());
							previewFha.setAssociateObjectType(FileHandleAssociateType.FileEntity);
							previewFha.setFileHandleId(preview.getId());
						}
						
						FileHandleAssociation fha = new FileHandleAssociation();
						fha.setAssociateObjectId(file.getId());
						fha.setAssociateObjectType(FileHandleAssociateType.FileEntity);
						fha.setFileHandleId(file.getDataFileHandleId());
						loadFromFileHandleAssociation(fha, previewFha);
					} else {
						synAlert.showError("Synapse ID is not a File: " + entityBundle.getEntity().getId());
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					synAlert.handleException(caught);
				}
			});
		} else {
			// it's a wiki attachment
			jsClient.getWikiAttachmentFileHandles(wikiKey, wikiVersionInView, new AsyncCallback<List<FileHandle>>() {
				public void onSuccess(List<FileHandle> fileHandles) {
					FileHandle targetFileHandle = null;
					String fileName = descriptor.get(WidgetConstants.IMAGE_WIDGET_FILE_NAME_KEY);
					for (FileHandle fileHandle : fileHandles) {
						if (fileName.equals(fileHandle.getFileName())) {
							targetFileHandle = fileHandle;
							break;
						}
					}
					if (targetFileHandle != null) {
						FileHandleAssociation fha = new FileHandleAssociation();
						fha.setAssociateObjectId(wikiKey.getWikiPageId());
						fha.setAssociateObjectType(FileHandleAssociateType.WikiAttachment);
						fha.setFileHandleId(targetFileHandle.getId());
						loadFromFileHandleAssociation(fha, null);
					} else {
						synAlert.showError("Wiki attachment not found: " + fileName);
					}
				};
				@Override
				public void onFailure(Throwable caught) {
					synAlert.handleException(caught);
				}
			});
		}
		
		String responsiveValue = descriptor.get(WidgetConstants.IMAGE_WIDGET_RESPONSIVE_KEY);
		if (responsiveValue != null && !Boolean.parseBoolean(responsiveValue)) {
			view.addStyleName(MAX_WIDTH_NONE);
		}
		//set up view based on descriptor parameters
		descriptor = widgetDescriptor;
	}
	
	@SuppressWarnings("unchecked")
	public void clearState() {
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

		/*
	 * Private Methods
	 */
}
