package org.sagebionetworks.web.server.servlet;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.sagebionetworks.web.client.SearchService;
import org.sagebionetworks.web.server.ColumnConfigProvider;
import org.sagebionetworks.web.server.RestTemplateProvider;
import org.sagebionetworks.web.server.ServerConstants;
import org.sagebionetworks.web.server.UrlTemplateUtil;
import org.sagebionetworks.web.shared.HeaderData;
import org.sagebionetworks.web.shared.SearchParameters;
import org.sagebionetworks.web.shared.SearchParameters.FromType;
import org.sagebionetworks.web.shared.TableResults;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SearchServiceImpl extends RemoteServiceServlet implements
		SearchService {

	private static Logger logger = Logger.getLogger(SearchServiceImpl.class.getName());

	/**
	 * The template is injected with Gin
	 */
	private RestTemplateProvider templateProvider;

	/**
	 * Injected with Gin
	 */
	private ColumnConfigProvider columnConfig;

	private Map<String, List<String>> defaultColumns = new TreeMap<String, List<String>>();
	private String rootUrl;

	/**
	 * Injected via Gin.
	 * 
	 * @param template
	 */
	@Inject
	public void setRestTemplate(RestTemplateProvider template) {
		this.templateProvider = template;
	}

	/**
	 * Injected via Gin
	 * 
	 * @param columnConfig
	 */
	@Inject
	public void setColunConfigProvider(ColumnConfigProvider columnConfig) {
		this.columnConfig = columnConfig;
	}

	/**
	 * Injected via Guice from the ServerConstants.properties file.
	 * 
	 * @param url
	 */
	@Inject
	public void setRootUrl(
			@Named(ServerConstants.KEY_REST_API_ROOT_URL) String url) {
		this.rootUrl = url;
		logger.info("rootUrl:" + this.rootUrl);

	}

	/**
	 * Injects the default columns
	 * 
	 * @param defaults
	 */
	@Inject
	public void setDefaultDatasetColumns(
			@Named(ServerConstants.KEY_DEFAULT_DATASET_COLS) String defaults) {
		// convert from a string to a list
		String[] split = defaults.split(",");
		List<String> keyList = new LinkedList<String>();
		for (int i = 0; i < split.length; i++) {
			keyList.add(split[i].trim());
		}
		// Add this list to the map
		defaultColumns.put(FromType.dataset.name(), keyList);
	}

	@Override
	public TableResults executeSearch(SearchParameters params) {
		if(params == null) throw new IllegalArgumentException("Parameters cannot be null");
		if(params.fetchType() == null) throw new IllegalArgumentException("SearchParameters.fetchType() returned null");
		TableResults results = new TableResults();
		// The API expects an offset of 1 for the first element

		// First we need to determine which columns are visible
		List<String> visible = getVisibleColumns(params.getFromType(),
				params.getSelectColumns());
		// Given what is visible, we need to determine what is required.
		// This includes non-visible columns that the visible columns depend on.
		List<String> allRequired = columnConfig.addAllDependancies(visible);
		// Get the columns data for all required columns
		List<HeaderData> allColumnHeaderData = getColumnsForResults(allRequired);

		// Execute the query

		// Build the uri from the parameters
		URI uri = QueryStringUtils.writeQueryUri(rootUrl, params);
		logger.info("Url GET: " + uri.toString());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>("", headers);

		// Make the actual call.
		ResponseEntity<Object> response = templateProvider.getTemplate()
				.exchange(uri, HttpMethod.GET, entity, Object.class);
		LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response
				.getBody();
		List<Map<String, Object>> rows = (List<Map<String, Object>>) body
				.get(KEY_RESULTS);
		// Before we set the rows we need to validate types
		Map<String, HeaderData> allColumnsHeaderMap = createMap(allColumnHeaderData);
		rows = TypeValidation.validateTypes(rows, allColumnsHeaderMap);
		// Set the resulting rows.
		results.setRows(rows);
		results.setTotalNumberResults((Integer) body
				.get(KEY_TOTAL_NUMBER_OF_RESULTS));
		
		// The last step is to process all of the url templates
		UrlTemplateUtil.processUrlTemplates(allColumnHeaderData, rows);

		// Create the list of visible column headers to return to the caller
		List<HeaderData> visibleHeaders = getColumnsForResults(visible);
		results.setColumnInfoList(visibleHeaders);

		return results;
	}
	
	/**
	 * Create a map of column headers to their id.
	 * @param list
	 * @return
	 */
	private Map<String, HeaderData> createMap(List<HeaderData> list){
		Map<String, HeaderData> map = new TreeMap<String, HeaderData>();
		if(list != null){
			for(HeaderData header: list){
				map.put(header.getId(), header);
			}			
		}
		return map;
	}


	/**
	 * If the select columns are null or empty then the defaults will be used.
	 * 
	 * @param selectColumns
	 * @return
	 */
	public List<String> getVisibleColumns(String type,
			List<String> selectColumns) {
		if (selectColumns == null || selectColumns.size() < 1) {
			// We need a new list
			selectColumns = getDefaultColumnIds(type);
		}
		// Now add all of the
		// We also need to add any dependencies
		return selectColumns;
	}

	/**
	 * Build up the columsn base on the select clause.
	 * 
	 * @param selectColumns
	 * @return
	 */
	public List<HeaderData> getColumnsForResults(List<String> selectColumns) {
		if (selectColumns == null)
			throw new IllegalArgumentException("Select columns cannot be null");
		// First off, if the select columns are null or empty then we use the
		// defaults
		List<HeaderData> results = new ArrayList<HeaderData>();
		// Lookup each column
		for (int i = 0; i < selectColumns.size(); i++) {
			String selectKey = selectColumns.get(i);
			HeaderData data = columnConfig.get(selectKey);
			if (data == null)
				throw new IllegalArgumentException(
						"Cannot find data for column id: " + selectKey);
			results.add(data);
		}
		return results;
	}

	public List<String> getDefaultColumnIds(String type) {
		return defaultColumns.get(type);
	}

}
