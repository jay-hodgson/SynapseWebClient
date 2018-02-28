package org.sagebionetworks.web.client.widget.search;

import static org.sagebionetworks.web.client.ServiceEntryPointUtils.fixServiceEntryPoint;

import java.util.Arrays;

import org.sagebionetworks.repo.model.search.query.SearchQuery;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.place.PeopleSearch;
import org.sagebionetworks.web.client.place.Synapse;
import org.sagebionetworks.web.client.presenter.SearchUtil;
import org.sagebionetworks.web.client.widget.SynapseWidgetPresenter;
import org.sagebionetworks.web.shared.SearchQueryUtils;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SearchBox implements SearchBoxView.Presenter, SynapseWidgetPresenter {
	
	private SearchBoxView view;
	private GlobalApplicationState globalApplicationState;
	private JSONObjectAdapter adapterFactory;
	private GWTWrapper gwt;
	private boolean searchAll = false;
	public static final RegExp DOI_REGEX = RegExp.compile("10[.]{1}[0-9]+[/]{1}(syn([0-9]+[.]?[0-9]*)+)$", "i");
	
	@Inject
	public SearchBox(SearchBoxView view, 
			GlobalApplicationState globalApplicationState,
			JSONObjectAdapter adapterFactory,
			GWTWrapper gwt) {
		this.view = view;
		this.globalApplicationState = globalApplicationState;
		this.adapterFactory = adapterFactory;
		this.gwt = gwt;
		view.setPresenter(this);
	}	
	
	@Override
	public Widget asWidget() {
		view.setPresenter(this);
		return view.asWidget();		
	}

	public void clearState() {
		view.clear();
	}

	@Override
	public void search(String value) {
		if (value != null && !value.isEmpty()) {
			if (value.charAt(0) == '@') {
				globalApplicationState.getPlaceChanger().goTo(new PeopleSearch(value.substring(1)));
			} else {
				MatchResult matcher = DOI_REGEX.exec(value);
				if (matcher != null && matcher.getGroupCount() > 0){
					globalApplicationState.getPlaceChanger().goTo(new Synapse(matcher.getGroup(1)));
				} else {
					SearchUtil.searchForTerm(value, globalApplicationState, adapterFactory, gwt, searchAll);
				}
			}
		}
	}


	@Override
	public void setSearchAll(boolean searchAll) {
		this.searchAll = searchAll;
	}

	public void setVisible(boolean isVisible) {
		view.setVisible(isVisible);
	}

	
	/*
	 * Private Methods
	 */
}
