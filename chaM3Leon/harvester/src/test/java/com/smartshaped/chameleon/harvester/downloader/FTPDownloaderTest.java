package com.smartshaped.chameleon.harvester.downloader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;

@ExtendWith(MockitoExtension.class)
class FTPDownloaderTest {

  private final List<String> requestParams = new ArrayList<>();
  @Mock private SparkSession sparkSession;
  @Mock private scala.Option<SparkSession> option;
  @Mock private UserDefinedFunction dwnldFtpFl;
  @Mock private Request request;
  @Mock private Dataset<Row> dataset;
  @Mock private Dataset<String> uriDataset;
  @Mock private Column column;
  @Mock Map<String, String> queryParams;
  @Mock FTPClient ftpClient;
  @Mock private FileSystem hadoopFileSystem;
  @Mock private FSDataOutputStream fsDataOutputStream;

  @Mock private HarvesterConfigurationUtils configurationUtils;

  @Test
  void downloadSuccess() throws ConfigurationException {
    try (MockedStatic<SparkSession> sparkSessionMockedStatic = mockStatic(SparkSession.class);
        MockedStatic<functions> functionsMockedStatic = mockStatic(functions.class);
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<FTPClient> ftpClientMocked =
            mockConstruction(
                FTPClient.class,
                (mock, context) -> {
                  doNothing().when(mock).connect(anyString(), anyInt());
                  when(mock.getReplyCode()).thenReturn(200);
                  when(mock.login(anyString(), anyString())).thenReturn(true);
                })) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("true");

      functionsMockedStatic
          .when(() -> functions.udf(any(UDF1.class), any(StringType.class)))
          .thenReturn(dwnldFtpFl);
      sparkSessionMockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      when(option.get()).thenReturn(sparkSession);
      when(sparkSession.createDataset(anyList(), any(Encoders.STRING().getClass())))
          .thenReturn(uriDataset);

      when(uriDataset.withColumnRenamed("value", "ftp_path")).thenReturn(dataset);
      when(functions.col(anyString())).thenReturn(column);
      when(dwnldFtpFl.apply(column)).thenReturn(column);
      when(dataset.withColumn("path", column)).thenReturn(dataset);

      when(dataset.withColumn("requestContent", functions.lit(anyString()))).thenReturn(dataset);

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();

      assertDoesNotThrow(() -> ftpDownloader.download(requestParams, request));
    }
  }

  @Test
  void downloadFtpDisconnect() throws ConfigurationException {
    try (MockedStatic<SparkSession> sparkSessionMockedStatic = mockStatic(SparkSession.class);
        MockedStatic<functions> functionsMockedStatic = mockStatic(functions.class);
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<FTPClient> ftpClientMocked =
            mockConstruction(
                FTPClient.class,
                (mock, context) -> {
                  doNothing().when(mock).connect(anyString(), anyInt());
                  when(mock.getReplyCode()).thenReturn(404);
                  when(mock.login(anyString(), anyString())).thenReturn(true);
                })) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("true");

      sparkSessionMockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();

      assertThrows(DownloaderException.class, () -> ftpDownloader.download(requestParams, request));
    }
  }

  @Test
  void downloadFtpLoginFailed() throws ConfigurationException {
    try (MockedStatic<SparkSession> sparkSessionMockedStatic = mockStatic(SparkSession.class);
        MockedStatic<functions> functionsMockedStatic = mockStatic(functions.class);
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<FTPClient> ftpClientMocked =
            mockConstruction(
                FTPClient.class,
                (mock, context) -> {
                  doNothing().when(mock).connect(anyString(), anyInt());
                  when(mock.getReplyCode()).thenReturn(200);
                  when(mock.login(anyString(), anyString())).thenReturn(false);
                })) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("true");

      sparkSessionMockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();

      assertThrows(DownloaderException.class, () -> ftpDownloader.download(requestParams, request));
    }
  }

  @Test
  void downloadSuccessFtpPassiveMode() throws ConfigurationException {
    try (MockedStatic<SparkSession> sparkSessionMockedStatic = mockStatic(SparkSession.class);
        MockedStatic<functions> functionsMockedStatic = mockStatic(functions.class);
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<FTPClient> ftpClientMocked =
            mockConstruction(
                FTPClient.class,
                (mock, context) -> {
                  doNothing().when(mock).connect(anyString(), anyInt());
                  when(mock.getReplyCode()).thenReturn(200);
                  when(mock.login(anyString(), anyString())).thenReturn(true);
                })) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("false");

      functionsMockedStatic
          .when(() -> functions.udf(any(UDF1.class), any(StringType.class)))
          .thenReturn(dwnldFtpFl);
      sparkSessionMockedStatic.when(SparkSession::getActiveSession).thenReturn(option);

      when(option.get()).thenReturn(sparkSession);
      when(sparkSession.createDataset(anyList(), any(Encoders.STRING().getClass())))
          .thenReturn(uriDataset);

      when(uriDataset.withColumnRenamed("value", "ftp_path")).thenReturn(dataset);
      when(functions.col(anyString())).thenReturn(column);
      when(dwnldFtpFl.apply(column)).thenReturn(column);
      when(dataset.withColumn("path", column)).thenReturn(dataset);

      when(dataset.withColumn("requestContent", functions.lit(anyString()))).thenReturn(dataset);

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();

      assertDoesNotThrow(() -> ftpDownloader.download(requestParams, request));
    }
  }

  @Test
  void callDownloadFileTestSuccess() throws ConfigurationException, IOException {
    try (MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class);
        MockedConstruction<Path> pathMockedConstruction = mockConstruction(Path.class)) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");
      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("false");

      doAnswer(
              invocation -> {
                Object[] args = invocation.getArguments();
                ByteArrayOutputStream out = (ByteArrayOutputStream) args[1];
                out.write("Mock file content".getBytes());
                return true;
              })
          .when(ftpClient)
          .retrieveFile(anyString(), any(ByteArrayOutputStream.class));

      fileSystemMockedStatic
          .when(() -> FileSystem.get(any(URI.class), any(Configuration.class)))
          .thenReturn(hadoopFileSystem);

      when(hadoopFileSystem.exists(any())).thenReturn(false);

      when(hadoopFileSystem.create(any(Path.class), any(Boolean.class)))
          .thenReturn(fsDataOutputStream);
      doNothing().when(fsDataOutputStream).write(any(), anyInt(), anyInt());

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();
      ftpDownloader.setFtpClient(ftpClient);

      assertDoesNotThrow(() -> ftpDownloader.downloadFileFromFTP(""));
    }
  }

  @Test
  void callDownloadFileTestIOException() throws ConfigurationException, IOException {
    try (MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class);
        MockedConstruction<Path> pathMockedConstruction = mockConstruction(Path.class)) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");

      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("false");

      doAnswer(
              invocation -> {
                Object[] args = invocation.getArguments();
                ByteArrayOutputStream out = (ByteArrayOutputStream) args[1];
                out.write("Mock file content".getBytes());
                return true;
              })
          .when(ftpClient)
          .retrieveFile(anyString(), any(ByteArrayOutputStream.class));

      fileSystemMockedStatic
          .when(() -> FileSystem.get(any(URI.class), any(Configuration.class)))
          .thenReturn(hadoopFileSystem);

      when(hadoopFileSystem.exists(any())).thenReturn(false);

      when(hadoopFileSystem.create(any(Path.class), any(Boolean.class)))
          .thenReturn(fsDataOutputStream);
      doThrow(IOException.class).when(fsDataOutputStream).write(any(), anyInt(), anyInt());

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();
      ftpDownloader.setFtpClient(ftpClient);

      assertThrows(DownloaderException.class, () -> ftpDownloader.downloadFileFromFTP(""));
    }
  }

  @Test
  void callDownloadFileAlreadyExists() throws ConfigurationException, IOException {
    try (MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class);
        MockedConstruction<Path> pathMockedConstruction = mockConstruction(Path.class)) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");
      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("false");

      doAnswer(
              invocation -> {
                Object[] args = invocation.getArguments();
                ByteArrayOutputStream out = (ByteArrayOutputStream) args[1];
                out.write("Mock file content".getBytes());
                return true;
              })
          .when(ftpClient)
          .retrieveFile(anyString(), any(ByteArrayOutputStream.class));

      fileSystemMockedStatic
          .when(() -> FileSystem.get(any(URI.class), any(Configuration.class)))
          .thenReturn(hadoopFileSystem);

      when(hadoopFileSystem.exists(any())).thenReturn(true);

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();
      ftpDownloader.setFtpClient(ftpClient);

      assertDoesNotThrow(() -> ftpDownloader.downloadFileFromFTP(""));
    }
  }

  @Test
  void callDownloadFileTestDownloaderException() throws ConfigurationException, IOException {
    try (MockedStatic<FileSystem> fileSystemMockedStatic = mockStatic((FileSystem.class));
        MockedStatic<HarvesterConfigurationUtils> mockedConfig =
            mockStatic(HarvesterConfigurationUtils.class);
        MockedConstruction<URI> uriMockedConstruction = mockConstruction(URI.class);
        MockedConstruction<Path> pathMockedConstruction = mockConstruction(Path.class)) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);
      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");

      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("false");

      doAnswer(
              invocation -> {
                Object[] args = invocation.getArguments();
                ByteArrayOutputStream out = (ByteArrayOutputStream) args[1];
                out.write("Mock file content".getBytes());
                return false;
              })
          .when(ftpClient)
          .retrieveFile(anyString(), any(ByteArrayOutputStream.class));

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();
      ftpDownloader.setFtpClient(ftpClient);

      assertThrows(DownloaderException.class, () -> ftpDownloader.downloadFileFromFTP(""));
    }
  }

  @Test
  void callCloseConnectionsSuccess() throws ConfigurationException, IOException {
    try (MockedStatic<HarvesterConfigurationUtils> mockedConfig =
        mockStatic(HarvesterConfigurationUtils.class)) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");
      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("false");

      when(ftpClient.isConnected()).thenReturn(true);
      when(ftpClient.logout()).thenReturn(true);
      doNothing().when(ftpClient).disconnect();

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();
      ftpDownloader.setFtpClient(ftpClient);

      assertDoesNotThrow(ftpDownloader::closeConnections);
    }
  }

  @Test
  void callCloseConnectionsException() throws ConfigurationException, IOException {
    try (MockedStatic<HarvesterConfigurationUtils> mockedConfig =
        mockStatic(HarvesterConfigurationUtils.class)) {

      mockedConfig
          .when(HarvesterConfigurationUtils::getHarvesterConf)
          .thenReturn(configurationUtils);

      when(configurationUtils.getDownloaderHdfsPath(anyString())).thenReturn("a");
      when(configurationUtils.getQueryParam(anyString())).thenReturn(queryParams);
      when(queryParams.get("host")).thenReturn("host");
      when(queryParams.get("port")).thenReturn("1");
      when(queryParams.get("username")).thenReturn("username");
      when(queryParams.get("password")).thenReturn("password");
      when(queryParams.get("active")).thenReturn("false");

      when(ftpClient.isConnected()).thenReturn(true);
      when(ftpClient.logout()).thenThrow(IOException.class);

      FTPDownloaderTestClass ftpDownloader = new FTPDownloaderTestClass();
      ftpDownloader.setFtpClient(ftpClient);

      assertThrows(DownloaderException.class, ftpDownloader::closeConnections);
    }
  }
}
