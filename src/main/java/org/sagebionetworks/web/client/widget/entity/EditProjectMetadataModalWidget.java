package org.sagebionetworks.web.client.widget.entity;

import org.sagebionetworks.repo.model.EntityBundle;
import org.sagebionetworks.web.client.utils.Callback;

import com.google.gwt.user.client.ui.IsWidget;

public interface EditProjectMetadataModalWidget extends IsWidget{
	public void configure(EntityBundle entityBundle, boolean canChangeSettings, Callback handler);
}
