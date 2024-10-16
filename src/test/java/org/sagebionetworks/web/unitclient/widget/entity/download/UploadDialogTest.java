package org.sagebionetworks.web.unitclient.widget.entity.download;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sagebionetworks.repo.model.Entity;
import org.sagebionetworks.web.client.events.CancelHandler;
import org.sagebionetworks.web.client.events.UploadSuccessHandler;
import org.sagebionetworks.web.client.utils.CallbackP;
import org.sagebionetworks.web.client.widget.entity.download.UploadDialogWidget;
import org.sagebionetworks.web.client.widget.entity.download.UploadDialogWidgetView;
import org.sagebionetworks.web.client.widget.entity.download.Uploader;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UploadDialogTest {

  @Mock
  UploadDialogWidgetView view;

  @Mock
  Uploader mockUploader;

  UploadDialogWidget widget;

  @Before
  public void before() throws Exception {
    widget = new UploadDialogWidget(view, mockUploader);
  }

  @Test
  public void testConfigure() {
    String title = "dialog title";
    Entity entity = mock(Entity.class);
    String parentEntityId = "parent";
    CallbackP<String> fileHandleIdCallback = mock(CallbackP.class);
    boolean isEntity = true;
    widget.configure(
      title,
      entity,
      parentEntityId,
      fileHandleIdCallback,
      isEntity
    );

    verify(mockUploader)
      .configure(entity, parentEntityId, fileHandleIdCallback, isEntity);
    verify(view).configureDialog(eq(title), any());

    verify(mockUploader).setSuccessHandler(any(UploadSuccessHandler.class));
    verify(mockUploader).setCancelHandler(any(CancelHandler.class));
  }

  @Test
  public void testDisableMultipleFileUploads() {
    widget.disableMultipleFileUploads();
    verify(mockUploader).disableMultipleFileUploads();
  }

  @Test
  public void testShow() {
    widget.show();
    verify(view).showDialog();
  }
}
