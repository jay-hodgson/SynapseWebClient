package org.sagebionetworks.web.unitclient.widget.provenance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.repo.model.EntityHeader;
import org.sagebionetworks.repo.model.FileEntity;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.Reference;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.provenance.Activity;
import org.sagebionetworks.repo.model.provenance.Used;
import org.sagebionetworks.repo.model.provenance.UsedEntity;
import org.sagebionetworks.repo.model.request.ReferenceList;
import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.org.json.AdapterFactoryImpl;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.GlobalApplicationState;
import org.sagebionetworks.web.client.SynapseClientAsync;
import org.sagebionetworks.web.client.SynapseJSNIUtils;
import org.sagebionetworks.web.client.cache.ClientCache;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.transform.JsoProvider;
import org.sagebionetworks.web.client.widget.lazyload.LazyLoadHelper;
import org.sagebionetworks.web.client.widget.provenance.ProvenanceWidget;
import org.sagebionetworks.web.client.widget.provenance.ProvenanceWidgetView;
import org.sagebionetworks.web.client.widget.provenance.nchart.NChartCharacters;
import org.sagebionetworks.web.client.widget.provenance.nchart.NChartLayersArray;
import org.sagebionetworks.web.shared.PaginatedResults;
import org.sagebionetworks.web.shared.WidgetConstants;
import org.sagebionetworks.web.shared.WikiPageKey;
import org.sagebionetworks.web.shared.exceptions.NotFoundException;
import org.sagebionetworks.web.shared.provenance.ActivityGraphNode;
import org.sagebionetworks.web.shared.provenance.EntityGraphNode;
import org.sagebionetworks.web.shared.provenance.ProvGraph;
import org.sagebionetworks.web.shared.provenance.ProvGraphEdge;
import org.sagebionetworks.web.shared.provenance.ProvGraphNode;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;
import org.sagebionetworks.web.unitclient.widget.provenance.nchart.JsoProviderTestImpl;

