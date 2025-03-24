package com.smartshaped.chameleon.harvester.downloader;

import static org.apache.spark.sql.functions.udf;
import static org.apache.spark.sql.types.DataTypes.StringType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.expressions.UserDefinedFunction;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

/**
 * Abstract class that implements the Downloader interface for downloading from an FTP server. It
 * provides the connectToFTP method that can be used by subclasses to connect to the FTP server and
 * the downloadFile method that can be used to download a file from the FTP server.
 */
public abstract class FTPDownloader extends Downloader {

  private final String hdfsPath;
  private final String host;
  private final int port;
  private final String username;
  private final String password;
  private final boolean isActive;
  protected FTPClient ftpClient;

  /**
   * Constructor for the FTPDownloader class. It is protected as it is intended to be used by
   * subclasses.
   *
   * @throws ConfigurationException if there is an error while parsing the configuration
   */
  protected FTPDownloader() throws ConfigurationException {
    super();
    this.hdfsPath = configurationUtils.getDownloaderHdfsPath(className);
    this.host = queryParams.get("host");
    this.port = Integer.parseInt(queryParams.get("port"));
    this.username = queryParams.get("username");
    this.password = queryParams.get("password");
    this.isActive = Boolean.parseBoolean(queryParams.get("active"));
  }

  /**
   * Connects to an FTP server using the specified host, port, username, and password.
   *
   * <p>This method establishes a connection to the FTP server and logs in using the provided
   * credentials. It then sets the connection mode to either active or passive based on the
   * configuration and sets the file type to binary.
   *
   * @throws IOException if an I/O error occurs while connecting to the server.
   * @throws DownloaderException if the server refuses the connection, or if the login fails.
   */
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

  /**
   * Closes the connection to the FTP server.
   *
   * <p>This method logs out from the FTP server and disconnects from it. If the connection is
   * already closed, this method does nothing.
   *
   * @throws DownloaderException if there is an error closing the connection.
   */
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

  /**
   * Downloads the given list of FTP paths and returns a Spark Dataset containing the FTP paths and
   * their corresponding paths on HDFS.
   *
   * @param paramList list of parameters for the request
   * @param request request object
   * @return a Spark Dataset containing the FTP paths and their corresponding paths on HDFS
   * @throws DownloaderException if any error occurs during the download process
   * @throws IOException if an I/O error occurs while connecting to the FTP server
   */
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
            StringType);

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
            .withColumn("path", downloadFTPFile.apply(ftpPathDataset.col("ftp_path")));

    result = addRequest(result, request);

    logger.info("Downloaded successfully with count: {}", result.count());
    return result;
  }

  /**
   * Downloads a file from the given FTP path and stores it on HDFS.
   *
   * @param ftpPath the FTP path of the file to download
   * @return the path of the file on HDFS
   * @throws DownloaderException if any error occurs during the download process
   */
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

  /**
   * Adds a column named "requestContent" to the given Dataset containing the content of the
   * request.
   *
   * <p>This method appends a new column "requestContent" with the content from the provided Request
   * object to the input Dataset<Row>. The content is added as a literal value.
   *
   * @param dataset The Dataset<Row> to which the "requestContent" column will be added.
   * @param request The Request object containing the content to be added to the Dataset.
   * @return The updated Dataset<Row> with the new "requestContent" column.
   */
  protected Dataset<Row> addRequest(Dataset<Row> dataset, Request request) {
    dataset = dataset.withColumn("requestContent", functions.lit(request.getContent()));
    return dataset;
  }

  /**
   * Writes the content of the given InputStream to the specified HDFS path.
   *
   * <p>This method creates a new file on the HDFS at the given path and writes the content of the
   * provided InputStream into it. The file is created with overwrite enabled. The method ensures
   * that the data is flushed and synchronized to disk after writing.
   *
   * @param inputStream the InputStream containing the data to be written to HDFS
   * @param hdfs the HDFS FileSystem object used to interact with HDFS
   * @param hdfsPath the HDFS path where the file will be created
   * @throws DownloaderException if an error occurs during the write operation
   */
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
