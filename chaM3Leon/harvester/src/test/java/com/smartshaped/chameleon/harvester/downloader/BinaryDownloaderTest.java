package com.smartshaped.chameleon.harvester.downloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.StringType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryDownloaderTest {

  private final List<String> requestParams = new ArrayList<>();
  @Mock private SparkSession sparkSession;
  @Mock private scala.Option<SparkSession> option;
  @Mock private Request request;
  @Mock private UserDefinedFunction dwnldBryFl;
  @Mock private Dataset<Row> dataset;
  @Mock private Dataset<String> uriDataset;
  @Mock private Column column;

  @Mock private HttpClient.Builder builderCli;
  @Mock private HttpRequest.Builder builderReq;
  @Mock private HttpRequest httprequest;
  @Mock private HttpClient httpclient;
  @Mock private HttpResponse httpresponse;
  @Mock private URI uri;
  @Mock private Configuration hadoopConf;
  @Mock private FileSystem hadoopFileSystem;
  @Mock private Path finalPath;
  @Mock private InputStream inputStream;
  @Mock private FSDataOutputStream fsDataOutputStream;
  @Mock private HarvesterConfigurationUtils configurationUtils;

  @Test
  void downloadSuccess() throws ConfigurationException {
    try (MockedStatic<SparkSession> sparkSessionMockedStatic = mockStatic(SparkSession.class);
        MockedStatic<functions> functionsMockedStatic = mockStatic(functions.class)) {
      functionsMockedStatic
          .when(() -> functions.udf(any(UDF1.class), any(StringType.class)))
          .thenReturn(dwnldBryFl);
      sparkSessionMockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      when(option.get()).thenReturn(sparkSession);
      when(sparkSession.createDataset(anyList(), any(Encoders.STRING().getClass())))
          .thenReturn(uriDataset);
      when(uriDataset.col(anyString())).thenReturn(column);
      when(dwnldBryFl.apply(column)).thenReturn(column);
      when(uriDataset.withColumn("path", column)).thenReturn(dataset);

      BinaryDownloaderTestClass binaryDownloader = new BinaryDownloaderTestClass();
      assertDoesNotThrow(() -> binaryDownloader.download(requestParams, request));
    }
  }

  @Test
  void callDownloadFileTestSuccess()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class);
        MockedStatic<URI> uriMockedStatic = mockStatic(URI.class);
        MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class);
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<Path> pathMockedConstruction = mockConstruction(Path.class)) {

      when(builderCli.build()).thenReturn(httpclient);
      when(builderCli.version(HttpClient.Version.HTTP_2)).thenReturn(builderCli);
      httpClient.when(HttpClient::newBuilder).thenReturn(builderCli);

      when(builderReq.build()).thenReturn(httprequest);
      uriMockedStatic.when(() -> URI.create(anyString())).thenReturn(uri);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);
      when(builderReq.GET()).thenReturn(builderReq);
      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenReturn(httpresponse);
      when(httpresponse.statusCode()).thenReturn(200);
      fileSystemMockedStatic
          .when(() -> FileSystem.get(any(URI.class), any(Configuration.class)))
          .thenReturn(hadoopFileSystem);

      when(hadoopFileSystem.exists(any())).thenReturn(false);
      when(httpresponse.body()).thenReturn(inputStream);
      when(hadoopFileSystem.create(any(Path.class), any(Boolean.class)))
          .thenReturn(fsDataOutputStream);
      when(inputStream.read(any())).thenReturn(-1);

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");

      BinaryDownloaderTestClass binaryDownloader = new BinaryDownloaderTestClass();
      assertDoesNotThrow(() -> binaryDownloader.downloadFile(""));
    }
  }

  @Test
  void callDownloadFileTestFailureIO()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class);
        MockedStatic<URI> uriMockedStatic = mockStatic(URI.class);
        MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class);
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<Path> pathMockedConstruction = mockConstruction(Path.class)) {

      when(builderCli.build()).thenReturn(httpclient);
      when(builderCli.version(HttpClient.Version.HTTP_2)).thenReturn(builderCli);
      httpClient.when(HttpClient::newBuilder).thenReturn(builderCli);

      when(builderReq.build()).thenReturn(httprequest);
      uriMockedStatic.when(() -> URI.create(anyString())).thenReturn(uri);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);
      when(builderReq.GET()).thenReturn(builderReq);
      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenReturn(httpresponse);
      when(httpresponse.statusCode()).thenReturn(200);
      fileSystemMockedStatic
          .when(() -> FileSystem.get(any(URI.class), any(Configuration.class)))
          .thenReturn(hadoopFileSystem);

      when(hadoopFileSystem.exists(any())).thenReturn(false);
      when(httpresponse.body()).thenReturn(inputStream);
      when(hadoopFileSystem.create(any(Path.class), any(Boolean.class)))
          .thenReturn(fsDataOutputStream);
      when(inputStream.read(any())).thenThrow(IOException.class);

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");

      BinaryDownloaderTestClass binaryDownloader = new BinaryDownloaderTestClass();
      assertThrows(DownloaderException.class, () -> binaryDownloader.downloadFile(""));
    }
  }

  @Test
  void callDownloadFileTestElseOne()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class);
        MockedStatic<URI> uriMockedStatic = mockStatic(URI.class);
        MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class);
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<Path> pathMockedConstruction = mockConstruction(Path.class)) {

      when(builderCli.build()).thenReturn(httpclient);
      when(builderCli.version(HttpClient.Version.HTTP_2)).thenReturn(builderCli);
      httpClient.when(HttpClient::newBuilder).thenReturn(builderCli);

      when(builderReq.build()).thenReturn(httprequest);
      uriMockedStatic.when(() -> URI.create(anyString())).thenReturn(uri);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);
      when(builderReq.GET()).thenReturn(builderReq);
      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenReturn(httpresponse);
      when(httpresponse.statusCode()).thenReturn(200);
      fileSystemMockedStatic
          .when(() -> FileSystem.get(any(URI.class), any(Configuration.class)))
          .thenReturn(hadoopFileSystem);

      when(hadoopFileSystem.exists(any())).thenReturn(true);

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");

      BinaryDownloaderTestClass binaryDownloader = new BinaryDownloaderTestClass();
      assertDoesNotThrow(() -> binaryDownloader.downloadFile(""));
    }
  }

  @Test
  void callDownloadFileTestElseTwo()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class);
        MockedStatic<URI> uriMockedStatic = mockStatic(URI.class);
        MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class)) {

      when(builderCli.build()).thenReturn(httpclient);
      when(builderCli.version(HttpClient.Version.HTTP_2)).thenReturn(builderCli);
      httpClient.when(HttpClient::newBuilder).thenReturn(builderCli);

      when(builderReq.build()).thenReturn(httprequest);
      uriMockedStatic.when(() -> URI.create(anyString())).thenReturn(uri);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);
      when(builderReq.GET()).thenReturn(builderReq);
      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenReturn(httpresponse);
      when(httpresponse.statusCode()).thenReturn(404);

      BinaryDownloaderTestClass binaryDownloader = new BinaryDownloaderTestClass();
      assertThrows(DownloaderException.class, () -> binaryDownloader.downloadFile(""));
    }
  }

  @Test
  void callDownloadFileTestFailure()
      throws ConfigurationException, IOException, InterruptedException {
    try (MockedStatic<HttpRequest> httpReq = mockStatic(HttpRequest.class);
        MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class);
        MockedStatic<URI> uriMockedStatic = mockStatic(URI.class);
        MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class)) {

      when(builderCli.build()).thenReturn(httpclient);
      when(builderCli.version(HttpClient.Version.HTTP_2)).thenReturn(builderCli);
      httpClient.when(HttpClient::newBuilder).thenReturn(builderCli);

      when(builderReq.build()).thenReturn(httprequest);
      uriMockedStatic.when(() -> URI.create(anyString())).thenReturn(uri);
      when(builderReq.uri(any(URI.class))).thenReturn(builderReq);
      when(builderReq.GET()).thenReturn(builderReq);
      httpReq.when(HttpRequest::newBuilder).thenReturn(builderReq);

      when(httpclient.send(any(), any())).thenThrow(IOException.class);

      BinaryDownloaderTestClass binaryDownloader = new BinaryDownloaderTestClass();
      assertThrows(DownloaderException.class, () -> binaryDownloader.downloadFile(""));
    }
  }
}