import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProvenanceWidgetTest {
		
	ProvenanceWidget provenanceWidget;
	ProvenanceWidgetView mockView;
	AuthenticationController mockAuthController;
	AdapterFactory adapterFactory;
	SynapseClientAsync mockSynapseClient;
	ClientCache mockClientCache;
	GlobalApplicationState mockGlobalAppState;
	
	FileEntity outputEntity;
	String entity456Id = "syn456";
	PaginatedResults<EntityHeader> referenceHeaders;
	
	ReferenceList referenceList;
	Exception someException = new Exception();
	WikiPageKey wikiKey = new WikiPageKey("", ObjectType.ENTITY.toString(), null);
	JsoProvider jsoProvider;
	Map<String, String> descriptor;
	Activity act;
	UserProfile modifiedByUserProfile;
	@Mock
	SynapseJSNIUtils mockSynapseJSNIUtils;
	@Mock
	LazyLoadHelper mockLazyLoadHelper;
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockView = mock(ProvenanceWidgetView.class);
		mockAuthController = mock(AuthenticationController.class);
		mockSynapseClient = mock(SynapseClientAsync.class);
		adapterFactory = new AdapterFactoryImpl();
		jsoProvider = new JsoProviderTestImpl();
		when(mockSynapseJSNIUtils.nChartlayout(any(NChartLayersArray.class), any(NChartCharacters.class))).thenReturn(jsoProvider.newLayoutResult());
		mockClientCache = mock(ClientCache.class);
		mockGlobalAppState = mock(GlobalApplicationState.class);
		provenanceWidget = new ProvenanceWidget(mockView, mockSynapseClient, mockGlobalAppState, mockAuthController, adapterFactory, mockSynapseJSNIUtils, jsoProvider, mockClientCache, mockLazyLoadHelper);
		verify(mockView).setPresenter(provenanceWidget);
		
		outputEntity = new FileEntity();
		outputEntity.setId("syn123");
		outputEntity.setVersionNumber(1L);
		act = new Activity();
		act.setId("789");
		Reference ref123 = new Reference();
		ref123.setTargetId(outputEntity.getId());
		ref123.setTargetVersionNumber(outputEntity.getVersionNumber());
		Reference ref456 = new Reference();
		ref456.setTargetId(entity456Id);
		ref456.setTargetVersionNumber(1L);
		EntityHeader header123 = new EntityHeader();
		header123.setId(outputEntity.getId());
		header123.setVersionNumber(outputEntity.getVersionNumber());
		EntityHeader header456 = new EntityHeader();
		header456.setId(ref456.getTargetId());
		header456.setVersionNumber(ref456.getTargetVersionNumber());
		UsedEntity ue = new UsedEntity();
		ue.setReference(ref456);
		Set<Used> used = new HashSet<Used>();
		used.add(ue);
		act.setUsed(used);
		referenceList = new ReferenceList();
		referenceList.setReferences(new ArrayList<Reference>(Arrays.asList(new Reference[] { ref123, ref456 })));
		referenceHeaders = new PaginatedResults<EntityHeader>();
		referenceHeaders.setResults(new ArrayList<EntityHeader>(Arrays.asList(new EntityHeader[] { header456, header123 })));
		
		PaginatedResults<Reference> generatedBy = new PaginatedResults<Reference>();
		generatedBy.setResults(Arrays.asList(new Reference[] { ref123 }));		
		
		AsyncMockStubber.callSuccessWith(outputEntity).when(mockSynapseClient).getEntity(eq(outputEntity.getId()), any(AsyncCallback.class));
		AsyncMockStubber.callSuccessWith(act).when(mockSynapseClient).getActivityForEntityVersion(eq(outputEntity.getId()), eq(outputEntity.getVersionNumber()), any(AsyncCallback.class));
		AsyncMockStubber.callSuccessWith(referenceHeaders).when(mockSynapseClient).getEntityHeaderBatch(any(ReferenceList.class), any(AsyncCallback.class));		
		AsyncMockStubber.callSuccessWith(generatedBy).when(mockSynapseClient).getEntitiesGeneratedBy(eq(act.getId()), anyInt(), anyInt(), any(AsyncCallback.class));
		
		descriptor = new HashMap<String, String>();
		String depth = "1";
		String showExpand = "true";
		String displayHeight = "500";
		descriptor.put(WidgetConstants.PROV_WIDGET_DEPTH_KEY, depth);
		descriptor.put(WidgetConstants.PROV_WIDGET_ENTITY_LIST_KEY, outputEntity.getId());
		descriptor.put(WidgetConstants.PROV_WIDGET_EXPAND_KEY, showExpand);
		descriptor.put(WidgetConstants.PROV_WIDGET_DISPLAY_HEIGHT_KEY, displayHeight);		
		descriptor.put(WidgetConstants.PROV_WIDGET_UNDEFINED_KEY, Boolean.toString(true));
		
		modifiedByUserProfile = new UserProfile();
		modifiedByUserProfile.setUserName("007");
		modifiedByUserProfile.setFirstName("James");
		AsyncMockStubber.callSuccessWith("").when(mockSynapseClient).getUserProfile(anyString(), any(AsyncCallback.class));

	}
	
	@Test
	public void testAsWidget(){
		provenanceWidget.asWidget();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBuildGraphSuccess() throws Exception {				
		provenanceWidget.configure(null, descriptor, null, null);	
		ProvGraph graph = verifyBuildGraphCalls();					
		verifySuccessGraphStructure(graph);
	}

	private void verifySuccessGraphStructure(ProvGraph graph) {
		assertNotNull(graph);
		ProvGraphNode node123 = null;
		ProvGraphNode node456 = null;
		ProvGraphNode nodeAct = null;
		for(ProvGraphNode node : graph.getNodes()) {
			if(node instanceof EntityGraphNode) {
				if(((EntityGraphNode) node).getEntityId().equals(outputEntity.getId())) 
					node123 = node;
				else if(((EntityGraphNode) node).getEntityId().equals(entity456Id))
					node456 = node;
			} else if(node instanceof ActivityGraphNode) {
				if(((ActivityGraphNode) node).getActivityId().equals(act.getId()))
					nodeAct = node;
			}
		}
		assertNotNull(node123);
		assertNotNull(node456);
		assertNotNull(nodeAct);
		
		assertTrue(graph.getEdges().contains(new ProvGraphEdge(node123, nodeAct)));
		assertTrue(graph.getEdges().contains(new ProvGraphEdge(nodeAct, node456)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBuildTreeFailGetActivity() throws Exception {
		AsyncMockStubber.callFailureWith(someException).when(mockSynapseClient).getActivityForEntityVersion(eq(outputEntity.getId()), eq(outputEntity.getVersionNumber()), any(AsyncCallback.class));
		
		provenanceWidget.configure(null, descriptor, null, null);	
		verify(mockSynapseClient).getActivityForEntityVersion(eq(outputEntity.getId()), eq(outputEntity.getVersionNumber()), any(AsyncCallback.class));		
		ProvGraph graph = captureGraph();
		
		assertNotNull(graph);
		assertEquals(1, graph.getEdges().size());		
		assertEquals(3, graph.getNodes().size());
		ProvGraphNode node123 = null;
		ActivityGraphNode nodeAct = null;
		for(ProvGraphNode node : graph.getNodes()) {
			if(node instanceof EntityGraphNode) {
				if(((EntityGraphNode) node).getEntityId().equals(outputEntity.getId())) 
					node123 = node;
			} else if(node instanceof ActivityGraphNode) {				
					nodeAct = (ActivityGraphNode) node;
			}
		}
		assertNotNull(node123);
		assertNotNull(nodeAct);
		assertEquals(DisplayConstants.ERROR_PROVENANCE_RELOAD, nodeAct.getActivityName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBuildTreeFailGetActivity404() throws Exception {
		AsyncMockStubber.callFailureWith(new NotFoundException()).when(mockSynapseClient).getActivityForEntityVersion(eq(outputEntity.getId()), eq(outputEntity.getVersionNumber()), any(AsyncCallback.class));
		
		provenanceWidget.configure(null, descriptor, null, null);	
		verify(mockSynapseClient).getActivityForEntityVersion(eq(outputEntity.getId()), eq(outputEntity.getVersionNumber()), any(AsyncCallback.class));
		verify(mockSynapseClient).getEntityHeaderBatch(any(ReferenceList.class), any(AsyncCallback.class));
		ProvGraph graph = captureGraph();
		
		assertNotNull(graph);
		assertEquals(1, graph.getEdges().size());		
		assertEquals(3, graph.getNodes().size());
		ProvGraphNode node123 = null;
		ActivityGraphNode nodeAct = null;
		for(ProvGraphNode node : graph.getNodes()) {
			if(node instanceof EntityGraphNode) {
				if(((EntityGraphNode) node).getEntityId().equals(outputEntity.getId())) 
					node123 = node;
			} else if(node instanceof ActivityGraphNode) {				
					nodeAct = (ActivityGraphNode) node;
			}
		}
		assertNotNull(node123);
		assertNotNull(nodeAct);
		assertTrue(nodeAct.getActivityName().contains(DisplayConstants.NOT_FOUND));
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBuildTreeFailHeaderBatch() throws Exception {
		AsyncMockStubber.callFailureWith(someException).when(mockSynapseClient).getEntityHeaderBatch(any(ReferenceList.class), any(AsyncCallback.class));
		
		provenanceWidget.configure(null, descriptor, null, null);	
		verify(mockSynapseClient).getActivityForEntityVersion(eq(outputEntity.getId()), eq(outputEntity.getVersionNumber()), any(AsyncCallback.class));
		verify(mockSynapseClient).getEntityHeaderBatch(eq(referenceList), any(AsyncCallback.class));
		ProvGraph graph = captureGraph();

		verifySuccessGraphStructure(graph);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFindOldVersionsNotFoundException() throws Exception {
		SynapseJSNIUtils mockJsniUtils = mock(SynapseJSNIUtils.class);
		when(mockJsniUtils.nChartlayout(any(NChartLayersArray.class), any(NChartCharacters.class))).thenReturn(jsoProvider.newLayoutResult());
		provenanceWidget = new ProvenanceWidget(mockView, mockSynapseClient, mockGlobalAppState, mockAuthController, adapterFactory, mockJsniUtils, jsoProvider, mockClientCache, mockLazyLoadHelper);
		
		String message = "entity syn999 was not found";
		AsyncMockStubber.callFailureWith(new NotFoundException(message)).when(mockSynapseClient).getEntityHeaderBatch(any(ReferenceList.class), any(AsyncCallback.class));
		// create graph
		provenanceWidget.configure(null, descriptor, null, null);	
		
		provenanceWidget.findOldVersions();
		
		//send error to the console - silent error
		verify(mockJsniUtils).consoleError(anyString());
	}
		
	@Test
	public void testFindOldVersions() throws Exception {
		// create graph
		provenanceWidget.configure(null, descriptor, null, null);	
		ProvGraph graph = verifyBuildGraphCalls();					
		
		reset(mockSynapseClient);
		
		// current version of each reference
		EntityHeader header123 = new EntityHeader();
		header123.setId(outputEntity.getId());
		header123.setVersionNumber(outputEntity.getVersionNumber());
		EntityHeader header456 = new EntityHeader();
		header456.setId(entity456Id);
		header456.setVersionNumber(2L); // v2 is newer than in Before method
		PaginatedResults<EntityHeader> currentVersionBatch = new PaginatedResults<EntityHeader>();
		currentVersionBatch.setResults(new ArrayList<EntityHeader>(Arrays.asList(new EntityHeader[] { header456, header123 })));		
		// new mocks for batch call
		AsyncMockStubber.callSuccessWith(currentVersionBatch).when(mockSynapseClient).getEntityHeaderBatch(any(ReferenceList.class), any(AsyncCallback.class));

		provenanceWidget.findOldVersions();
		
		@SuppressWarnings("rawtypes")
		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);		
		verify(mockView).markOldVersions(argument.capture());		
		@SuppressWarnings("unchecked")
		List<String> oldVersions = (List<String>)argument.getValue();
		
		assertEquals(1, oldVersions.size());	
	}

	private ProvGraph verifyBuildGraphCalls() throws Exception {
		verify(mockSynapseClient).getActivityForEntityVersion(eq(outputEntity.getId()), eq(outputEntity.getVersionNumber()), any(AsyncCallback.class));
		verify(mockSynapseClient).getEntityHeaderBatch(eq(referenceList), any(AsyncCallback.class));		
		return captureGraph();
	}

	private ProvGraph captureGraph() {
		ArgumentCaptor<ProvGraph> argument = ArgumentCaptor.forClass(ProvGraph.class);		
		verify(mockView).setGraph(argument.capture());
		ProvGraph graph = argument.getValue();
		return graph;
	}

}












