package org.sagebionetworks.web.client.widget.entity.renderer;

import org.sagebionetworks.web.client.SynapseView;

import com.google.gwt.user.client.ui.IsWidget;

public interface WikiSubpagesOrderEditorView extends IsWidget, SynapseView {
	
	/**
	 * Sets the presenter for this view.
	 * @param presenter The presenter to be set.
	 */
	void setPresenter(Presenter presenter);
	
	void configure(WikiSubpageOrderEditorTree subpageTree);
	
	void initializeState();
	
	interface Presenter {}
}
