package org.sagebionetworks.web.client.widget.table.v2.results;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * This is a view only component that contains zero business logic.
 * 
 * @author jhill
 *
 */

public class SortableTableHeaderImpl implements SortableTableHeader {
	
	public interface Binder extends UiBinder<Widget, SortableTableHeaderImpl> {}
	
	@UiField
	Anchor tableHeaderLink;
	
	Widget widget;
	
	@Inject
	public SortableTableHeaderImpl(Binder binder){
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void configure(String displayText, final String columnSql, final SortingListener handler) {
		tableHeaderLink.setText(displayText);
		if(handler != null){
			tableHeaderLink.addClickHandler(event -> {
				handler.onToggleSort(columnSql);
			});
		}
	}

	@Override
	public void setIcon(IconType icon) {
		tableHeaderLink.setIcon(icon);	
	}

}
