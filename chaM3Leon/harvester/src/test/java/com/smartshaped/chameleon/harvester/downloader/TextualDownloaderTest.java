package com.smartshaped.chameleon.harvester.downloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.StringType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

@ExtendWith(MockitoExtension.class)
class TextualDownloaderTest {

  private final List<String> requestParams = new ArrayList<>();
  @Mock private Request request;

  @Mock private SparkSession sparkSession;
  @Mock private scala.Option<SparkSession> option;

  @Mock private UserDefinedFunction apiCallerUdf;
  @Mock private Dataset<Row> dataset;
  @Mock private Dataset<String> uriDataset;
  @Mock private Column column;
  @Mock private URI uri;
  @Mock private InputStream inputStream;
  @Mock private HttpClient.Builder builderCli;
  @Mock private HttpRequest.Builder builderReq;
  @Mock private HttpRequest httprequest;
  @Mock private HttpClient httpclient;
  @Mock private HttpResponse httpresponse;

  @Test
  void downloadSuccess() throws ConfigurationException {
    try (MockedStatic<SparkSession> sparkSessionMockedStatic = mockStatic(SparkSession.class);
        MockedStatic<functions> functionsMockedStatic = mockStatic(functions.class)) {
      functionsMockedStatic
          .when(() -> functions.udf(any(UDF1.class), any(StringType.class)))
          .thenReturn(apiCallerUdf);
      sparkSessionMockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      when(option.get()).thenReturn(sparkSession);
      when(sparkSession.createDataset(anyList(), any(Encoders.STRING().getClass())))
          .thenReturn(uriDataset);
      when(uriDataset.col(anyString())).thenReturn(column);
      when(apiCallerUdf.apply(column)).thenReturn(column);
      when(uriDataset.withColumn("response", column)).thenReturn(dataset);

      TextualDownloaderTestClass textualDownloader = new TextualDownloaderTestClass();
      assertDoesNotThrow(() -> textualDownloader.download(requestParams, request));
    }
  }

  @Test
  void callApiAndGetResponseSuccess()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class); ) {

      httpClient.when(HttpClient::newHttpClient).thenReturn(httpclient);

      when(builderReq.build()).thenReturn(httprequest);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);

      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenReturn(httpresponse);
      when(httpresponse.statusCode()).thenReturn(200);

      when(httpresponse.body()).thenReturn("");

      TextualDownloaderTestClass textualDownloader = new TextualDownloaderTestClass();
      assertDoesNotThrow(() -> textualDownloader.callApiAndGetResponse(uri));
    }
  }

  @Test
  void callApiAndGetResponseStatusFailure()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class); ) {

      httpClient.when(HttpClient::newHttpClient).thenReturn(httpclient);

      when(builderReq.build()).thenReturn(httprequest);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);

      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenReturn(httpresponse);
      when(httpresponse.statusCode()).thenReturn(400);

      TextualDownloaderTestClass textualDownloader = new TextualDownloaderTestClass();
      assertThrows(DownloaderException.class, () -> textualDownloader.callApiAndGetResponse(uri));
    }
  }

  @Test
  void callApiAndGetResponseFailureIO()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class); ) {

      httpClient.when(HttpClient::newHttpClient).thenReturn(httpclient);

      when(builderReq.build()).thenReturn(httprequest);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);

      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenThrow(IOException.class);

      TextualDownloaderTestClass textualDownloader = new TextualDownloaderTestClass();
      assertThrows(DownloaderException.class, () -> textualDownloader.callApiAndGetResponse(uri));
    }
  }

  @Test
  void callApiAndGetResponseFailureInterrupted()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class); ) {

      httpClient.when(HttpClient::newHttpClient).thenReturn(httpclient);

      when(builderReq.build()).thenReturn(httprequest);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);

      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenThrow(InterruptedException.class);

      TextualDownloaderTestClass textualDownloader = new TextualDownloaderTestClass();
      assertThrows(DownloaderException.class, () -> textualDownloader.callApiAndGetResponse(uri));
    }
  }
}
