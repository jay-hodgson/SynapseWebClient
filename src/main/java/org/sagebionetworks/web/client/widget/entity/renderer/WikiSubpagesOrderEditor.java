package org.sagebionetworks.web.client.widget.entity.renderer;

import java.util.List;

import org.sagebionetworks.repo.model.v2.wiki.V2WikiHeader;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class WikiSubpagesOrderEditor {
	
	private WikiSubpagesOrderEditorView view;
	private WikiSubpageOrderEditorTree editorTree;
	
	@Inject
	public WikiSubpagesOrderEditor(WikiSubpagesOrderEditorView view, WikiSubpageOrderEditorTree editorTree) {
		this.view = view;
		this.editorTree = editorTree;
	}
	
	public void configure(List<V2WikiHeader> wikiHeaders, String ownerObjectName) {
		editorTree.configure(wikiHeaders, ownerObjectName);
		view.configure(editorTree);
	}
	
	public void initializeState() {
		view.initializeState();
	}
	
	/**
	 * Generate the WikiSubpagesOrderEditor Widget
	 */
	public Widget asWidget() {
		return view.asWidget();
	}
	
	public WikiSubpageOrderEditorTree getTree() {
		return editorTree;
	}
}
