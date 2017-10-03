package org.sagebionetworks.web.client.widget.entity.renderer;

import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.user.client.ui.IsWidget;

public interface WikiSubpagesOrderEditorView extends IsWidget, SynapseView {
	void configure(WikiSubpageOrderEditorTree subpageTree);
	void initializeState();
}
