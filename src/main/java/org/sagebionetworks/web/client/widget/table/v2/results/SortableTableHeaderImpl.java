package org.sagebionetworks.web.client.widget.table.v2.results;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.sagebionetworks.web.client.view.bootstrap.table.TableHeader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
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
	Button button;
	@UiField
	TableHeader tableHeader;
	
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
	public void configure(final String text, final SortingListener handler) {
		button.setText(text);
		if(handler != null){
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					handler.onToggleSort(text);
				}
			});
		}
	}

	@Override
	public void setIcon(IconType icon) {
		button.setIcon(icon);	
	}

	@Override
	public void add(IsWidget child) {
		tableHeader.add(child);
	}
}
