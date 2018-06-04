package org.sagebionetworks.web.client.widget.table.v2.results;

import static org.sagebionetworks.repo.model.table.TableConstants.NULL_VALUE_KEYWORD;
import static org.sagebionetworks.web.client.ServiceEntryPointUtils.fixServiceEntryPoint;
import static org.sagebionetworks.web.client.widget.table.v2.TableEntityWidget.DEFAULT_LIMIT;
import static org.sagebionetworks.web.client.widget.table.v2.TableEntityWidget.DEFAULT_OFFSET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sagebionetworks.repo.model.asynch.AsynchronousJobStatus;
import org.sagebionetworks.repo.model.asynch.AsynchronousResponseBody;
import org.sagebionetworks.repo.model.table.FacetColumnRangeRequest;
import org.sagebionetworks.repo.model.table.FacetColumnRequest;
import org.sagebionetworks.repo.model.table.FacetColumnResult;
import org.sagebionetworks.repo.model.table.FacetColumnResultValueCount;
import org.sagebionetworks.repo.model.table.FacetColumnResultValues;
import org.sagebionetworks.repo.model.table.FacetColumnValuesRequest;
import org.sagebionetworks.repo.model.table.Query;
import org.sagebionetworks.repo.model.table.QueryBundleRequest;
import org.sagebionetworks.repo.model.table.QueryResultBundle;
import org.sagebionetworks.repo.model.table.Row;
import org.sagebionetworks.repo.model.table.SelectColumn;
import org.sagebionetworks.repo.model.table.SortItem;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.cache.ClientCache;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.asynch.AsynchronousJobTracker;
import org.sagebionetworks.web.client.widget.asynch.AsynchronousProgressHandler;
import org.sagebionetworks.web.client.widget.asynch.AsynchronousProgressWidget;
import org.sagebionetworks.web.client.widget.asynch.UpdatingAsynchProgressHandler;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.table.modal.fileview.TableType;
import org.sagebionetworks.web.shared.asynch.AsynchType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * This widget will execute a table query and show the resulting query results in a paginated view..
 * 
 * @author jmhill
 *
 */
public class TableQueryResultWidget implements TableQueryResultView.Presenter, IsWidget, PagingAndSortingListener {
	public static final int ETAG_CHECK_DELAY_MS = 5000;
	public static final String VERIFYING_ETAG_MESSAGE = "Verifying that the recent changes have propagated through the system...";
	public static final String RUNNING_QUERY_MESSAGE = "Running query...";
	public static final String QUERY_CANCELED = "Query canceled";
	/**
	 * Masks for requesting what should be included in the query bundle.
	 */
	public static final long BUNDLE_MASK_QUERY_RESULTS = 0x1;
	public static final long BUNDLE_MASK_QUERY_COUNT = 0x2;
	public static final long BUNDLE_MASK_QUERY_SELECT_COLUMNS = 0x4;
	public static final long BUNDLE_MASK_QUERY_MAX_ROWS_PER_PAGE = 0x8;
	public static final long BUNDLE_MASK_QUERY_COLUMN_MODELS = 0x10;
	public static final long BUNDLE_MASK_QUERY_FACETS = 0x20;

	private static final Long ALL_PARTS_MASK = new Long(255);
	SynapseClientAsync synapseClient;
	TableQueryResultView view;
	PortalGinInjector ginInjector;
	TablePageWidget pageViewerWidget;
	QueryResultEditorWidget queryResultEditor;
	Query startingQuery;
	boolean isEditable;
	TableType tableType;
	QueryResultsListener queryListener;
	SynapseAlert synapseAlert;
	CallbackP<FacetColumnRequest> facetChangedHandler;
	Callback resetFacetsHandler;
	ClientCache clientCache;
	GWTWrapper gwt;
	int currentJobIndex = 0;
	QueryResultBundle cachedFullQueryResultBundle = null, filteredResultsQueryBundle;
	List<SortItem> sortItems;
	boolean facetsVisible = true, isLoadAllPages = true;
	
