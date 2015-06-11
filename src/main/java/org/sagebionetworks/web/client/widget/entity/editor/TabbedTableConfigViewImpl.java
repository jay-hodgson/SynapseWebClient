package org.sagebionetworks.web.client.widget.entity.editor;

import org.gwtbootstrap3.client.ui.TextArea;
import org.sagebionetworks.web.client.DisplayUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TabbedTableConfigViewImpl implements TabbedTableConfigView {
	public interface TabbedTableConfigViewImplUiBinder extends UiBinder<Widget, TabbedTableConfigViewImpl> {}
	private Presenter presenter;
	
	@UiField
	public TextArea tableContents;
	
	public Widget widget;
	
	@Inject
	public TabbedTableConfigViewImpl(TabbedTableConfigViewImplUiBinder binder) {
		widget = binder.createAndBindUi(this);
		tableContents.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				GWT.debugger();
				if (KeyCodes.KEY_TAB == event.getNativeKeyCode()) {
					event.preventDefault();
					event.stopPropagation();
					int index = tableContents.getCursorPos();
			        String text = tableContents.getText();
			        tableContents.setText(text.substring(0, index) 
			                   + "\t" + text.substring(index));
			        tableContents.setCursorPos(index + 1);
				}	
			}
		});
	}
	
	@Override
	public void initView() {
	}

	@Override
	public void checkParams() throws IllegalArgumentException {
		if (!DisplayUtils.isDefined(tableContents.getValue()))
			throw new IllegalArgumentException("Please enter the table data and try again.");
	}

	@Override
	public Widget asWidget() {
		return widget;
	}	

	@Override 
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
		
	@Override
	public void showErrorMessage(String message) {
		DisplayUtils.showErrorMessage(message);
	}

	@Override
	public void showLoading() {
	}

	@Override
	public void showInfo(String title, String message) {
		DisplayUtils.showInfo(title, message);
	}

	@Override
	public void clear() {
		tableContents.setValue("");
	}

	@Override
	public String getTableContents() {
		return tableContents.getValue();
	}
	
	/*
	 * Private Methods
	 */

}
