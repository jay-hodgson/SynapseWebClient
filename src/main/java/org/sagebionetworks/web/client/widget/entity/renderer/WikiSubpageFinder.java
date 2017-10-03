package org.sagebionetworks.web.client.widget.entity.renderer;

import java.util.List;

import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.entity.renderer.WikiSubpageOrderEditorTree.SubpageOrderEditorTreeNode;
import org.sagebionetworks.web.client.widget.modal.Dialog;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class WikiSubpageFinder implements Dialog.Callback, IsWidget {
	Dialog dialog;
	WikiSubpageOrderEditorTree tree;
	SynapseAlert synAlert;
	CallbackP<V2WikiHeader> wikiPageIdSelectedCallback;
	@Inject
	public WikiSubpageFinder(Dialog dialog,
			SynapseAlert synAlert,
			WikiSubpageOrderEditorTree tree) {
		super();
		this.dialog = dialog;
		this.synAlert = synAlert;
		this.tree = tree;
		dialog.configure("Select Wiki Page", "OK", "Cancel", this, true);
		dialog.add(tree.asWidget());
		dialog.add(synAlert.asWidget());
	}
	
	@Override
	public void onDefault() {
		dialog.hide();
	}
	
	@Override
	public void onPrimary() {
		// has a selection been made?
		SubpageOrderEditorTreeNode selectedNode = tree.getSelectedTreeItem();
		if (selectedNode == null) {
			synAlert.showError("Please select a Wiki page and try again.");
			return;
		}
		
		wikiPageIdSelectedCallback.invoke(selectedNode.getHeader());
	}
	
	@Override
	public Widget asWidget() {
		return dialog.asWidget();
	}

	public void show(List<V2WikiHeader> wikiHeaders, String ownerObjectName, CallbackP<V2WikiHeader> wikiPageIdSelectedCallback) {
		this.wikiPageIdSelectedCallback = wikiPageIdSelectedCallback;
		//configure tree with wiki headers
		tree.configure(wikiHeaders, ownerObjectName);
		synAlert.clear();
		dialog.show();
	}
}