	@Inject
	public TableQueryResultWidget(TableQueryResultView view, 
			SynapseClientAsync synapseClient, 
			PortalGinInjector ginInjector, 
			SynapseAlert synapseAlert,
			ClientCache clientCache,
			GWTWrapper gwt) {
		this.synapseClient = synapseClient;
		fixServiceEntryPoint(synapseClient);
		this.view = view;
		this.ginInjector = ginInjector;
		this.pageViewerWidget = ginInjector.createNewTablePageWidget();
		this.synapseAlert = synapseAlert;
		this.clientCache = clientCache;
		this.gwt = gwt;
		this.view.setPageWidget(this.pageViewerWidget);
		this.view.setPresenter(this);
		this.view.setSynapseAlertWidget(synapseAlert.asWidget());
		resetFacetsHandler = new Callback() {
			@Override
			public void invoke() {
				startingQuery.setSelectedFacets(null);
				if (!isLoadAllPages) {
					cachedFullQueryResultBundle = null;
					startingQuery.setOffset(0L);
					runQuery();	
				} else {
					facetsChanged();
				}
			}
		};
		facetChangedHandler = new CallbackP<FacetColumnRequest>() {
			@Override
			public void invoke(FacetColumnRequest request) {
				List<FacetColumnRequest> selectedFacets = startingQuery.getSelectedFacets();
				if (selectedFacets == null) {
					selectedFacets = new ArrayList<FacetColumnRequest>();
					startingQuery.setSelectedFacets(selectedFacets);
				}
				for (FacetColumnRequest facetColumnRequest : selectedFacets) {
					if (facetColumnRequest.getColumnName().equals(request.getColumnName())) {
						selectedFacets.remove(facetColumnRequest);
						break;
					}
				}
				selectedFacets.add(request);
				if (!isLoadAllPages) {
					cachedFullQueryResultBundle = null;
					startingQuery.setOffset(0L);
					runQuery();
				} else {
					facetsChanged();
				}
			}
		};
	}
	
	public void facetsChanged() {
		//calculate correct counts based on new selected facets.  use full result set (cachedFullQueryResultBundle) to set up current view (filteredResultsQueryBundle)
		List<FacetColumnRequest> selectedFacets = startingQuery.getSelectedFacets();
		filteredResultsQueryBundle = deepCopyQueryBundle(cachedFullQueryResultBundle);
		
		// apply facets to filteredResultsQueryBundle!
		// first, remove rows that don't pass the selected facets
		List<FacetColumnResult> facetResults = filteredResultsQueryBundle.getFacets();
		List<Row> allRows = filteredResultsQueryBundle.getQueryResult().getQueryResults().getRows();
		List<SelectColumn> selectedColumns = filteredResultsQueryBundle.getQueryResult().getQueryResults().getHeaders();
		Map<String, Integer> columnName2Index = new HashMap<>();
		for (int i = 0; i < selectedColumns.size(); i++) {
			columnName2Index.put(selectedColumns.get(i).getName(), i);
		}
		Map<String, FacetColumnResult> columnName2FacetColumnResult = new HashMap<>();
		for (FacetColumnResult facetColumnResult : facetResults) {
			columnName2FacetColumnResult.put(facetColumnResult.getColumnName(), facetColumnResult);
			//also reset the counts (for values results)
			if (facetColumnResult instanceof FacetColumnResultValues) {
				for (FacetColumnResultValueCount valueCount : ((FacetColumnResultValues)facetColumnResult).getFacetValues()) {
					valueCount.setCount(0L);
				}
			}
		}
		
		List<Row> filteredRows = new ArrayList<>();
		for (Row row : allRows) {
			//does this row pass the selected facet definitions?
			boolean passesFacetConditions = true;
			for (FacetColumnRequest facetColumnRequest : selectedFacets) {
				int index = columnName2Index.get(facetColumnRequest.getColumnName());
				String value = row.getValues().get(index);
				if (facetColumnRequest instanceof FacetColumnRangeRequest) {
					if (value != null) {
						Double valueDouble = Double.parseDouble(value);
						FacetColumnRangeRequest rangeRequest = (FacetColumnRangeRequest)facetColumnRequest;
						if (rangeRequest.getMin() != null) {
							Double minDouble = Double.parseDouble(rangeRequest.getMin());
							if (valueDouble < minDouble) {
								passesFacetConditions = false;
								break;
							}
						}
						if (rangeRequest.getMax() != null) {
							Double maxDouble = Double.parseDouble(rangeRequest.getMax());
							if (valueDouble > maxDouble) {
								passesFacetConditions = false;
								break;
							}
						}
					} else {
						passesFacetConditions = false;
						break;
					}
				} else {
					FacetColumnValuesRequest valuesRequest = (FacetColumnValuesRequest)facetColumnRequest;
					Set<String> selectedFacetValues = valuesRequest.getFacetValues();
					if (value != null && !selectedFacetValues.contains(value)) {
						passesFacetConditions = false;
						break;
					}
				}
			}
			if (passesFacetConditions) {
				filteredRows.add(row);
			}
		}
		//now that we have the correct rows, update the counts!
		for (Row row : allRows) {
			for (FacetColumnResult facetColumnResult : facetResults) {
				if (facetColumnResult instanceof FacetColumnResultValues) {
					FacetColumnResultValues facetColumnResultValues = (FacetColumnResultValues)facetColumnResult;
					int index = columnName2Index.get(facetColumnResultValues.getColumnName());
					String value = row.getValues().get(index);
					for (FacetColumnResultValueCount valueCount : facetColumnResultValues.getFacetValues()) {
						if ((value == null && valueCount.getValue().equals(NULL_VALUE_KEYWORD)) ||
							value != null && valueCount.getValue().equals(value)) {
							valueCount.setCount(valueCount.getCount().longValue()+1);
						}
					}
				}
			}
		}
		filteredResultsQueryBundle.getQueryResult().getQueryResults().setRows(filteredRows);
		onPageChange(0L);
	}
	
