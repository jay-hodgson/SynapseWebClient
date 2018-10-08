package org.sagebionetworks.web.unitclient.widget.table.v2.results.facets;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.repo.model.table.ColumnModel;
import org.sagebionetworks.repo.model.table.ColumnType;
import org.sagebionetworks.repo.model.table.FacetColumnRequest;
import org.sagebionetworks.repo.model.table.FacetColumnResult;
import org.sagebionetworks.repo.model.table.FacetColumnResultRange;
import org.sagebionetworks.repo.model.table.FacetColumnResultValueCount;
import org.sagebionetworks.repo.model.table.FacetColumnResultValues;
import org.sagebionetworks.repo.model.table.FacetType;
import org.sagebionetworks.web.client.PortalGinInjector;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.view.DivView;
import org.sagebionetworks.web.client.widget.entity.renderer.SingleButtonView;
import org.sagebionetworks.web.client.widget.table.v2.results.facets.FacetColumnResultDateRangeWidget;
import org.sagebionetworks.web.client.widget.table.v2.results.facets.FacetColumnResultRangeWidget;
import org.sagebionetworks.web.client.widget.table.v2.results.facets.FacetColumnResultSliderRangeWidget;
import org.sagebionetworks.web.client.widget.table.v2.results.facets.FacetColumnResultValuesWidget;
import org.sagebionetworks.web.client.widget.table.v2.results.facets.FacetsWidget;

import com.google.gwt.user.client.ui.IsWidget;

public class FacetsWidgetTest {
	@Mock
	DivView mockView;
	@Mock
	PortalGinInjector mockGinInjector;
	@Mock
	CallbackP<FacetColumnRequest> mockFacetChangedHandler;
	@Mock
	ColumnModel mockColumnModel;
	@Mock
	FacetColumnResultRange mockFacetColumnResultRange;
	@Mock
	FacetColumnResultValues mockFacetColumnResultValues;
	@Mock
	FacetColumnResultValueCount mockFacetResultValueCount;
	@Mock
	SingleButtonView mockClearButton;
	@Mock
	FacetColumnResultValuesWidget mockFacetColumnResultValuesWidget;
	@Mock
	FacetColumnResultSliderRangeWidget mockFacetColumnResultSliderRangeWidget;
	@Mock
	FacetColumnResultRangeWidget mockFacetColumnResultRangeWidget;
	@Mock
	FacetColumnResultDateRangeWidget mockFacetColumnResultDateRangeWidget;
	
	FacetsWidget widget;
	List<FacetColumnResult> facets;
	List<ColumnModel> columnModels;
	List<FacetColumnResultValueCount> facetValues;
	public static final String COLUMN_NAME = "a column";
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		widget = new FacetsWidget(mockView, mockClearButton, mockGinInjector);
		verify(mockView).addStyleName(anyString());
		facets = new ArrayList<FacetColumnResult>();
		columnModels = new ArrayList<ColumnModel>();
		facetValues = new ArrayList<FacetColumnResultValueCount>();
		columnModels.add(mockColumnModel);
		when(mockFacetColumnResultValues.getFacetValues()).thenReturn(facetValues);
		when(mockFacetColumnResultValues.getFacetType()).thenReturn(FacetType.enumeration);
		when(mockFacetColumnResultRange.getFacetType()).thenReturn(FacetType.range);
		
		when(mockGinInjector.getFacetColumnResultValuesWidget()).thenReturn(mockFacetColumnResultValuesWidget);
		when(mockGinInjector.getFacetColumnResultSliderRangeWidget()).thenReturn(mockFacetColumnResultSliderRangeWidget);
		when(mockGinInjector.getFacetColumnResultRangeWidget()).thenReturn(mockFacetColumnResultRangeWidget);
		when(mockGinInjector.getFacetColumnResultDateRangeWidget()).thenReturn(mockFacetColumnResultDateRangeWidget);
		
		when(mockFacetColumnResultRange.getColumnMin()).thenReturn("3");
		when(mockFacetColumnResultRange.getColumnMax()).thenReturn("120");
		when(mockColumnModel.getName()).thenReturn(COLUMN_NAME);
		when(mockFacetColumnResultRange.getColumnName()).thenReturn(COLUMN_NAME);
		when(mockFacetColumnResultValues.getColumnName()).thenReturn(COLUMN_NAME);
	}

	@Test
	public void testAsWidget() {
		widget.asWidget();
		verify(mockView).asWidget();
	}
}
