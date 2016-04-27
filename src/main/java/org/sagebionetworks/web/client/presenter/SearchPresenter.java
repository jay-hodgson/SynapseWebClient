package org.sagebionetworks.web.client.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.EntityTypeUtils;
import org.sagebionetworks.repo.model.search.Hit;
import org.sagebionetworks.repo.model.search.SearchResults;
import org.sagebionetworks.repo.model.search.query.KeyValue;
import org.sagebionetworks.repo.model.search.query.SearchQuery;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.DisplayUtils;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.RequestBuilderWrapper;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.cookie.CookieProvider;
import org.sagebionetworks.web.client.place.Search;
import org.sagebionetworks.web.client.view.SearchView;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.pagination.BasicPaginationWidget;
import org.sagebionetworks.web.client.widget.pagination.DetailedPaginationWidget;
import org.sagebionetworks.web.client.widget.pagination.PageChangeListener;
import org.sagebionetworks.web.client.widget.search.PaginationEntry;
import org.sagebionetworks.web.client.widget.search.PaginationUtil;
import org.sagebionetworks.web.shared.SearchQueryUtils;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class SearchPresenter extends AbstractActivity implements SearchView.Presenter, Presenter<Search>, PageChangeListener {
	
	//private final List<String> FACETS_DEFAULT = Arrays.asList(new String[] {"node_type","disease","species","tissue","platform","num_samples","created_by","modified_by","created_on","modified_on","acl","reference"});
	
	private Search place;
	private SearchView view;
	private GlobalApplicationState globalApplicationState;
	private SynapseClientAsync synapseClient;
	private JSONObjectAdapter jsonObjectAdapter;
	private SynapseAlert synAlert;
	
	private SearchQuery currentSearch;
	private SearchResults currentResult;
	private boolean newQuery = false;
	private Map<String,String> timeValueToDisplay = new HashMap<String, String>();
	private Date searchStartTime;
	private CookieProvider cookies;
	private RequestBuilderWrapper requestBuilder;
	private static String GOOGLE_API_KEY = "AIzaSyBH1ZOIRGBY09yMgX75mGQmkYVJrKsBV3w";
	private static String GOOGLE_SEARCH_ENGINE_ID_CX = "011729395372098405859:0vx7jvfcbik";
	// pagination widget used in google results
	BasicPaginationWidget paginationWidget;
	
	@Inject
	public SearchPresenter(SearchView view,
			GlobalApplicationState globalApplicationState,
			SynapseClientAsync synapseClient,
			JSONObjectAdapter jsonObjectAdapter,
			SynapseAlert synAlert, 
			CookieProvider cookies,
			RequestBuilderWrapper requestBuilder,
			BasicPaginationWidget paginationWidget) {
		this.view = view;
		this.globalApplicationState = globalApplicationState;
		this.synapseClient = synapseClient;
		this.jsonObjectAdapter = jsonObjectAdapter;
		this.synAlert = synAlert;
		this.cookies = cookies;
		this.requestBuilder = requestBuilder;
		this.paginationWidget = paginationWidget;
		currentSearch = getBaseSearchQuery();
		view.setPresenter(this);
		view.setSynAlertWidget(synAlert.asWidget());
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// Install the view
		panel.setWidget(view);
	}

	@Override
	public void setPlace(Search place) {
		this.place = place;
		view.setPresenter(this);
		String queryTerm = place.getSearchTerm();
		if (queryTerm == null) queryTerm = "";
		currentSearch = checkForJson(queryTerm);
		// if in alpha mode, use Google custom search
		if(DisplayUtils.isInTestWebsite(cookies)) {
			//start index is 1 in this api!
			view.clear();
			executeGoogleCustomSearch(0L);
		} else {
			if (place.getStart() != null)
				currentSearch.setStart(place.getStart());
			executeSearch();	
		}
	}
	
	@Override
	public void onPageChange(Long newOffset) {
		executeGoogleCustomSearch(newOffset);
	}
	
	public String getBasicSearchTermFromCurrentQuery() {
		List<String> searchTerms = currentSearch.getQueryTerm();
		StringBuilder query = new StringBuilder();
		for (String term : searchTerms) {
			query.append(term);
			query.append(' ');
		}
		String queryString = query.toString();
		if (queryString.indexOf("%20") > -1) {
			queryString = URL.decode(queryString);
		}
		return queryString;
	}
	
	public void executeGoogleCustomSearch(Long index) {
		if (index < 1) {
			index = 1L;
		}
		String searchTerms = getBasicSearchTermFromCurrentQuery();
		view.setSearchTerm(searchTerms);
		String encodedSearchTerm = URL.encodeQueryString(searchTerms);
		//add filter for performance: fields=items(htmlTitle,link,htmlSnippet)
		String url = "https://www.googleapis.com/customsearch/v1?prettyPrint=false&key="+GOOGLE_API_KEY+"&start="+index+"&cx="+GOOGLE_SEARCH_ENGINE_ID_CX+"&q=" + encodedSearchTerm;
		executeGoogleCustomSearchUrl(url);
	}
	
	public void executeGoogleCustomSearchUrl(String url) {
		//get results from google api
		//url encode search term
		requestBuilder.configure(RequestBuilder.GET, url);
		view.showLoading();
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
						Response response) {
					int statusCode = response.getStatusCode();
					if (statusCode == Response.SC_OK) {
						StringBuilder builder = new StringBuilder();
						JSONObject value = JSONParser.parseStrict(response.getText()).isObject();
						//determine previous and next page start index
						JSONObject queries = value.get("queries").isObject();
						
						// set up pagination
						JSONObject requestQuery = queries.get("request").isArray().get(0).isObject();
						Long limit = (long)requestQuery.get("count").isNumber().doubleValue();
						Long offset = (long)requestQuery.get("startIndex").isNumber().doubleValue();
						Long totalCount = Long.parseLong(requestQuery.get("totalResults").isString().stringValue());
						if (totalCount > 0) {
							paginationWidget.configure(limit, offset, totalCount, SearchPresenter.this);	
						} 
						if (value.containsKey("items")) {
							JSONArray items = value.get("items").isArray();
							for (int i = 0; i < items.size(); i++) {
								JSONObject item = items.get(i).isObject();
								builder.append("<a href=\"");
								builder.append(item.get("link").isString().stringValue());
								builder.append("\">");
								builder.append(item.get("htmlTitle").isString().stringValue());
								builder.append("</a><br>");
								builder.append(item.get("htmlSnippet").isString().stringValue().replaceAll("<br>", ""));
								builder.append("<br><br>");
							}
						}
						view.setGoogleSearchResults(builder.toString());
						view.setGooglePaginationWidget(paginationWidget.asWidget());
					} else {
						JSONObject value = JSONParser.parseStrict(response.getText()).isObject();
						String reason = value.get("message").isString().stringValue();
						onError(null, new IllegalArgumentException("Unable to retrieve search results. Reason: " + reason));
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					synAlert.handleException(exception);
				}
			});
		} catch (final Exception e) {
			synAlert.handleException(e);
		}
		
	}
	
	@Override
    public String mayStop() {
        view.clear();
        return null;
    }

	@Override
	public void setSearchTerm(String queryTerm) {
		SearchUtil.searchForTerm(queryTerm, globalApplicationState, synapseClient);
	}

	@Override
	public void addFacet(String facetName, String facetValue) {
		List<KeyValue> bq = currentSearch.getBooleanQuery();
		if(bq == null) {
			bq = new ArrayList<KeyValue>();			
			currentSearch.setBooleanQuery(bq);
		}

		// check if exists
		boolean exists = false;
		for(KeyValue kv : bq) {
			if(kv.getKey().equals(facetName) && kv.getValue().equals(facetValue)) {
				exists = true;
				break;
			}
		}
		
		// only add if not exists already. but do run the search
		if(!exists) {	
					
			// add facet to query list
			KeyValue kv = new KeyValue();		
			kv.setKey(facetName);
			kv.setValue(facetValue);		
			bq.add(kv);
			
			// set start back to zero so we go to first page with the new facet
			currentSearch.setStart(new Long(0));			
		}

		executeNewSearch();
	}

	private void executeNewSearch() {
		JSONObjectAdapter adapter = jsonObjectAdapter.createNew();
		
		try {
			currentSearch.writeToJSONObject(adapter);
		} catch (JSONObjectAdapterException e) {
			view.showErrorMessage(DisplayConstants.ERROR_GENERIC);
		}
		setSearchTerm(adapter.toJSONString());
	}

	@Override
	public void addTimeFacet(String facetName, String facetValue, String displayValue) {
		timeValueToDisplay.put(createTimeValueKey(facetName, facetValue), displayValue);
		addFacet(facetName, facetValue);
	}
	
	@Override
	public String getDisplayForTimeFacet(String facetName, String facetValue) {
		return timeValueToDisplay.get(createTimeValueKey(facetName, facetValue));
	}
	
	
	@Override
	public void removeFacet(String facetName, String facetValue) {
		List<KeyValue> bq = currentSearch.getBooleanQuery();
		// check for existing facet and remove it
		for(KeyValue kv : bq) {
			if(kv.getKey().equals(facetName) && kv.getValue().equals(facetValue)) {
				bq.remove(kv);
				break;
			}
		}
		
		// set to first page
		currentResult.setStart(new Long(0));
		executeNewSearch();
	}

	@Override
	public void clearSearch() {
		currentSearch = getBaseSearchQuery();
		executeNewSearch();
	}
	
	@Override
	public List<KeyValue> getAppliedFacets() {
		List<KeyValue> bq = currentSearch.getBooleanQuery(); 
		if(bq == null) {
			return new ArrayList<KeyValue>();
		} else {
			return bq;
		}
	}

	@Override
	public List<String> getFacetDisplayOrder() {
		return SearchQueryUtils.FACETS_DISPLAY_ORDER;
	}

	@Override
	public void setStart(int newStart) {
		currentSearch.setStart(new Long(newStart));
		executeNewSearch();
	}
	
	@Override
	public Long getStart() {
		return currentSearch.getStart();
	}

	@Override
	public Date getSearchStartTime() {
		if(searchStartTime == null) searchStartTime = new Date();
		return searchStartTime;		
	}

	@Override
	public List<PaginationEntry> getPaginationEntries(int nPerPage, int nPagesToShow) {
		if(currentResult == null) return null;		
		Long nResults = currentResult.getFound();
		Long start = currentResult.getStart();
		if(nResults == null || start == null)
			return null;
		return PaginationUtil.getPagination(nResults.intValue(), start.intValue(), nPerPage, nPagesToShow);		
	}

	@Override
	public IconType getIconForHit(Hit hit) {
		if(hit == null) return null;
		EntityType type = EntityType.valueOf(hit.getNode_type());
		return DisplayUtils.getIconTypeForEntityClassName(EntityTypeUtils.getEntityTypeClassName(type));
	}
	
	@Override
	public String getCurrentSearchJSON() {
		String searchJSON = "";
		JSONObjectAdapter adapter = jsonObjectAdapter.createNew();
		try {
			currentSearch.writeToJSONObject(adapter);
			searchJSON = adapter.toJSONString();
		} catch (JSONObjectAdapterException e) {
			view.showErrorMessage(DisplayConstants.ERROR_GENERIC);
		}
		return searchJSON;
	}

	private SearchQuery checkForJson(String queryString) {
		SearchQuery query = getBaseSearchQuery();

		query.setQueryTerm(Arrays.asList(queryString.split(" ")));

		// if query parses into SearchQuery, use that, otherwise use it as a
		// search Term
		if (queryString != null) {
			String fixedQueryString = queryString;
			//check for url encoded
			if (queryString.startsWith("%7B")) {
				fixedQueryString = URL.decode(queryString);
			}
			if (fixedQueryString.startsWith("{")) {
				try {
					query = new SearchQuery(jsonObjectAdapter.createNew(fixedQueryString));
					// passed a searchQuery
				} catch (JSONObjectAdapterException e) {
					// fall through to a use as search term
				}
			}
		} 

		return query;
	}

	private SearchQuery getBaseSearchQuery() {
		SearchQuery query = SearchQueryUtils.getDefaultSearchQuery();
		timeValueToDisplay.clear();
		searchStartTime = new Date();		
		newQuery = true;		
		return query;
	}
	
	private void executeSearch() { 	
		synAlert.clear();
		view.showLoading();
		// Is there a search defined? If not, display empty result.
		if (isEmptyQuery()) {
			currentResult = new SearchResults();
			currentResult.setFound(new Long(0));
			view.setSearchResults(currentResult, "", newQuery);
			newQuery = false;
			return;
		}
		AsyncCallback<SearchResults> callback = new AsyncCallback<SearchResults>() {			
			@Override
			public void onSuccess(SearchResults result) {
				currentResult = result;
				view.setSearchResults(currentResult, join(currentSearch.getQueryTerm(), " "), newQuery);
				newQuery = false;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.clear();
				synAlert.handleException(caught);
			}
		};
		synapseClient.search(currentSearch, callback);
	}

	private boolean isEmptyQuery() {
		return (currentSearch.getQueryTerm() == null || currentSearch.getQueryTerm().size() == 0 
				|| (currentSearch.getQueryTerm().size() == 1 && "".equals(currentSearch.getQueryTerm().get(0))))
				&& (currentSearch.getBooleanQuery() == null || currentSearch.getBooleanQuery().size() == 0);
	}


	private static String join(List<String> list, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (String item : list) {
			sb.append(item);
			sb.append(delimiter);
		}
		String str = sb.toString();
		if (str.length() > 0) {
			str = str.substring(0, str.length()-1);
		}
		return str;
	}

	private String createTimeValueKey(String facetName, String facetValue) {
		return facetName + facetValue;
	}

}
