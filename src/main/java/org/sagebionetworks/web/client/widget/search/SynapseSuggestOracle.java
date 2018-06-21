package org.sagebionetworks.web.client.widget.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.repo.model.principal.TypeFilter;
import org.sagebionetworks.web.client.GWTTimer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.inject.Inject;

public class SynapseSuggestOracle extends SuggestOracle {

	public SuggestOracle.Request request;
	public SuggestOracle.Callback callback;
	public int pageSize;
	public int offset;
	public boolean isLoading;
	public SynapseSuggestBox suggestBox;
	public UserGroupSuggestionProvider provider;
	public String searchTerm;
	public String width;
	private GWTTimer timer;
	private TypeFilter type = TypeFilter.ALL;
	public static final Map<TypeFilter, List<UserGroupSuggestion>> RECENTLY_SELECTED_SUGGESTIONS = new HashMap<>();
	static {
		for (TypeFilter filter : TypeFilter.values()) {
			RECENTLY_SELECTED_SUGGESTIONS.put(filter, new ArrayList<>());
		}
	}
	
	@Inject
	public SynapseSuggestOracle(GWTTimer timer) {
		this.timer = timer;
	}
	public void setTypeFilter(TypeFilter type) {
		this.type = type;
	}
	public TypeFilter getTypeFilter() {
		return type;
	}
	public void configure(final SynapseSuggestBox suggestBox,
			int pageSize,
			UserGroupSuggestionProvider provider) {
		this.isLoading = false;
		this.suggestBox = suggestBox;
		this.pageSize = pageSize;
		this.provider = provider;
		this.timer.configure(new Runnable() {
			@Override
			public void run() {
				// If you backspace quickly the contents of the field are emptied but a
				// query for a single character is still executed. Workaround for this
				// is to check for an empty string field here.
				if (!suggestBox.getText().trim().isEmpty()) {
					offset = 0;
					suggestBox.setOffset(offset);
					getSuggestions(offset);
				}
			}
		});
	}
 	
	public SuggestOracle.Request getRequest()	{	return request;		}
	public SuggestOracle.Callback getCallback()	{	return callback;	}

	public void getSuggestions(final int offset) {
		if (!isLoading) {
			suggestBox.showLoading();
			//seachTerm or request.getQuery?
			provider.getSuggestions(type, offset, pageSize, suggestBox.getWidth(), request.getQuery(), new AsyncCallback<SynapseSuggestionBundle>() {
				@Override
				public void onSuccess(SynapseSuggestionBundle suggestionBundle) {
					suggestBox.setSelectedSuggestion(null);
					suggestBox.hideLoading();
					if (suggestBox != null) {
						suggestBox.updateFieldStateForSuggestions((int)suggestionBundle.getTotalNumberOfResults(), offset);
					}
					SuggestOracle.Response response = new SuggestOracle.Response(suggestionBundle.getSuggestionBundle());
					callback.onSuggestionsReady(request, response);
					isLoading = false;
				}

				@Override
				public void onFailure(Throwable caught) {
					suggestBox.hideLoading();
					suggestBox.handleOracleException(caught);
				}
			});
		}
	}
	
	@Override
	public void requestSuggestions(SuggestOracle.Request request, SuggestOracle.Callback callback) {
		this.request = request;
		this.callback = callback;
		timer.cancel();
		timer.schedule(SynapseSuggestBox.DELAY);
	}
	
	@Override
	public void requestDefaultSuggestions(Request request, Callback callback) {
		SuggestOracle.Response response = new SuggestOracle.Response(RECENTLY_SELECTED_SUGGESTIONS.get(type));
		callback.onSuggestionsReady(request, response);
	}
	
	public static void addSelectionToRecentList(TypeFilter type, UserGroupSuggestion suggestion) {
		List<UserGroupSuggestion> suggestions = RECENTLY_SELECTED_SUGGESTIONS.get(type);
		if (suggestions.size() > 10) {
			suggestions.remove(suggestions.size() - 1);
		}
		suggestions.add(0, suggestion);
	}
	
	@Override
	public boolean isDisplayStringHTML() {
		return true;
	}
	
	public void setWidth(String width) {
		this.width = width;
	}
	
}