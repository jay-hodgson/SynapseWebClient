package org.sagebionetworks.web.client;

import java.util.Date;

import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.web.client.callback.MD5Callback;
import org.sagebionetworks.web.client.widget.provenance.nchart.LayoutResult;
import org.sagebionetworks.web.client.widget.provenance.nchart.LayoutResultJso;
import org.sagebionetworks.web.client.widget.provenance.nchart.NChartCharacters;
import org.sagebionetworks.web.client.widget.provenance.nchart.NChartLayersArray;
import org.sagebionetworks.web.shared.WebConstants;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class SynapseJSNIUtilsImpl implements SynapseJSNIUtils {
	
	private static ProgressCallback progressCallback;
	
	@Override
	public void recordPageVisit(String token) {
		_recordPageVisit(token);
	}

	private static native void _recordPageVisit(String token) /*-{
		$wnd.ga('set', 'page', '/#'+token);
		$wnd.ga('send', 'pageview');
	}-*/;
	
	@Override
	public String getCurrentHistoryToken() {
		return History.getToken();
	}

	@Override
	public void bindBootstrapTooltip(String id) {
		_bindBootstrapTooltip(id);
	}

	private static native void _bindBootstrapTooltip(String id) /*-{
		$wnd.jQuery('#'+id).tooltip().tooltip('fixTitle');	//update title from data-original-title, if necessary
	}-*/;

	@Override
	public void hideBootstrapTooltip(String id) {
		_hideBootstrapTooltip(id);
	}

	private static native void _hideBootstrapTooltip(String id) /*-{
		$wnd.jQuery('#'+id).tooltip('hide');
	}-*/;
	
	@Override
	public void bindBootstrapPopover(String id) {
		_bindBootstrapPopover(id);
	}
	
	@Override
	public void highlightCodeBlocks() {
		_highlightCodeBlocks();
	}
	
	public static native void _highlightCodeBlocks() /*-{
	  $wnd.jQuery('pre code').each(function(i, e) {$wnd.hljs.highlightBlock(e)});
	}-*/;
	
	@Override
	public void loadTableSorters() {
		_tablesorter();
	}
	
	private static native void _tablesorter() /*-{
		$wnd.jQuery('table.markdowntable').tablesorter();
	}-*/;
	
	private static native void _bindBootstrapPopover(String id) /*-{
		$wnd.jQuery('#'+id).popover();
	}-*/;
	
	private static native String _getRelativeTime(String s) /*-{
		return $wnd.moment(s).fromNow();
	}-*/;
	private static native String _getCalendarTime(String s) /*-{
		return $wnd.moment(s).calendar();
	}-*/;
	
	private static DateTimeFormat smallDateFormat = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm:ssaa ZZZ");
	private static DateTimeFormat longDateFormat = DateTimeFormat.getFormat("EEEE, MMMM d, yyyy h:mm a ZZZ");
	private static DateTimeFormat iso8601Format =  DateTimeFormat.getFormat(PredefinedFormat.ISO_8601);
	
	@Override
	public String convertDateToSmallString(Date toFormat) {
		return smallDateFormat.format(toFormat, GlobalApplicationStateImpl.currentTimezone);
	}
	@Override
	public String getRelativeTime(Date toFormat) {
		return _getRelativeTime(iso8601Format.format(toFormat));
	}
	@Override
	public String getCalendarTime(Date toFormat) {
		return _getCalendarTime(iso8601Format.format(toFormat));
	}
	
	@Override
	public String getLongFriendlyDate(Date toFormat) {
		return longDateFormat.format(toFormat, GlobalApplicationStateImpl.currentTimezone);
	}
	
	@Override
	public String getBaseFileHandleUrl() {
		return GWT.getModuleBaseURL()+"filehandle";
	}
	
	@Override
	public String getBaseProfileAttachmentUrl() {
		return GWT.getModuleBaseURL() + "profileAttachment";
	}
	
	@Override
	public String getFileHandleAssociationUrl(String objectId, FileHandleAssociateType objectType, String fileHandleId, String xsrfToken) {
		return GWT.getModuleBaseURL() + WebConstants.FILE_HANDLE_ASSOCIATION_SERVLET + "?" + 
				WebConstants.ASSOCIATED_OBJECT_ID_PARAM_KEY + "=" + objectId + "&" +
				WebConstants.ASSOCIATED_OBJECT_TYPE_PARAM_KEY + "=" + objectType.toString() + "&" + 
				WebConstants.FILE_HANDLE_ID_PARAM_KEY + "=" + fileHandleId + "&" +
				WebConstants.XSRF_TOKEN_KEY + "=" + xsrfToken;
	}

	@Override
	public int randomNextInt() {
		return Random.nextInt();
	}

	@Override
	public String getLocationPath() {
		return Location.getPath();
	}

	@Override
	public String getLocationQueryString() {
		return Location.getQueryString();
	}

	@Override
	public LayoutResult nChartlayout(NChartLayersArray layers,
			NChartCharacters characters) {		
		return _nChartlayout(layers, characters);
	}

	private final static native LayoutResultJso _nChartlayout(NChartLayersArray layers, NChartCharacters characters) /*-{	        
	    var debug = {'features': ['nodes'], 'wireframe': true};
		var conf = {'group_styles': {'pov': {'stroke-width': 3}},
	        'debug': debug};	        
		var chart = new $wnd.NChart(characters, layers, conf).calc().plot();
			
		// convert graph into LayoutResult
		var layoutResult = {}; 
		var ncGraph = chart.graph;
		for(var i=0; i<ncGraph.layers.length; i++) {		
			var ncLayer = ncGraph.layers[i];
			for(var j=0; j<ncLayer.nodes.length; j++) {
				var ncNode = ncLayer.nodes[j];
				var provGraphNodeId = ncNode.event;
				var xypoint = { 'x':ncNode.x, 'y':ncNode.y };
				if(!(provGraphNodeId in layoutResult)) { 
					layoutResult[provGraphNodeId] = [];
				}
				layoutResult[provGraphNodeId].push(xypoint);				
			}
		}		
		return layoutResult;
	}-*/;

	@Override
	public void setPageTitle(String newTitle) {
	    if (Document.get() != null) {
	        Document.get().setTitle (newTitle);
	    }
	}
	
	@Override
	public void setPageDescription(String newDescription) {
		if (Document.get() != null) {
			NodeList<com.google.gwt.dom.client.Element> tags = Document.get().getElementsByTagName("meta");
		    for (int i = 0; i < tags.getLength(); i++) {
		        MetaElement metaTag = ((MetaElement) tags.getItem(i));
		        if (metaTag.getName().equals("description")) {
		            metaTag.setContent(newDescription);	//doesn't seem to work
		            break;
		        }
		    }
		}
	}
	
	
	
	@Override
	public void uploadFileChunk(String contentType, JavaScriptObject blob, Long startByte, Long endByte, String url, XMLHttpRequest xhr, ProgressCallback callback) {
		SynapseJSNIUtilsImpl.progressCallback = callback;
		_directUploadBlob(contentType, blob, startByte, endByte, url, xhr);
	}
	
	@Override
	public JavaScriptObject getFileBlob(int index, String fileFieldId) {
		return _getFileBlob(index, fileFieldId);
	}
	
	private final static native JavaScriptObject _getFileBlob(int index, String fileFieldId) /*-{
		var fileToUploadElement = $doc.getElementById(fileFieldId);
		if (fileToUploadElement && 'files' in fileToUploadElement) {
			return fileToUploadElement.files[index];
		}
		return null;
	}-*/;
	
	private final static native void _directUploadBlob(String contentType, JavaScriptObject fileToUpload, Long startByte, Long endByte, String url, XMLHttpRequest xhr) /*-{
		var start = parseInt(startByte) || 0;
		var end = parseInt(endByte) || fileToUpload.size - 1;
		var fileSliceToUpload;
		//in versions later than Firefox 13 and Chrome 21, Blob.slice() is not prefixed (and the vendor prefixed methods are deprecated)
		if (fileToUpload.slice) {
        	fileSliceToUpload = fileToUpload.slice(start, end+1, contentType);
	    }else if (fileToUpload.mozSlice) {
        	fileSliceToUpload = fileToUpload.mozSlice(start, end+1, contentType);
	    } else if (fileToUpload.webkitSlice) {
	        fileSliceToUpload = fileToUpload.webkitSlice(start, end+1, contentType);
	    } else {
	        throw new Error("Unable to slice file.");
	    }
		xhr.upload.onprogress = $entry(@org.sagebionetworks.web.client.SynapseJSNIUtilsImpl::updateProgress(Lcom/google/gwt/core/client/JavaScriptObject;));
  		xhr.open('PUT', url, true);
  		//explicitly set content type
  		xhr.setRequestHeader('Content-type', contentType);
  		xhr.send(fileSliceToUpload);
	}-*/;
	
	
	public static void updateProgress(JavaScriptObject evt) {
		if (SynapseJSNIUtilsImpl.progressCallback != null) {
			SynapseJSNIUtilsImpl.progressCallback.updateProgress(_getLoaded(evt), _getTotal(evt));
		}
	}
	
	private final static native double _getLoaded(JavaScriptObject evt) /*-{
		if (evt.lengthComputable) {
			return evt.loaded;
		}
		return 0;
	}-*/;
	
	private final static native double _getTotal(JavaScriptObject evt) /*-{
		if (evt.lengthComputable) {
			return evt.total;
		}
		return 0;
	}-*/;
	
	@Override
	public String getContentType(String fileFieldId, int index) {
		return _getContentType(fileFieldId, index);
	}
	private final static native String _getContentType(String fileFieldId, int index) /*-{
		var fileToUploadElement = $doc.getElementById(fileFieldId);
		return fileToUploadElement.files[index].type;
	}-*/;
	
	@Override
	public double getFileSize(JavaScriptObject blob) {
		return _getFileSize(blob);
	}
	private final static native double _getFileSize(JavaScriptObject blob) /*-{
		return blob.size;
	}-*/;
	
	@Override
	public String[] getMultipleUploadFileNames(String fileFieldId) {
		String unSplitNames = _getFilesSelected(fileFieldId);
		if (unSplitNames.equals(""))
			return null;
		return unSplitNames.split(";");
	}
	
	private static native String _getFilesSelected(String fileFieldId) /*-{
		var fileToUploadElement = $doc.getElementById(fileFieldId);
	    var out = "";
		if (fileToUploadElement) {
		    for (i = 0; i < fileToUploadElement.files.length; i++) {
		        var file = fileToUploadElement.files[i];
		        out += file.name + ';';
		    }
		}
	    return out;
	}-*/;
	
	public boolean isElementExists(String elementId) {
		return Document.get().getElementById(elementId) != null;
	};
	
	/**
	 * Using SparkMD5 (https://github.com/satazor/SparkMD5) to (progressively by slicing the file) calculate the md5.
	 */
	@Override
	public void getFileMd5(JavaScriptObject blob, MD5Callback md5Callback) {
		_getFileMd5(blob, md5Callback);
	}
	private final static native void _getFileMd5(JavaScriptObject file, MD5Callback md5Callback) /*-{
		var blobSlice = file.slice || file.mozSlice || file.webkitSlice;
		chunkSize = 2097152; // read in chunks of 2MB
        chunks = Math.ceil(file.size / chunkSize);
        currentChunk = 0;
        spark = new $wnd.SparkMD5.ArrayBuffer();
        $wnd.frOnload = function(e) {
            console.log("read chunk nr", currentChunk + 1, "of", chunks);
            spark.append(e.target.result);                 // append array buffer
            currentChunk++;

            if (currentChunk < chunks) {
                $wnd.loadNext();
            }
            else {
               console.log("finished loading file (to calculate md5)");
               // Call instance method setMD5() on md5Callback with the final md5
    			md5Callback.@org.sagebionetworks.web.client.callback.MD5Callback::setMD5(Ljava/lang/String;)(spark.end());
            }
        };
        $wnd.frOnerror = function () {
        	console.warn("unable to calculate md5");
            md5Callback.@org.sagebionetworks.web.client.callback.MD5Callback::setMD5(Ljava/lang/String;)(null);
        };
        
        $wnd.loadNext = function() { 
            var fileReader = new FileReader();
	        fileReader.onload = $wnd.frOnload;
	        fileReader.onerror = $wnd.frOnerror;
	
	        var start = currentChunk * chunkSize,
	            end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
			console.log("MD5 full file: loading next chunk: start=", start, " end=", end);
	        fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
		};
       $wnd.loadNext();
	}-*/;

	/**
	 * Using SparkMD5 (https://github.com/satazor/SparkMD5) to calculate the md5 of part of a file.
	 */
	@Override
	public void getFilePartMd5(JavaScriptObject blob, int currentChunk, Long chunkSize, MD5Callback md5Callback) {
		_getFilePartMd5(blob, currentChunk, chunkSize.doubleValue(), md5Callback);
	}
	private final static native void _getFilePartMd5(JavaScriptObject file, int currentChunk, double chunkSize, MD5Callback md5Callback) /*-{
		var blobSlice = file.slice || file.mozSlice || file.webkitSlice;
		spark = new $wnd.SparkMD5.ArrayBuffer();
        $wnd.frOnload = function(e) {
            spark.append(e.target.result); // append array buffer
           // Call instance method setMD5() on md5Callback with the final md5
			md5Callback.@org.sagebionetworks.web.client.callback.MD5Callback::setMD5(Ljava/lang/String;)(spark.end());
        };
        $wnd.frOnerror = function () {
        	console.warn("unable to calculate file part md5");
            md5Callback.@org.sagebionetworks.web.client.callback.MD5Callback::setMD5(Ljava/lang/String;)(null);
        };
        
        $wnd.loadPart = function() { 
            var fileReader = new FileReader();
	        fileReader.onload = $wnd.frOnload;
	        fileReader.onerror = $wnd.frOnerror;
			var start = currentChunk * chunkSize,
	            end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
	        
	        console.log("MD5 file part: loading chunk: start=", start, " end=", end);
	        fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
		};
       $wnd.loadPart();
	}-*/;
	
	@Override
	public boolean isFileAPISupported() {
		return _isFileAPISupported();
	}
	
	private final static native boolean _isFileAPISupported() /*-{
		return ($wnd.File && $wnd.FileReader && $wnd.FileList && $wnd.Blob);
	}-*/;

	/**
	 * Get the url to a local file blob.
	 */
	@Override
	public String getFileUrl(String fileFieldId) {
		return _getFileUrl(fileFieldId);
	}
	private final static native String _getFileUrl(String fileFieldId) /*-{
		try {
			var fileToUploadElement = $doc.getElementById(fileFieldId);
			var file = fileToUploadElement.files[0];
			return URL.createObjectURL(file);
		}catch(err) {
			return null;
		}
	}-*/;

	@Override
	public void uploadUrlToGenomeSpace(String url) {
		_uploadUrlToGenomeSpace(url, null);
	}

	@Override
	public void uploadUrlToGenomeSpace(String url, String filename) {
		_uploadUrlToGenomeSpace(url, filename);		
	}

	private final static native void _uploadUrlToGenomeSpace(String url, String fileName) /*-{
		var gsUploadUrl = "https://gsui.genomespace.org/jsui/upload/loadUrlToGenomespace.html?uploadUrl=";
		var dest = $wnd.encodeURIComponent(url);
		gsUploadUrl += dest;
		if(fileName != null) {
			gsUploadUrl += "&fileName=" + fileName;
		}
		var newWin = $wnd.open(gsUploadUrl, "GenomeSpace Upload", "height=340px,width=550px");
		newWin.focus();
		newWin.setCallbackOnGSUploadComplete = function(savePath) {
			alert('outer Saved to GenomeSpace as ' + savePath);
		}
		newWin.setCallbackOnGSUploadError = function(savePath) {
			alert('outer ERROR saving to GenomeSpace as ' + savePath);
		}
	}-*/;

	@Override
	public void consoleLog(String message) {
		_consoleLog(message);
	}

	public final static native void _consoleLog(String message) /*-{
		console.log(message);
	}-*/;

	@Override
	public void consoleError(String message) {
		_consoleError(message);
	}

	public final static native void _consoleError(String message) /*-{
		console.error(message);
	}-*/;
	
	@Override
	public void processWithMathJax(Element element) {
		_processWithMathJax(element);		
	}

	private final static native void _processWithMathJax(Element element) /*-{
		$wnd.layoutMath(element);
	}-*/;

	@Override
	public void loadCss(final String url, final Callback<Void, Exception> callback) {
		final LinkElement link = Document.get().createLinkElement();
		link.setRel("stylesheet");
		link.setHref(url);
		_nativeAttachToHead(link);
		
		// fall back timer
		final Timer t = new Timer() {
			@Override
			public void run() {
				callback.onSuccess(null);
			}
		};
		
		Command loadedCommand = new Command() {			
			@Override
			public void execute() {
				callback.onSuccess(null);
				t.cancel();
			}
		};
		
		_addCssLoadHandler(url, loadedCommand);		
		t.schedule(5000); // failsafe: after 5 seconds assume loaded
	}
	
	/**
	 * Attach element to head
	 */
	protected static native void _nativeAttachToHead(JavaScriptObject scriptElement) /*-{
	    $doc.getElementsByTagName("head")[0].appendChild(scriptElement);
	}-*/;


	/**
	 * provides a callback mechanism for when CSS resources that have been added to the dom are fully loaded
	 * @param cssUrl
	 * @param finishedUploadingCallback
	 */
	private static native void _addCssLoadHandler(String cssUrl, Command command) /*-{
		// Use Image load error callback to detect loading as no reliable/cross-browser callback exists for Link element
		var img = $doc.createElement('img');		
		img.onerror = function() {			
			command.@com.google.gwt.user.client.Command::execute()();
		}
		img.src = cssUrl;
	}-*/;
	
	@Override
	public void initOnPopStateHandler() {
		_initOnPopStateHandler();
	}

	private static native void _initOnPopStateHandler()/*-{
		// reload the page on pop state
		//we set the source property of the state if we used pushState or replaceState
		$wnd.addEventListener("popstate", function(event) {
			var stateObj = event.state;
			if (typeof stateObj !== "undefined" && stateObj !== null && typeof stateObj.source !== "undefined"){
				$wnd.location.reload(false);
			}
		});
	}-*/;
	
	@Override
	public void showTwitterFeed(String dataWidgetId, String elementId,
			String linkColor, String borderColor, int tweetCount) {
		_showTwitterFeed(dataWidgetId, elementId, linkColor, borderColor, tweetCount);		
	}

	private final static native void _showTwitterFeed(String dataWidgetId,
			String elementId, String linkColorHex, String borderColorHex,
			int tweetCount) /*-{
		if (typeof $wnd.twttr !== 'undefined') {
			var element = $doc.getElementById(elementId);
			$wnd.twttr.widgets.createTimeline(dataWidgetId, element, {
				chrome : "nofooter noheader",
				linkColor : linkColorHex,
				borderColor : borderColorHex,
				tweetLimit : tweetCount
			});
		}
		
	}-*/;
	
	@Override
	public boolean elementSupportsAttribute(Element el, String attribute) {
		return _elementSupportsAttribute(el.getTagName(), attribute);
	}
	
	private final static native boolean _elementSupportsAttribute(String tagName, String attribute) /*-{
	    return attribute in $doc.createElement(tagName);
	}-*/;
	
	boolean isFilterXssInitialized = false;
	
	@Override
	public String sanitizeHtml(String html) {
		if (!isFilterXssInitialized) {
			//init
			initFilterXss();
			isFilterXssInitialized = true;
		}
		return _sanitizeHtml(html);
	}

	private final static native void initFilterXss() /*-{
		var options = {
				whiteList: {
				    a:      ['target', 'href', 'title'],
				    abbr:   ['title'],
				    address: [],
				    area:   ['shape', 'coords', 'href', 'alt'],
				    article: [],
				    aside:  [],
				    audio:  ['autoplay', 'controls', 'loop', 'preload', 'src'],
				    b:      [],
				    bdi:    ['dir'],
				    bdo:    ['dir'],
				    big:    [],
				    blockquote: ['cite'],
				    body:   [],
				    br:     [],
				    caption: [],
				    center: [],
				    cite:   [],
				    code:   [],
				    col:    ['align', 'valign', 'span', 'width'],
				    colgroup: ['align', 'valign', 'span', 'width'],
				    dd:     [],
				    del:    ['datetime'],
				    details: ['open'],
				    div:    [],
				    dl:     [],
				    dt:     [],
				    em:     [],
				    font:   ['color', 'size', 'face'],
				    footer: [],
				    h1:     [],
				    h2:     [],
				    h3:     [],
				    h4:     [],
				    h5:     [],
				    h6:     [],
				    header: [],
				    hr:     [],
				    html:   [],
				    i:      [],
				    img:    ['src', 'alt', 'title', 'width', 'height'],
				    ins:    ['datetime'],
				    li:     [],
				    mark:   [],
				    nav:    [],
				    ol:     [],
				    p:      [],
				    pre:    [],
				    s:      [],
				    section:[],
				    small:  [],
				    span:   [],
				    sub:    [],
				    sup:    [],
				    strong: [],
				    table:  ['width', 'border', 'align', 'valign'],
				    tbody:  ['align', 'valign'],
				    td:     ['width', 'rowspan', 'colspan', 'align', 'valign'],
				    tfoot:  ['align', 'valign'],
				    th:     ['width', 'rowspan', 'colspan', 'align', 'valign'],
				    thead:  ['align', 'valign'],
				    tr:     ['rowspan', 'align', 'valign'],
				    tt:     [],
				    u:      [],
				    ul:     [],
				    video:  ['autoplay', 'controls', 'loop', 'preload', 'src', 'height', 'width']
				},
				stripIgnoreTagBody: ['script'],  // the script tag is a special case, we need
				allowCommentTag: true,
				onIgnoreTag: function (tag, html, options) {
					if (tag === '!doctype') {
				      // do not filter doctype
				      return html;
				    }
				}
			};
			$wnd.xss = new $wnd.filterXSS.FilterXSS(options);
	}-*/;
	
	private final static native String _sanitizeHtml(String html) /*-{
		try {
			return $wnd.xss.process(html);
		} catch (err) {
			console.error(err);
		}
	}-*/;
	
	@Override
	public String getCurrentURL() {
		return Location.getHref();
	}
	
	@Override
	public String getCurrentHostName() {
		return Location.getHostName();
	}
	
	@Override
	public String getProtocol(String url) {
		return _getProtocol(url);
	}
	private final static native String _getProtocol(String url) /*-{
		var parser = $doc.createElement('a');
		parser.href = url;
		var v = parser.protocol; // for example, "https:"
		parser = null; 
		return v;
	}-*/;
	
	@Override
	public String getHost(String url) {
		return _getHost(url);
	}
	
	private final static native String _getHost(String url) /*-{
		var parser = $doc.createElement('a');
		parser.href = url;
		var v = parser.host;     // for example, "test.com:8080"
		parser = null; 
		return v;
	}-*/;
	
	@Override
	public String getHostname(String url) {
		return _getHostname(url);
	}
	
	private final static native String _getHostname(String url) /*-{
		var parser = $doc.createElement('a');
		parser.href = url;
		var v = parser.hostname; // for example, "test.com"
		parser = null; 
		return v;
	}-*/;
	
	@Override
	public String getPort(String url) {
		return _getPort(url);
	}
	private final static native String _getPort(String url) /*-{
		var parser = $doc.createElement('a');
		parser.href = url;
		var v = parser.port;     // for example, "8080"
		parser = null; 
		return v;
	}-*/;
	
	@Override
	public String getPathname(String url) {
		return _getPathname(url);
	}
	
	private final static native String _getPathname(String url) /*-{
		var parser = $doc.createElement('a');
		parser.href = url;
		var v = parser.pathname; // for example, "/resources/images/" 
		parser = null; 
		return v;
	}-*/;
	
	@Override
	public void copyToClipboard() {
		try {
			_copyToClipboard();
			Notify.hideAll();
			NotifySettings settings = DisplayUtils.getDefaultSettings();
			settings.setType(NotifyType.INFO);
			Notify.notify("Copied to clipboard", settings);
		} catch (Throwable t) {
			consoleError(t.getMessage());
		}
	}

	private final static native String _copyToClipboard() /*-{
		$doc.execCommand('copy');
	}-*/;
}
