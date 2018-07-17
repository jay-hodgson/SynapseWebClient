package org.sagebionetworks.web.client.widget.entity.renderer;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.html.Div;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class XlsPreviewViewImpl implements XlsPreviewView {

	public interface Binder extends UiBinder<Widget, XlsPreviewViewImpl> {}
	
	@UiField
	Div htmlContainer;
	@UiField
	Div synAlertContainer;
	@UiField
	Div loadingUI;
	@UiField
	BlockQuote previewNote;
	Widget w;
	@Inject
	public XlsPreviewViewImpl(Binder binder) {
		w = binder.createAndBindUi(this);
		previewNote.setVisible(false);
	}
	
	@Override
	public Widget asWidget() {
		return w;
	}
	
	@Override
	public void setHtml(String rawHtml) {
		htmlContainer.clear();
		HTML html = new HTML(rawHtml);
		htmlContainer.add(html);
		previewNote.setVisible(true);
	}

	@Override
	public void setLoadingVisible(boolean visible) {
		loadingUI.setVisible(visible);
	}
	@Override
	public void setSynAlert(IsWidget w) {
		synAlertContainer.clear();
		synAlertContainer.add(w);
	}
}
