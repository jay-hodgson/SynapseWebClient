package org.sagebionetworks.web.client.widget.table.v2.results.cell;

import java.util.ArrayList;
import java.util.Collections;

import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.EntityTypeUtils;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.cache.ClientCache;
import org.sagebionetworks.web.client.place.Synapse;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.lazyload.LazyLoadHelper;
import org.sagebionetworks.web.shared.WebConstants;
import org.sagebionetworks.web.shared.exceptions.UnknownErrorException;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class EntityIdCellRendererImpl implements EntityIdCellRenderer{

	EntityIdCellRendererView view;
	LazyLoadHelper lazyLoadHelper;
	SynapseClientAsync synapseClient;
	
	String entityId, entityName;
	ClientCache clientCache;
	AdapterFactory adapterFactory;
	// 10 minutes?
	public static final Long CACHE_TIME_MS = 1000L*60L*10L;
		
	@Inject
	public EntityIdCellRendererImpl(EntityIdCellRendererView view, 
			SynapseClientAsync synapseClient, 
			LazyLoadHelper lazyLoadHelper,
			ClientCache clientCache,
			AdapterFactory adapterFactory) {
		this.view = view;
		this.lazyLoadHelper = lazyLoadHelper;
		this.synapseClient = synapseClient;
		this.clientCache = clientCache;
		this.adapterFactory = adapterFactory;
		Callback loadDataCallback = new Callback() {
			@Override
			public void invoke() {
				loadData();
			}
		};
		lazyLoadHelper.configure(loadDataCallback, view);
	}

	public void loadData() {
		GWT.debugger();
		if (entityName == null && entityId != null) {
			//try to get it from the client cache
			String entityHeaderJson = clientCache.get(entityId + WebConstants.ENTITY_HEADER_SUFFIX);
			if (entityHeaderJson != null) {
				try {
					EntityHeader header = new EntityHeader(adapterFactory.createNew(entityHeaderJson));
					updateView(header);
					return;
				} catch (JSONObjectAdapterException e) {
					//if any problems occur, try to get the header with a rpc
				}
			}
			view.showLoadingIcon();
			synapseClient.getEntityHeaderBatch(Collections.singletonList(entityId), new AsyncCallback<ArrayList<EntityHeader>>() {
				@Override
				public void onSuccess(ArrayList<EntityHeader> results) {
					if (results.size() == 1) {
						EntityHeader header = results.get(0);
						updateView(header);
						
						// try to save to cache
						JSONObjectAdapter adapter = adapterFactory.createNew();
						try {
							header.writeToJSONObject(adapter);
							Long expireTime = System.currentTimeMillis() + CACHE_TIME_MS;
							clientCache.put(entityId + WebConstants.ENTITY_HEADER_SUFFIX, adapter.toJSONString(), expireTime);
						} catch (JSONObjectAdapterException e) {
						}
					} else {
						onFailure(new UnknownErrorException(DisplayConstants.ERROR_LOADING));
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					view.showErrorIcon(caught.getMessage());
					view.setLinkText(entityId);
				}
			});
		}
	}
	
	public void updateView(EntityHeader header) {
		entityName = header.getName();
		view.setIcon(EntityTypeUtils.getIconTypeForEntityClassName(header.getType()));
		view.setLinkText(entityName);
	}
	
	@Override
	public Widget asWidget() {
		return this.view.asWidget();
	}
	
	@Override
	public void setValue(String value) {
		this.entityId = value;
		entityName = null;
		lazyLoadHelper.setIsConfigured();
		view.setLinkHref(Synapse.getHrefForDotVersion(entityId));
	}

	@Override
	public String getValue() {
		return entityId;
	}

}
