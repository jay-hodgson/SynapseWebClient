package org.sagebionetworks.web.unitclient.widget.search;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.web.client.GWTWrapper;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.PlaceChanger;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.place.Search;
import org.sagebionetworks.web.client.place.Synapse;
import org.sagebionetworks.web.client.presenter.SearchUtil;

public class SearchUtilTest {

	@Mock
	private GlobalApplicationState mockGlobalAppState;
	@Mock
	private PlaceChanger mockPlaceChanger;
	@Mock
	private GWTWrapper mockGWT;
	JSONObjectAdapterImpl jsonObjectAdapter;
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		jsonObjectAdapter = new JSONObjectAdapterImpl();
		when(mockGlobalAppState.getPlaceChanger()).thenReturn(mockPlaceChanger);
	}
	
	@Test
	public void testSearchForTerm() {
		//search for something that does not look like a Synapse ID
		SearchUtil.searchForTerm("not_an_id", mockGlobalAppState, jsonObjectAdapter, mockGWT, false);
		verify(mockPlaceChanger).goTo(isA(Search.class));
	}
	
	@Test
	public void testSearchForSynId() {
		//mocking successful verification of the syn id, should change to Synapse entity place
		SearchUtil.searchForTerm("syn123", mockGlobalAppState, jsonObjectAdapter, mockGWT, false);
		verify(mockPlaceChanger).goTo(isA(Synapse.class));
	}
}





