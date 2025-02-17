package com.smartshaped.chameleon.harvester.downloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SingleThreadJsonDownloaderTest {

  private List<String> uriList = new ArrayList<>();
  private String uri;

  private List<String> reqParams = new ArrayList<String>();
  @Mock private Request req;
  @Mock private HttpRequest.Builder builder;
  @Mock private HttpRequest httprequest;
  @Mock private HttpClient httpclient;
  @Mock private HttpResponse httpresponse;

  @BeforeEach
  void setUp() {
    uriList.add(uri);
  }

  @Test
  void downloadsSuccess() throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {

      httpReq.when(() -> HttpRequest.newBuilder()).thenReturn(builder);
      when(builder.uri(any(URI.class))).thenReturn(builder);
      when(builder.build()).thenReturn(httprequest);
      httpClient.when(() -> HttpClient.newHttpClient()).thenReturn(httpclient);
      when(httpclient.send(any(), any())).thenReturn(httpresponse);

      when(httpresponse.statusCode()).thenReturn(200);

      SingleThreadJsonDownloaderExample singleThreadJsonDownloader =
          new SingleThreadJsonDownloaderExample();
      assertDoesNotThrow(() -> singleThreadJsonDownloader.download(reqParams, req));
    }
  }

  @Test
  void downloadsFailureOne() throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {

      httpReq.when(() -> HttpRequest.newBuilder()).thenReturn(builder);
      when(builder.uri(any(URI.class))).thenReturn(builder);
      when(builder.build()).thenReturn(httprequest);
      httpClient.when(() -> HttpClient.newHttpClient()).thenReturn(httpclient);
      when(httpclient.send(any(), any())).thenThrow(IOException.class);

      SingleThreadJsonDownloaderExample singleThreadJsonDownloader =
          new SingleThreadJsonDownloaderExample();
      assertThrows(
          DownloaderException.class, () -> singleThreadJsonDownloader.download(reqParams, req));
    }
  }

  @Test
  void downloadsFailureTwo() throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {

      httpReq.when(() -> HttpRequest.newBuilder()).thenReturn(builder);
      when(builder.uri(any(URI.class))).thenReturn(builder);
      when(builder.build()).thenReturn(httprequest);
      httpClient.when(() -> HttpClient.newHttpClient()).thenReturn(httpclient);
      when(httpclient.send(any(), any())).thenReturn(httpresponse);

      SingleThreadJsonDownloaderExample singleThreadJsonDownloader =
          new SingleThreadJsonDownloaderExample();
      assertThrows(
          DownloaderException.class, () -> singleThreadJsonDownloader.download(reqParams, req));
    }
  }
}
