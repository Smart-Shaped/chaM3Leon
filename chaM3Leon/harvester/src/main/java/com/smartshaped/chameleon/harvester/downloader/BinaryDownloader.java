package com.smartshaped.chameleon.harvester.downloader;

import static org.apache.spark.sql.functions.udf;
import static org.apache.spark.sql.types.DataTypes.StringType;

import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
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
import org.apache.spark.sql.functions;

/**
 * The BinaryDownloader class is responsible for downloading binary files from a list of URLs and
 * storing them on HDFS. It extends the Downloader class and provides a method to download files and
 * return a Spark Dataset containing the URLs and their corresponding HDFS paths.
 *
 * <p>It uses HttpClient for making HTTP requests and interacts with HDFS to store the downloaded
 * files. The class is abstract and requires implementation of methods to create a structured file
 * name and URI list.
 */
public abstract class BinaryDownloader extends Downloader {
  private static final Logger logger = LogManager.getLogger(BinaryDownloader.class);
  private final HttpClient httpClient;
  private final String hdfsPath;

  /**
   * Creates a new BinaryDownloader instance.
   *
   * @throws ConfigurationException if an error occurs while getting the HDFS path from the
   *     configuration
   */
  protected BinaryDownloader() throws ConfigurationException {
    super();
    this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    this.hdfsPath = configurationUtils.getDownloaderHdfsPath(className);
  }

  /**
   * Downloads the given list of URLs and returns a Spark Dataset containing the URLs and the
   * corresponding paths on HDFS.
   *
   * @param paramList list of parameters for the request
   * @param request request object
   * @return a Spark Dataset containing the URLs and the corresponding paths on HDFS
   * @throws DownloaderException if any error occurs during the download process
   */
  @Override
  public Dataset<Row> download(List<String> paramList, Request request) throws DownloaderException {
    SparkSession sparkSession = SparkSession.getActiveSession().get();

    UserDefinedFunction downloadBinaryFile =
        udf(
            (String url) -> {
              logger.info("Downloading file from url: {}", url);
              return downloadFile(url);
            },
            StringType);

    List<String> urls = createUriList(paramList, request);

    Dataset<String> uriDataset = sparkSession.createDataset(urls, Encoders.STRING());
    logger.info("Created URI dataset");
    logger.debug("Created URI dataset with count: {}", uriDataset.count());

    Dataset<Row> uriFinal =
        uriDataset.withColumn("path", downloadBinaryFile.apply(uriDataset.col("value")));

    uriFinal = addRequest(uriFinal, request);
    return uriFinal;
  }

  /**
   * Downloads a file from the given URL and stores it on HDFS.
   *
   * @param url the URL of the file to download
   * @return the path of the file on HDFS
   * @throws DownloaderException if any error occurs during the download process
   */
  protected String downloadFile(String url) throws DownloaderException {
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
        return finalPath.toString();
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

  /**
   * Writes the content of the given InputStream to the given HDFS path.
   *
   * @param response the HTTP response containing the file to download
   * @param hdfs the HDFS file system
   * @param hdfsPath the path on HDFS where the file will be written
   * @throws DownloaderException if any error occurs while writing to HDFS
   */
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
}
