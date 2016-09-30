package org.sagebionetworks.web.unitclient.widget.entity.renderer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.evaluation.model.Evaluation;
import org.sagebionetworks.repo.model.auth.LoginRequest;
import org.sagebionetworks.web.client.ChallengeClientAsync;
import org.sagebionetworks.web.client.utils.Callback;
import org.sagebionetworks.web.client.widget.entity.controller.SynapseAlert;
import org.sagebionetworks.web.client.widget.entity.renderer.APITableWidget;
import org.sagebionetworks.web.client.widget.entity.renderer.LeaderboardWidget;
import org.sagebionetworks.web.client.widget.entity.renderer.LeaderboardWidgetView;
import org.sagebionetworks.web.shared.PaginatedResults;
import org.sagebionetworks.web.shared.WidgetConstants;
import org.sagebionetworks.web.shared.WikiPageKey;
import org.sagebionetworks.web.test.helper.AsyncMockStubber;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class LeaderboardWidgetTest {
	
	LeaderboardWidget widget;
	@Mock
	LeaderboardWidgetView mockView;
	@Mock
	APITableWidget mockApiTableWidget; 
	@Mock
	ChallengeClientAsync mockChallengeClient;
	@Mock
	SynapseAlert mockSynAlert;
	
	@Mock
	WikiPageKey wikiKey;
	@Mock
	Map<String, String> widgetDescriptor;
	@Mock
	Callback widgetRefreshRequired;
	Long wikiVersionInView = 2L;
	@Mock
	PaginatedResults<Evaluation> results;
	@Mock
	Evaluation evaluation1;
	@Mock
	Evaluation evaluation2;
	
	List<Evaluation> evaluationList;
	public static final String EVAL_1_ID = "111";
	public static final String EVAL_2_ID = "222";
	public static final String EVAL_1_NAME = "eval 1";
	public static final String EVAL_2_NAME = "eval 2";
	public static final String PROJECT_ID = "syn456789";
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		widget = new LeaderboardWidget(mockView, mockApiTableWidget, mockChallengeClient, mockSynAlert);
		evaluationList = new ArrayList<Evaluation>();
		when(results.getResults()).thenReturn(evaluationList);
		AsyncMockStubber.callSuccessWith(results).when(mockChallengeClient).getProjectEvaluations(anyString(), any(AsyncCallback.class));
		when(evaluation1.getId()).thenReturn(EVAL_1_ID);
		when(evaluation2.getId()).thenReturn(EVAL_2_ID);
		when(evaluation1.getName()).thenReturn(EVAL_1_NAME);
		when(evaluation2.getName()).thenReturn(EVAL_2_NAME);
		
	}
	
	@Test
	public void testConstruction() {
		verify(mockView).setPresenter(widget);
		verify(mockView).setAPITable(any(Widget.class));
		verify(mockView).setSynAlert(any(Widget.class));
	}

	@Test
	public void testConfigureWithoutProjectId() {
		widget.configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
		verify(mockSynAlert).clear();
		verify(mockView).setEvaluationSelectionVisible(false);
		verify(mockApiTableWidget).configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
	}

	@Test
	public void testConfigureWithProjectIdNoEvaluations() {
		when(widgetDescriptor.get(WidgetConstants.PROJECT_ID_KEY)).thenReturn(PROJECT_ID);
		widget.configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
		verify(mockSynAlert).clear();
		verify(mockView).setEvaluationSelectionVisible(false);
		verify(mockChallengeClient).getProjectEvaluations(eq(PROJECT_ID), any(AsyncCallback.class));
		// fall back to just showing the super table
		verify(mockApiTableWidget).configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
	}
	
	@Test
	public void testConfigureWithProjectIdOneEvaluation() {
		when(widgetDescriptor.get(WidgetConstants.PROJECT_ID_KEY)).thenReturn(PROJECT_ID);
		evaluationList.add(evaluation1);
		
		String oldURI = "/evaluation/submission/query?query=select+*+from evaluation_12345+where+foo==bar";
		String expectedNewURI = "/evaluation/submission/query?query=select+*+from evaluation_111+where+foo==bar";
		when(widgetDescriptor.get(WidgetConstants.API_TABLE_WIDGET_PATH_KEY)).thenReturn(oldURI);
		
		widget.configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
		verify(mockView).addEvalation(EVAL_1_NAME);
		verify(mockSynAlert, times(2)).clear();
		verify(mockView).setEvaluationSelectionVisible(false);
		verify(mockView, never()).setEvaluationSelectionVisible(true);
		verify(mockChallengeClient).getProjectEvaluations(eq(PROJECT_ID), any(AsyncCallback.class));
		
		//verify URI is modified
		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		verify(widgetDescriptor).put(eq(WidgetConstants.API_TABLE_WIDGET_PATH_KEY), stringCaptor.capture());
		assertEquals(expectedNewURI, stringCaptor.getValue());
		verify(mockApiTableWidget).configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
	}

	@Test
	public void testConfigureWithProjectIdTwoEvaluations() {
		when(widgetDescriptor.get(WidgetConstants.PROJECT_ID_KEY)).thenReturn(PROJECT_ID);
		evaluationList.add(evaluation1);
		evaluationList.add(evaluation2);
		String oldURI = "/evaluation/submission/query?query=select+*+from+evaluation_12345";
		String expectedNewURI = "/evaluation/submission/query?query=select+*+from+evaluation_111";
		when(widgetDescriptor.get(WidgetConstants.API_TABLE_WIDGET_PATH_KEY)).thenReturn(oldURI);
		
		widget.configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
		verify(mockSynAlert, times(2)).clear();
		verify(mockView).setEvaluationSelectionVisible(false);
		verify(mockView).setEvaluationSelectionVisible(true);
		verify(mockView).addEvalation(EVAL_1_NAME);
		verify(mockView).addEvalation(EVAL_2_NAME);
		verify(mockChallengeClient).getProjectEvaluations(eq(PROJECT_ID), any(AsyncCallback.class));
		
		//verify URI is modified
		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		verify(widgetDescriptor).put(eq(WidgetConstants.API_TABLE_WIDGET_PATH_KEY), stringCaptor.capture());
		assertEquals(expectedNewURI, stringCaptor.getValue());
		verify(mockApiTableWidget).configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
	}

	@Test
	public void testConfigureWithProjectIdRPCFailure() {
		when(widgetDescriptor.get(WidgetConstants.PROJECT_ID_KEY)).thenReturn(PROJECT_ID);
		Exception ex = new Exception("failure to get project evaluations");
		AsyncMockStubber.callFailureWith(ex).when(mockChallengeClient).getProjectEvaluations(anyString(), any(AsyncCallback.class));
		widget.configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
		InOrder inOrder = inOrder(mockSynAlert);
		inOrder.verify(mockSynAlert).clear();
		inOrder.verify(mockSynAlert).handleException(ex);
	}
	
	@Test
	public void testMissingEvaluationIdPrefix() {
		when(widgetDescriptor.get(WidgetConstants.PROJECT_ID_KEY)).thenReturn(PROJECT_ID);
		evaluationList.add(evaluation1);
		evaluationList.add(evaluation2);
		String oldURI = "/evaluation/submission/query?query=select+*+from+baz";
		when(widgetDescriptor.get(WidgetConstants.API_TABLE_WIDGET_PATH_KEY)).thenReturn(oldURI);
		widget.configure(wikiKey, widgetDescriptor, widgetRefreshRequired, wikiVersionInView);
		verify(mockSynAlert).showError(LeaderboardWidget.MISSING_EVALUATION_ID_IN_QUERY);
	}

	@Test
	public void testAsWidget() {
		widget.asWidget();
		verify(mockView).asWidget();
	}
}
