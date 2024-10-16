package org.sagebionetworks.web.unitclient.widget.entity.renderer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sagebionetworks.web.client.utils.FutureUtils.getDoneFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.entitybundle.v2.EntityBundle;
import org.sagebionetworks.repo.model.entitybundle.v2.EntityBundleRequest;
import org.sagebionetworks.repo.model.file.FileHandle;
import org.sagebionetworks.web.client.DisplayConstants;
import org.sagebionetworks.web.client.SynapseJavascriptClient;
import org.sagebionetworks.web.client.security.AuthenticationController;
import org.sagebionetworks.web.client.widget.entity.renderer.VideoWidget;
import org.sagebionetworks.web.client.widget.entity.renderer.VideoWidgetView;
import org.sagebionetworks.web.shared.WidgetConstants;
import org.sagebionetworks.web.shared.WikiPageKey;

@RunWith(MockitoJUnitRunner.Silent.class)
public class VideoWidgetTest {

  VideoWidget widget;

  @Mock
  VideoWidgetView mockView;

  WikiPageKey wikiKey = new WikiPageKey("", ObjectType.ENTITY.toString(), null);

  @Mock
  AuthenticationController mockAuthController;

  @Mock
  SynapseJavascriptClient mockJsClient;

  @Mock
  EntityBundle mockEntityBundle;

  @Mock
  FileHandle mockFileHandle;

  @Before
  public void setup() {
    widget = new VideoWidget(mockView, mockAuthController, mockJsClient);
    when(mockAuthController.isLoggedIn()).thenReturn(true);
    when(mockEntityBundle.getFileHandles())
      .thenReturn(Collections.singletonList(mockFileHandle));
    when(
      mockJsClient.getEntityBundle(anyString(), any(EntityBundleRequest.class))
    )
      .thenReturn(getDoneFuture(mockEntityBundle));
  }

  @Test
  public void testAsWidget() {
    widget.asWidget();
    verify(mockView).asWidget();
  }

  @Test
  public void testConfigure() {
    Map<String, String> descriptor = new HashMap<String, String>();
    String mp4VideoId = "syn123";
    String webMVideoId = "syn456";
    String oggVideoId = "syn789";
    String width = "400px";
    String height = "600px";
    descriptor.put(WidgetConstants.VIDEO_WIDGET_MP4_SYNAPSE_ID_KEY, mp4VideoId);
    descriptor.put(
      WidgetConstants.VIDEO_WIDGET_WEBM_SYNAPSE_ID_KEY,
      webMVideoId
    );
    descriptor.put(WidgetConstants.VIDEO_WIDGET_OGG_SYNAPSE_ID_KEY, oggVideoId);
    descriptor.put(WidgetConstants.VIDEO_WIDGET_WIDTH_KEY, width);
    descriptor.put(WidgetConstants.HEIGHT_KEY, height);

    widget.configure(wikiKey, descriptor, null, null);
    verify(mockView)
      .configure(
        eq(mp4VideoId),
        eq(oggVideoId),
        eq(webMVideoId),
        eq(width),
        eq(height)
      );
  }

  @Test
  public void testConfigureYouTube() {
    Map<String, String> descriptor = new HashMap<String, String>();
    String videoId = "my test video id";
    descriptor.put(WidgetConstants.YOUTUBE_WIDGET_VIDEO_ID_KEY, videoId);
    widget.configure(wikiKey, descriptor, null, null);
    verify(mockView).configure(eq(VideoWidget.YOUTUBE_URL_PREFIX + videoId));
  }

  @Test
  public void testShortConfigureMP4() {
    String mp4VideoId = "syn123";
    String oggVideoId = null;
    String webMVideoId = null;
    String width = "400";
    String height = "600";
    widget.configure(mp4VideoId, "filename.mp4", 400, 600);
    verify(mockView)
      .configure(
        eq(mp4VideoId),
        eq(oggVideoId),
        eq(webMVideoId),
        eq(width),
        eq(height)
      );
  }

  @Test
  public void testShortConfigureWebMV() {
    String mp4VideoId = null;
    String oggVideoId = null;
    String webMVideoId = "syn456";
    String width = "400";
    String height = "600";
    widget.configure(webMVideoId, "filename.webm", 400, 600);
    verify(mockView)
      .configure(
        eq(mp4VideoId),
        eq(oggVideoId),
        eq(webMVideoId),
        eq(width),
        eq(height)
      );
  }

  @Test
  public void testShortConfigureOgg() {
    String mp4VideoId = null;
    String oggVideoId = "syn789";
    String webMVideoId = null;
    String width = "400";
    String height = "600";
    widget.configure(oggVideoId, "filename.ogg", 400, 600);
    verify(mockView)
      .configure(
        eq(mp4VideoId),
        eq(oggVideoId),
        eq(webMVideoId),
        eq(width),
        eq(height)
      );
  }

  @Test
  public void testSynapseFileAnonymousView() {
    when(mockAuthController.isLoggedIn()).thenReturn(false);
    when(mockEntityBundle.getFileHandles()).thenReturn(Collections.EMPTY_LIST);
    String webMVideoId = "syn456";

    widget.configure(webMVideoId, "filename.webm", 400, 600);

    verify(mockView, never())
      .configure(
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString()
      );
    verify(mockView).showError(DisplayConstants.ERROR_LOGIN_REQUIRED);
  }

  @Test
  public void testSynapseFileLoggedInWithoutAccess() {
    when(mockAuthController.isLoggedIn()).thenReturn(true);
    when(mockEntityBundle.getFileHandles()).thenReturn(Collections.EMPTY_LIST);
    String webMVideoId = "syn456";

    widget.configure(webMVideoId, "filename.webm", 400, 600);

    verify(mockView, never())
      .configure(
        anyString(),
        anyString(),
        anyString(),
        anyString(),
        anyString()
      );
    verify(mockView).showError(DisplayConstants.ERROR_FAILURE_PRIVLEDGES);
  }

  @Test
  public void testSynapseFileAnonymousViewOpenData() {
    // if open data and not logged in, then the file handle will still be returned (mockFileHandle is set up to be returned in the @Before)
    when(mockAuthController.isLoggedIn()).thenReturn(false);
    String mp4VideoId = null;
    String oggVideoId = null;
    String webMVideoId = "syn456";
    String width = "400";
    String height = "600";

    widget.configure(webMVideoId, "filename.webm", 400, 600);

    verify(mockView)
      .configure(
        eq(mp4VideoId),
        eq(oggVideoId),
        eq(webMVideoId),
        eq(width),
        eq(height)
      );
  }
}
