package org.sagebionetworks.web.client.widget.asynch;

import java.util.function.Consumer;

import org.sagebionetworks.repo.model.SignedTokenInterface;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.repo.model.file.FileResult;
import org.sagebionetworks.web.client.utils.FutureUtils;

import com.google.common.util.concurrent.FluentFuture;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PresignedURLAsyncHandler {
	void getFileResult(FileHandleAssociation fileHandleAssociation, AsyncCallback<FileResult> callback);
	FluentFuture<FileResult> getFileResult(FileHandleAssociation fileHandleAssociation);
}
