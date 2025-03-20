package com.smartshaped.chameleon.harvester.downloader;

import static org.apache.spark.sql.functions.udf;
import static org.apache.spark.sql.types.DataTypes.StringType;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.*;
import org.apache.spark.sql.expressions.UserDefinedFunction;

public abstract class FTPDownloader extends Downloader {

  private final String hdfsPath;
  private final String host;
  private final int port;
  private final String username;
  private final String password;
  private final boolean isActive;
  protected FTPClient ftpClient;

  protected FTPDownloader() throws ConfigurationException {
    super();
    this.hdfsPath = configurationUtils.getDownloaderHdfsPath(className);
    this.host = queryParams.get("host");
    this.port = Integer.parseInt(queryParams.get("port"));
    this.username = queryParams.get("username");
    this.password = queryParams.get("password");
    this.isActive = Boolean.parseBoolean(queryParams.get("active"));
  }

  private void connectToFTP() throws IOException, DownloaderException {
    ftpClient = new FTPClient();
    logger.info("Connecting to FTP server {}:{}", host, port);
    ftpClient.connect(host, port);

    int reply = ftpClient.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      ftpClient.disconnect();
      throw new DownloaderException("FTP server refused connection");
    }

    if (!ftpClient.login(username, password)) {
      throw new DownloaderException("FTP login failed");
    }
    logger.info("Connected successfully to FTP server");

    if (isActive) {
      ftpClient.enterLocalActiveMode();
    } else {
      ftpClient.enterLocalPassiveMode();
    }

    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
  }

  @Override
  public void closeConnections() throws DownloaderException {
    if (ftpClient != null && ftpClient.isConnected()) {
      try {
        ftpClient.logout();
        ftpClient.disconnect();
        logger.info("FTP connection closed");
      } catch (IOException e) {
        throw new DownloaderException("Error closing FTP connection", e);
      }
    }
  }

  @Override
  public Dataset<Row> download(List<String> paramList, Request request)
      throws DownloaderException, IOException {
    SparkSession sparkSession = SparkSession.getActiveSession().get();
    if (ftpClient == null || !ftpClient.isConnected()) {
      connectToFTP();
    }

    UserDefinedFunction downloadFTPFile =
        udf(
                (String ftpPath) -> {
                  logger.info("Downloading file from FTP path: {}", ftpPath);
                  return downloadFileFromFTP(ftpPath);
                },
                StringType)
            .asNondeterministic();

    List<String> ftpPaths = createUriList(paramList, request);

    if (ftpPaths.isEmpty()) {
      logger.warn("No FTP paths found for download");
      return null;
    }

    Dataset<String> ftpPathDataset = sparkSession.createDataset(ftpPaths, Encoders.STRING());
    logger.info("Created FTP path dataset");
    logger.debug("Created FTP path dataset with count: {}", ftpPathDataset.count());

    Dataset<Row> result =
        ftpPathDataset
            .withColumnRenamed("value", "ftp_path")
            .withColumn("path", downloadFTPFile.apply(functions.col("ftp_path")));

    result = addRequest(result, request);

    logger.info("Downloaded successfully with count: {}", result.count());
    return result;
  }

  protected String downloadFileFromFTP(String ftpPath) throws DownloaderException {
    try {
      logger.info("Downloading file from FTP: {}", ftpPath);

      String fileName = ftpPath.substring(ftpPath.lastIndexOf(File.separator) + 1);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      boolean success = ftpClient.retrieveFile(ftpPath, outputStream);

      if (success) {
        Path finalHdfsPath = new Path(hdfsPath.concat(File.separator).concat(fileName));
        logger.info("File downloaded successfully. Creating HDFS path: {}", finalHdfsPath);

        Configuration configuration = new Configuration();
        FileSystem hdfs = FileSystem.get(new URI(hdfsPath), configuration);

        if (!hdfs.exists(finalHdfsPath)) {
          logger.info("File does not exist on HDFS. Creating file.");
          try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
            writeFileToHDFS(inputStream, hdfs, finalHdfsPath);
          }
          logger.info("File created successfully on HDFS: {}", fileName);
        } else {
          logger.info("File already exists on HDFS: {}", fileName);
        }

        hdfs.close();
        return finalHdfsPath.toString();
      } else {
        throw new DownloaderException("Failed to download file from FTP: " + ftpPath);
      }
    } catch (IOException | URISyntaxException | DownloaderException e) {
      throw new DownloaderException("Error downloading file from FTP: " + ftpPath, e);
    }
  }

  protected Dataset<Row> addRequest(Dataset<Row> dataset, Request request) {
    dataset = dataset.withColumn("requestContent", functions.lit(request.getContent()));
    return dataset;
  }

  private void writeFileToHDFS(InputStream inputStream, FileSystem hdfs, Path hdfsPath)
      throws DownloaderException {
    try (FSDataOutputStream outputStream = hdfs.create(hdfsPath, true)) {
      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.hflush();
      outputStream.hsync();
    } catch (IOException e) {
      throw new DownloaderException("Error writing to HDFS", e);
    }
  }
}
