package com.smartshaped.chameleon.harvester.downloader;

import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;

public abstract class TextualDownloader extends Downloader {

  protected TextualDownloader() throws ConfigurationException {
    super();
  }

  @Override
  public Dataset<Row> download(List<String> paramList, Request req) throws DownloaderException {
    SparkSession sparkSession = SparkSession.getActiveSession().get();
    logger.info("Starting download with parameters: {}", paramList);
    List<String> uriList = createUriList(paramList, new Request());
    logger.info("Created URI list: {}", uriList);

    Dataset<String> uriDataset = sparkSession.createDataset(uriList, Encoders.STRING());
    logger.info("Created URI dataset with count: {}", uriDataset.count());

    UserDefinedFunction apiCallerUdf =
        functions.udf(
            (String uri) -> {
              logger.info("Calling API for URI: {}", uri);
              String response = callApiAndGetResponse(URI.create(uri));
              logger.info("Received response for URI: {}", uri);
              return response;
            },
            DataTypes.StringType);

    Dataset<Row> result =
        uriDataset.withColumn("response", apiCallerUdf.apply(uriDataset.col("value")));
    logger.info("Download completed successfully with count: {}", result.count());

    return result;
  }

  protected String callApiAndGetResponse(URI uri) throws DownloaderException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

    HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new DownloaderException("API call failed with status: " + response.statusCode());
      }
      return response.body();
    } catch (IOException e) {
      throw new DownloaderException("An I/O error occurs sending or receiving from API", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new DownloaderException("API calling is interrupted", e);
    }
  }
}
