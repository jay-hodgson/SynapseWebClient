package org.sagebionetworks.web.client;

import org.sagebionetworks.web.shared.EntityBundleTransport;
import org.sagebionetworks.web.shared.EntityWrapper;
import org.sagebionetworks.web.shared.SerializableWhitelist;

import com.google.gwt.user.client.rpc.AsyncCallback;
	
public interface SynapseClientAsync {

	void getEntity(String entityId, AsyncCallback<EntityWrapper> callback);
	
	void getEntityBundle(String entityId, int partsMask, AsyncCallback<EntityBundleTransport> callback);

	void getEntityTypeRegistryJSON(AsyncCallback<String> callback);

	void getEntityPath(String entityId, AsyncCallback<EntityWrapper> callback);

	void search(String searchQueryJson, AsyncCallback<EntityWrapper> callback);

	void junk(SerializableWhitelist l,
			AsyncCallback<SerializableWhitelist> callback);

	void getEntityReferencedBy(String entityId, AsyncCallback<String> callback);

	
}
