package org.sagebionetworks.web.client.widget.table.modal.fileview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils.SelectedHandler;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.widget.entity.browse.EntityFilter;
import org.sagebionetworks.web.client.widget.entity.browse.EntityFinder;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.shared.exceptions.UnknownErrorException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EntityContainerListWidget implements EntityContainerListWidgetView.Presenter, IsWidget {
	EntityFinder finder;
	EntityContainerListWidgetView view;
	SynapseClientAsync synapseClient;
	List<String> entityIds;
	SynapseAlert synAlert;
	boolean canEdit = true;
	@Inject
	public EntityContainerListWidget(EntityContainerListWidgetView view, EntityFinder finder, SynapseClientAsync synapseClient, SynapseAlert synAlert) {
		this.view = view;
		this.finder = finder;
		this.synapseClient = synapseClient;
		this.synAlert = synAlert;
		view.setPresenter(this);
		boolean showVersions = false;
		entityIds = new ArrayList<String>();
		finder.configureMulti(EntityFilter.CONTAINER, showVersions, new SelectedHandler<List<Reference>>() {
			@Override
			public void onSelected(List<Reference> selected) {
				for (Reference ref : selected) {
					onAddProject(ref.getTargetId());
				}
			}
		});
	}
	
	public void configure(List<String> entityContainerIds, boolean canEdit) {
		view.clear();
		this.canEdit = canEdit;
		view.setAddButtonVisible(canEdit);
		view.setNoContainers(entityContainerIds.isEmpty());
		synAlert.clear();
		if (!entityContainerIds.isEmpty()) {
			synapseClient.getEntityHeaderBatch(entityContainerIds, new AsyncCallback<ArrayList<EntityHeader>>() {
				@Override
				public void onFailure(Throwable caught) {
					synAlert.handleException(caught);
				}
			
				@Override
				public void onSuccess(ArrayList<EntityHeader> entityHeaders) {
					for (EntityHeader header : entityHeaders) {
						entityIds.add(header.getId());
						view.addEntity(header.getId(), header.getName(), EntityContainerListWidget.this.canEdit);
					}
				}
			});
		}
	}
	
	@Override
	public void onAddProject() {
		finder.show();
	}
	
	/**
	 * Called when a container entity is selected in the entity finder.
	 * @param id
	 */
	public void onAddProject(String id) {
		synapseClient.getEntityHeaderBatch(Collections.singletonList(id), new AsyncCallback<ArrayList<EntityHeader>>() {
			@Override
			public void onFailure(Throwable caught) {
				finder.showError(caught.getMessage());
			}
		
			@Override
			public void onSuccess(ArrayList<EntityHeader> entityHeaders) {
				if (entityHeaders.size() == 1) {
					EntityHeader entity = entityHeaders.get(0);
					entityIds.add(entity.getId());
					view.setNoContainers(false);
					view.addEntity(entity.getId(), entity.getName(), canEdit);
					finder.hide();
				} else {
					onFailure(new UnknownErrorException(DisplayConstants.ERROR_LOADING));
				}
			}
		});
	}
	
	@Override
	public void onRemoveProject(String id) {
		entityIds.remove(id);
	}
	
	public List<String> getEntityIds() {
		return entityIds;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
}
