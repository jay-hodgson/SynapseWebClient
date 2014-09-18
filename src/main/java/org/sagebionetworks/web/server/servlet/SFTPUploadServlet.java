package org.sagebionetworks.web.server.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.sagebionetworks.client.SynapseClient;
import org.sagebionetworks.web.shared.WebConstants;

import com.google.inject.Inject;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Handles sftp file upload (streams content through proxy, does not store).
 *
 * @author jay
 *
 */
public class SFTPUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private JSch jsch = new JSch();
	
	protected static final ThreadLocal<HttpServletRequest> perThreadRequest = new ThreadLocal<HttpServletRequest>();
	
	/**
	 * Injected with Gin
	 */
	@SuppressWarnings("unused")
	private ServiceUrlProvider urlProvider;
	private SynapseProvider synapseProvider = new SynapseProviderImpl();
	private TokenProvider tokenProvider = new TokenProvider() {
		@Override
		public String getSessionToken() {
			return UserDataProvider.getThreadLocalUserToken(SFTPUploadServlet.perThreadRequest.get());
		}
	};

	/**
	 * Unit test can override this.
	 *
	 * @param provider
	 */
	public void setSynapseProvider(SynapseProvider synapseProvider) {
		this.synapseProvider = synapseProvider;
	}

	/**
	 * Essentially the constructor. Setup synapse client.
	 *
	 * @param provider
	 */
	@Inject
	public void setServiceUrlProvider(ServiceUrlProvider provider) {
		this.urlProvider = provider;
	}

	/**
	 * Unit test uses this to provide a mock token provider
	 *
	 * @param tokenProvider
	 */
	public void setTokenProvider(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		SFTPUploadServlet.perThreadRequest.set(arg0);
		super.service(arg0, arg1);
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		super.service(arg0, arg1);
	}

	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		throw new ServletException("SFTP upload servlet does not support GET");
	}

	
	public void sftpUploadFile(HttpServletRequest request) throws FileUploadException, IOException, ServletException {
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter = upload.getItemIterator(request);
		
		String username = request.getParameter(WebConstants.SFTP_USERNAME_PARAM_KEY);
		String password = request.getParameter(WebConstants.SFTP_PASSWORD_PARAM_KEY);
		int port = Integer.parseInt(request.getParameter(WebConstants.SFTP_PORT_PARAM_KEY));
		
		while (iter.hasNext()) {
			//should be one in this case
			FileItemStream item = iter.next();
			String name = item.getFieldName();
			InputStream stream = item.openStream();
			String fileName = item.getName();
			if (fileName.contains("\\")){
				fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
			}
			
			Session session = null;
	        try {
	            session = jsch.getSession(username, password, port);
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.setPassword(password);
	            session.connect();

	            Channel channel = session.openChannel("sftp");
	            channel.connect();
	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            sftpChannel.put(stream, fileName);
	            sftpChannel.exit();
	        } catch (JSchException e) {
	        	throw new ServletException(e);
	        } catch (SftpException e) {
	        	throw new ServletException(e);
	        } finally {
	        	if (session != null)
	        		session.disconnect();
	        }
		}
	}
	
	@Override
	public void doPost(final HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Before we do anything make sure we can get the users token
		String token = getSessionToken(request);
		if (token == null) {
			FileHandleServlet.setForbiddenMessage(response);
			return;
		}
		
		try {
			//Connect to synapse
			createNewClient(token);
			sftpUploadFile(request);
			FileHandleServlet.fillResponseWithSuccess(response, "");
		} catch (Exception e) {
			FileHandleServlet.fillResponseWithFailure(response, e);
			return;
		}
	}
	
	/**
	 * Get the session token
	 * @param request
	 * @return
	 */
	public String getSessionToken(final HttpServletRequest request){
		return tokenProvider.getSessionToken();
	}


	/**
	 * Create a new Synapse client.
	 *
	 * @return
	 */
	private SynapseClient createNewClient(String sessionToken) {
		SynapseClient client = synapseProvider.createNewClient();
		client.setAuthEndpoint(urlProvider.getPrivateAuthBaseUrl());
		client.setRepositoryEndpoint(urlProvider.getRepositoryServiceUrl());
		if (sessionToken != null)
			client.setSessionToken(sessionToken);
		return client;
	}
}
