package com.smartshaped.chameleon.harvester.downloader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.request.Request;

public abstract class SingleThreadJsonDownloader extends Downloader<JsonNode> {

  protected static final Logger log = LogManager.getLogger(SingleThreadJsonDownloader.class);

  protected SingleThreadJsonDownloader() throws ConfigurationException {
    super();
  }

  @Override
  public JsonNode download(List<String> reqParams, Request req) throws DownloaderException {

    logger.info("Starting downloading process...");
    List<String> uriList = createUriList(reqParams);
    List<HttpResponse<String>> responses = new LinkedList<>();
    HttpResponse<String> response = null;
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request;
    for (String uri : uriList) {
      URI newUri = URI.create(uri);
      request = HttpRequest.newBuilder().uri(newUri).build();
      log.debug("Request uri:{}", request.uri());
      try {
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
      } catch (IOException | InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new DownloaderException(e.getMessage());
      }
      if (response.statusCode() != 200) {
        throw new DownloaderException("Status code returned:" + response.statusCode());
      }
      log.debug("Response:{}", response.body());

      responses.add(response);
    }
    logger.info("Resources successfully downloaded");
    return joinResponses(responses);
  }

  protected abstract JsonNode joinResponses(List<HttpResponse<String>> responses);
}
