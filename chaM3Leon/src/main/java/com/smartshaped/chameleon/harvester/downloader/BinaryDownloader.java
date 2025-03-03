package com.smartshaped.chameleon.harvester.downloader;

import static org.apache.spark.sql.functions.udf;
import static org.apache.spark.sql.types.DataTypes.StringType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.UserDefinedFunction;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

public abstract class BinaryDownloader extends Downloader {
  private static final Logger logger = LogManager.getLogger(BinaryDownloader.class);
  private final HttpClient httpClient;
  private final String hdfsPath;

  protected BinaryDownloader() throws ConfigurationException {
    super();
    this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    this.hdfsPath = configurationUtils.getDownloaderHdfsPath(className);
  }

  @Override
  public Dataset<Row> download(List<String> paramlist, Request request) throws DownloaderException {
    SparkSession sparkSession = SparkSession.getActiveSession().get();

    UserDefinedFunction downloadBinaryFile =
        udf(
            (String url) -> {
              logger.info("Downloading file from url: {}", url);
              downloadFile(url);
              return url;
            },
            StringType);

    List<String> urls = createUriList(paramlist, request);

    Dataset<String> uriDataset = sparkSession.createDataset(urls, Encoders.STRING());

    return uriDataset.withColumn("path", downloadBinaryFile.apply(uriDataset.col("value")));
  }

  private void writeFileToHDFS(HttpResponse<InputStream> response, FileSystem hdfs, Path hdfsPath)
      throws DownloaderException {
    try (InputStream inputStream = response.body();
        FSDataOutputStream outputStream = hdfs.create(hdfsPath, true)) {
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

  protected void downloadFile(String url) throws DownloaderException {
    try {
      logger.info("Downloading file: {}", url);
      HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
      HttpResponse<InputStream> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
      if (response.statusCode() == 200) {
        String fileName = createStructuredFileName(url);
        logger.info("File downloaded successfully. Creating HDFS path: {}, {}", hdfsPath, fileName);
        Configuration configuration = new Configuration();
        FileSystem hdfs = FileSystem.get(new URI(hdfsPath), configuration);
        Path finalPath = new Path(hdfsPath.concat("/").concat(fileName));
        if (!hdfs.exists(finalPath)) {
          logger.info("File does not exist on HDFS. Creating file.");
          writeFileToHDFS(response, hdfs, finalPath);
          logger.info("File created successfully on HDFS: {}", fileName);
        } else {
          logger.info("File already exists on HDFS: {}", fileName);
        }
        hdfs.close();
      } else {
        throw new DownloaderException(
            "Error downloading file with status code: "
                + response.statusCode()
                + " from url : "
                + url);
      }
    } catch (InterruptedException | IOException | URISyntaxException e) {
      Thread.currentThread().interrupt();
      throw new DownloaderException("Failed to download or save the file: " + url, e);
    }
  }

  protected abstract String createStructuredFileName(String url);
}