	/**
	 * Configure this widget with a query string.
	 * @param queryString
	 * @param isEditable Is the user allowed to edit the query results?
	 * @param is table a file view?
	 * @param listener Listener for query start and finish events.
	 */
	public void configure(Query query, boolean isEditable, TableType tableType, QueryResultsListener listener){
		this.isEditable = isEditable;
		this.tableType = tableType;
		this.startingQuery = query;
		this.queryListener = listener;
		cachedFullQueryResultBundle = null;
		runQuery();
	}
	
	private void runQuery() {
		currentJobIndex++;
		runQuery(currentJobIndex);
	}
	
	private void runQuery(final int jobIndex) {
		this.view.setErrorVisible(false);
		fireStartEvent();
		pageViewerWidget.setTableVisible(false);
		this.view.setProgressWidgetVisible(true);
		String entityId = QueryBundleUtils.getTableId(this.startingQuery);
		String viewEtag = clientCache.get(entityId + QueryResultEditorWidget.VIEW_RECENTLY_CHANGED_KEY);
		if (viewEtag == null) {
			// run the job
			QueryBundleRequest qbr = new QueryBundleRequest();
			long partMask = BUNDLE_MASK_QUERY_RESULTS;
			// do not ask for query count
			if (cachedFullQueryResultBundle == null) {
				partMask = partMask | BUNDLE_MASK_QUERY_COLUMN_MODELS | BUNDLE_MASK_QUERY_SELECT_COLUMNS;
				if (facetsVisible) {
					partMask = partMask | BUNDLE_MASK_QUERY_FACETS;
				}
			} else {
				// we can release the old query result
				cachedFullQueryResultBundle.setQueryResult(null);
			}
			qbr.setPartMask(partMask);
			qbr.setQuery(this.startingQuery);
			qbr.setEntityId(entityId);
			AsynchronousProgressWidget progressWidget = ginInjector.creatNewAsynchronousProgressWidget();
			this.view.setProgressWidget(progressWidget);
			progressWidget.startAndTrackJob(RUNNING_QUERY_MESSAGE, false, AsynchType.TableQuery, qbr, new AsynchronousProgressHandler() {
				
				@Override
				public void onFailure(Throwable failure) {
					if (currentJobIndex == jobIndex) {
						showError(failure);	
					}
				}
				
				@Override
				public void onComplete(AsynchronousResponseBody response) {
					QueryResultBundle queryResultBundle = (QueryResultBundle) response;
					if (currentJobIndex == jobIndex) {
						setQueryResults(queryResultBundle);
					}
//					todo: do a query to find out how many pages there are (isLoadAllPages) 
					if (isLoadAllPages && startingQuery.getLimit().intValue() == queryResultBundle.getQueryResult().getQueryResults().getRows().size()) {
						//get next page!
						preloadNextPage(startingQuery.getLimit());
					}
				}
				
				@Override
				public void onCancel() {
					if (currentJobIndex == jobIndex) {
						showError(QUERY_CANCELED);
					}
				}
			});
		} else {
			verifyOldEtagIsNotInView(entityId, viewEtag);
		}
	}
	
