package org.sagebionetworks.web.client.jsinterop;

import org.sagebionetworks.repo.model.table.Query;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsNullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class QueryWrapperPlotNavProps extends ReactComponentProps {
	@FunctionalInterface
	@JsFunction
	public interface OnQueryCallback {
		void run(String newQueryJson);
	}

	@FunctionalInterface
	@JsFunction
	public interface OnQueryResultBundleCallback {
		void run(String newQueryResultBundleJson);
	}

	String initQueryJson;
	String sql;
	@JsNullable
	OnQueryCallback onQueryChange;
	@JsNullable
	OnQueryResultBundleCallback onQueryResultBundleChange;
	
	@JsNullable
	boolean shouldDeepLink;
	@JsNullable
	String downloadCartPageUrl;
	@JsNullable
	boolean hideSqlEditorControl;
	@JsNullable
	SynapseTableProps tableConfiguration;

	@JsOverlay
	public static QueryWrapperPlotNavProps create(String sql, String initQueryJson, OnQueryCallback onQueryChange, OnQueryResultBundleCallback onQueryResultBundleChange,
			boolean hideSqlEditorControl) {
		QueryWrapperPlotNavProps props = new QueryWrapperPlotNavProps();
		props.sql = sql;
		props.initQueryJson = initQueryJson;
		props.hideSqlEditorControl = hideSqlEditorControl;
		props.onQueryChange = onQueryChange;
		props.onQueryResultBundleChange = onQueryResultBundleChange;
		props.tableConfiguration = SynapseTableProps.create();
		props.shouldDeepLink = true;
		props.downloadCartPageUrl = "#!DownloadCart:0";
		return props;
	}
}
