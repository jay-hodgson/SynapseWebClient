package org.sagebionetworks.web.client.presenter;

import java.util.Arrays;

import org.sagebionetworks.repo.model.search.query.SearchQuery;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.web.client.ClientProperties;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.place.Search;
import org.sagebionetworks.web.client.place.Synapse;
import org.sagebionetworks.web.shared.SearchQueryUtils;

import com.google.gwt.http.client.URL;

/**
 * This logic was removed from the search presenter so we could make a clean SearchPresenterProxy.
 * 
 * @author John
 *
 */
public class SearchUtil {
	
	/**
	 * If this returns a Synapse place then we should redirect to an entity page
	 * @param place
	 * @return
	 */
	public static Synapse willRedirect(Search place) {
		String queryTerm = place.getSearchTerm();
		if (queryTerm == null) queryTerm = "";
		return willRedirect(queryTerm);
	}
	/**
	 * If this returns a Synapse place then we should redirect to an entity page
	 * @param queryTerm
	 * @return
	 */
	public static Synapse willRedirect(String queryTerm) {
		if(queryTerm.startsWith(ClientProperties.SYNAPSE_ID_PREFIX)) {
			String remainder = queryTerm.replaceFirst(ClientProperties.SYNAPSE_ID_PREFIX, "");
			if(remainder.matches("^[0-9]+$")) {
				return new Synapse(queryTerm);
			}
		}
		return null;
	}
	public static void searchForTerm(
			String queryTerm, 
			GlobalApplicationState globalApplicationState, 
			JSONObjectAdapter jsonObjectAdapter,
			GWTWrapper gwt,
			boolean allTypes) {
		Synapse synapsePlace = willRedirect(queryTerm);
		if (synapsePlace == null) {
			try {
				SearchQuery query;
				if (allTypes) {
					query = SearchQueryUtils.getAllTypesSearchQuery();
				} else {
					query = SearchQueryUtils.getDefaultSearchQuery();
				}
				query.setQueryTerm(Arrays.asList(queryTerm.split(" ")));
				String json = query.writeToJSONObject(jsonObjectAdapter.createNew()).toJSONString();
				Search searchPlace = new Search(gwt.encode(json));
				//no potential redirect, go directly to search!
				globalApplicationState.getPlaceChanger().goTo(searchPlace);
			} catch (JSONObjectAdapterException e) {
				globalApplicationState.getPlaceChanger().goTo(new Search(queryTerm));
			}	
		} else {
			globalApplicationState.getPlaceChanger().goTo(synapsePlace);
		}
	}

}