	private void preloadNextPage(final Long offset) {
		QueryBundleRequest qbr = new QueryBundleRequest();
		long partMask = BUNDLE_MASK_QUERY_RESULTS;
		qbr.setPartMask(partMask);
		
		qbr.setQuery(deepCopyQuery(this.startingQuery));
		qbr.getQuery().setOffset(offset);
		String entityId = QueryBundleUtils.getTableId(this.startingQuery);
		qbr.setEntityId(entityId);
		AsynchronousJobTracker jobTracker = ginInjector.getAsynchronousJobTracker();
		jobTracker.startAndTrack(AsynchType.TableQuery, qbr, AsynchronousProgressWidget.WAIT_MS,
				new UpdatingAsynchProgressHandler() {
			
			@Override
			public void onFailure(Throwable failure) {
				showError(failure);
			}
			
			@Override
			public void onComplete(AsynchronousResponseBody response) {
				QueryResultBundle queryResultBundle = (QueryResultBundle) response;
				addQueryResults(queryResultBundle);
				if (startingQuery.getLimit().intValue() == queryResultBundle.getQueryResult().getQueryResults().getRows().size()) {
					preloadNextPage(offset + startingQuery.getLimit());
				} else {
					//done preloading!
					filteredResultsQueryBundle = deepCopyQueryBundle(cachedFullQueryResultBundle);
				}
			}
			
			@Override
			public void onCancel() {
				showError(QUERY_CANCELED);
			}
			
			@Override
			public void onUpdate(AsynchronousJobStatus status) {
			}
			
			@Override
			public boolean isAttached() {
				return true;
			}
		});
	}
	
	public void addQueryResults(QueryResultBundle queryResultBundle) {
		cachedFullQueryResultBundle.getQueryResult().getQueryResults().getRows().addAll(queryResultBundle.getQueryResult().getQueryResults().getRows());
	}
	
	/**
	 * Look for the given etag in the given file view.  If it is still there, wait a few seconds and try again.  
	 * If the etag is not in the view, then remove the clientCache key and run the query (since this indicates that the user change was propagated to the replicated layer)
	 * @param fileViewEntityId
	 * @param oldEtag
	 */
	public void verifyOldEtagIsNotInView(final String fileViewEntityId, String oldEtag) {
		//check to see if etag exists in view
		QueryBundleRequest qbr = new QueryBundleRequest();
		qbr.setPartMask(ALL_PARTS_MASK);
		Query query = new Query();
		query.setSql("select * from " + fileViewEntityId + " where ROW_ETAG='"+oldEtag+"'");
		query.setOffset(DEFAULT_OFFSET);
		query.setLimit(DEFAULT_LIMIT);
		query.setIsConsistent(true);
		qbr.setQuery(query);
		qbr.setEntityId(fileViewEntityId);
		AsynchronousProgressWidget progressWidget = ginInjector.creatNewAsynchronousProgressWidget();
		this.view.setProgressWidget(progressWidget);
		progressWidget.startAndTrackJob(VERIFYING_ETAG_MESSAGE, false, AsynchType.TableQuery, qbr, new AsynchronousProgressHandler() {
			@Override
			public void onFailure(Throwable failure) {
				showError(failure);
			}
			
			@Override
			public void onComplete(AsynchronousResponseBody response) {
				QueryResultBundle resultBundle = (QueryResultBundle) response;
				if (resultBundle.getQueryCount() > 0) {
					// retry after waiting a few seconds
					gwt.scheduleExecution(new Callback() {
						@Override
						public void invoke() {
							runQuery();
						}
					}, ETAG_CHECK_DELAY_MS);
				} else {
					// clear cache value and run the actual query
					clientCache.remove(fileViewEntityId + QueryResultEditorWidget.VIEW_RECENTLY_CHANGED_KEY);
					runQuery();
				}
			}
			
			@Override
			public void onCancel() {
				showError(QUERY_CANCELED);
			}
		});
	}
	/**
	 * Called after a successful query.
	 * @param bundle
	 */
	private void setQueryResults(final QueryResultBundle bundle){
		if (cachedFullQueryResultBundle != null) {
			bundle.setColumnModels(cachedFullQueryResultBundle.getColumnModels());
			bundle.setFacets(cachedFullQueryResultBundle.getFacets());
			bundle.setSelectColumns(cachedFullQueryResultBundle.getSelectColumns());
			
		}
		//cachedFullQueryResultBundle will be populated with all pages (if loading all results locally)
		cachedFullQueryResultBundle = bundle;
		//filteredResultsQueryBundle will be filtered by the facet selection (if shown)
		filteredResultsQueryBundle = bundle;
		
		// Get the sort info
		this.synapseClient.getSortFromTableQuery(this.startingQuery.getSql(), new AsyncCallback<List<SortItem>>() {
			
			@Override
			public void onSuccess(List<SortItem> sortItems) {
				setQueryResultsAndSort(sortItems);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError(caught);
			}
		});
	}
	
	private QueryResultBundle deepCopyQueryBundle(QueryResultBundle toCopy) {
		JSONObjectAdapter adapter = ginInjector.getAdapterFactory().createNew();
		try {
			toCopy.writeToJSONObject(adapter);
			return new QueryResultBundle(adapter);
		} catch (JSONObjectAdapterException e) {
			showError(e);
		}
		return null;
	}
	
