package org.sagebionetworks.web.unitserver.filter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.sagebionetworks.web.server.servlet.filter.CORSFilter.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sagebionetworks.web.server.servlet.filter.CORSFilter;

@RunWith(MockitoJUnitRunner.class)
public class CORSFilterTest {
	CORSFilter filter;
	@Mock
	HttpServletRequest mockRequest;
	@Mock
	HttpServletResponse mockResponse;
	@Mock
	FilterChain mockFilterChain;
	@Captor
	ArgumentCaptor<String> stringCaptor;
	
	@Before
	public void setUp() {
		filter = new CORSFilter();
		when(mockRequest.getHeader(ORIGIN_HEADER)).thenReturn("https://tst" + SYNAPSE_ORG_SUFFIX); //https://tst.synapse.org
		when(mockRequest.getMethod()).thenReturn(OPTIONS); //preflight
		when(mockRequest.getHeader(ACCESS_CONTROL_REQUEST_METHOD)).thenReturn("GET");
	}
	
	@Test
	public void testSynapseOrg() throws ServletException, IOException{
		//we are in a .synapse.org subdomain
		// also request to allow the "credentials" header in the request
		when(mockRequest.getHeader(ACCESS_CONTROL_REQUEST_HEADERS)).thenReturn("credentials");
		
		filter.testFilter(mockRequest, mockResponse, mockFilterChain);
		
		//verify allow origin header set to the specific origin
		verify(mockResponse).addHeader(eq(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER), stringCaptor.capture());
		String allowOriginHeaderValue = stringCaptor.getValue();
		assertEquals("https://tst.synapse.org", allowOriginHeaderValue);
		
		// and Access-Control-Allow-Credentials is set to true
		verify(mockResponse).addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
		// verify our additional requested header is included
		verify(mockResponse).addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, credentials");
		verify(mockResponse).addHeader(ACCESS_CONTROL_ALLOW_METHODS, ALL_METHODS);
	}

	@Test
	public void testNotSynapseOrg() throws ServletException, IOException{
		//we are not in a .synapse.org subdomain
		when(mockRequest.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER)).thenReturn(Boolean.TRUE.toString());
		when(mockRequest.getHeader(ORIGIN_HEADER)).thenReturn("tst.notsynapse.org"); 
		when(mockRequest.getHeader(ACCESS_CONTROL_REQUEST_HEADERS)).thenReturn("credentials");
		
		filter.testFilter(mockRequest, mockResponse, mockFilterChain);
		
		//verify allow origin header set to * (causes CORS preflight to fail browserside)
		verify(mockResponse).addHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, DEFAULT_ALLOW_ORIGIN);
		// and Access-Control-Allow-Credentials is not added to the response
		verify(mockResponse, never()).addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
		//custom requested header (credentials) not allowed
		verify(mockResponse).addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
	}
}