	private Query deepCopyQuery(Query toCopy) {
		JSONObjectAdapter adapter = ginInjector.getAdapterFactory().createNew();
		try {
			toCopy.writeToJSONObject(adapter);
			return new Query(adapter);
		} catch (JSONObjectAdapterException e) {
			showError(e);
		}
		return null;
	}
	
	private void setQueryResultsAndSort(List<SortItem> sortItems){
		this.sortItems = sortItems;
		this.view.setErrorVisible(false);
		this.view.setProgressWidgetVisible(false);
		// configure the page widget
		this.pageViewerWidget.configure(filteredResultsQueryBundle, this.startingQuery, sortItems, false, tableType, null, this, facetChangedHandler, resetFacetsHandler);
		pageViewerWidget.setTableVisible(true);
		fireFinishEvent(true, isQueryResultEditable());
	}

	/**
	 * The results are editable if all of the select columns have ID
	 * @return
	 */
	public boolean isQueryResultEditable(){
		List<SelectColumn> selectColums = QueryBundleUtils.getSelectFromBundle(this.filteredResultsQueryBundle);
		if(selectColums == null){
			return false;
		}
		// Do all columns have IDs?
		for(SelectColumn col: selectColums){
			if(col.getId() == null){
				return false;
			}
		}
		// All of the columns have ID so we can edit
		return true;
	}
	
	/**
	 * Starting a query.
	 */
	private void fireStartEvent() {
		if(this.queryListener != null){
			this.queryListener.queryExecutionStarted();
		}
	}
	
	/**
	 * Finished a query.
	 */
	private void fireFinishEvent(boolean wasSuccessful, boolean resultsEditable) {
		if(this.queryListener != null){
			this.queryListener.queryExecutionFinished(wasSuccessful, resultsEditable);
		}
	}
	
	/**
	 * Show an error.
	 * @param caught
	 */
	private void showError(Throwable caught){
		setupErrorState();
		synapseAlert.handleException(caught);
	}
	
	/**
	 * Show an error message.
	 * @param message
	 */
	private void showError(String message){
		setupErrorState();
		synapseAlert.showError(message);
	}
	
	private void setupErrorState() {
		pageViewerWidget.setTableVisible(false);
		this.view.setProgressWidgetVisible(false);
		fireFinishEvent(false, false);
		this.view.setErrorVisible(true);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void onEditRows() {
		if(this.queryResultEditor == null){
			this.queryResultEditor = ginInjector.createNewQueryResultEditorWidget();
			view.setEditorWidget(this.queryResultEditor);
		}
		this.queryResultEditor.showEditor(filteredResultsQueryBundle, tableType, new Callback() {
			@Override
			public void invoke() {
				cachedFullQueryResultBundle = null;
				runQuery();
			}
		});
	}

	@Override
	public void onPageChange(Long newOffset) {
		this.startingQuery.setOffset(newOffset);
		if (!isLoadAllPages) {
			queryChanging();	
		} else {
			//filteredResultsQueryBundle has the filtered rows.
			//update the page view so that the rows shown are for the offset only.
			QueryResultBundle currentPage = deepCopyQueryBundle(filteredResultsQueryBundle);
			List<Row> rows = currentPage.getQueryResult().getQueryResults().getRows();
			List<Row> rowsToShow = new ArrayList<>();
			for (int i = startingQuery.getOffset().intValue(); i < (startingQuery.getOffset() + startingQuery.getLimit()) && i < rows.size(); i++) {
				rowsToShow.add(rows.get(i));
			}
			currentPage.getQueryResult().getQueryResults().setRows(rowsToShow);
			this.pageViewerWidget.configure(currentPage, this.startingQuery, sortItems, false, tableType, null, this, facetChangedHandler, resetFacetsHandler);
		}
	}
	
	private void runSql(String sql) {
		startingQuery.setSql(sql);
		startingQuery.setOffset(0L);
		queryChanging();
	}

	private void queryChanging() {
		if(this.queryListener != null){
			this.queryListener.onStartingNewQuery(this.startingQuery);
		}
		view.scrollTableIntoView();
		runQuery();
	}
	
	public Query getStartingQuery(){
		return this.startingQuery;
	}

	@Override
	public void onToggleSort(String header) {
		// This call will generate a new SQL string with the requested column toggled.
		synapseClient.toggleSortOnTableQuery(this.startingQuery.getSql(), header, new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				showError(caught);
			}

			@Override
			public void onSuccess(String sql) {
				runSql(sql);
			}});
	}
	
	public void setFacetsVisible(boolean visible) {
		facetsVisible = visible;
		pageViewerWidget.setFacetsVisible(visible);
	}
	
}
