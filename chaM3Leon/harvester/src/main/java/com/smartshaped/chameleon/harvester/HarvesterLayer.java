package com.smartshaped.chameleon.harvester;

import com.smartshaped.chameleon.common.utils.exception.CassandraException;
import com.smartshaped.chameleon.common.utils.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.DownloaderException;
import com.smartshaped.chameleon.harvester.exception.HarvesterException;
import com.smartshaped.chameleon.harvester.exception.HarvesterLayerException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.request.RequestHandler;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;
import com.smartshaped.chameleon.common.preprocessing.exception.PreprocessorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sedona.spark.SedonaContext;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

/**
 * The HarvesterLayer class is responsible for starting the harvesting process. It reads all the
 * requests from the database, filters the harvesters that match the request, and calls the {@link
 * Harvester#execute(Request)} method for each of them.
 */
public class HarvesterLayer {

  private static final Logger logger = LogManager.getLogger(HarvesterLayer.class);

  protected HarvesterConfigurationUtils configurationUtils;
  protected SparkSession sparkSession;
  protected RequestHandler handler;
  protected List<Harvester> harvesters;
  protected List<Harvester> filteredHarvesters;
  private Thread shutdownHook;

  /**
   * Constructs a HarvesterLayer instance, initializing necessary configurations.
   *
   * @throws ConfigurationException If there is an error in loading configurations
   * @throws CassandraException If there is an error in establishing Cassandra connection
   */
  public HarvesterLayer() throws ConfigurationException, CassandraException {

    configurationUtils = HarvesterConfigurationUtils.getHarvesterConf();
    logger.info("Harvester configurations loaded correctly");

    try {
      logger.info("Loading configuration for spark session...");
      SparkConf sparkConf = configurationUtils.getSparkConf();
      SparkSession config = SedonaContext.builder().config(sparkConf).getOrCreate();
      sparkSession = SedonaContext.create(config);
      logger.info("Spark session successfully created");
    } catch (Exception e) {
      throw new ConfigurationException("Error getting or creating Sedona SparkSession", e);
    }

    handler = configurationUtils.getRequestHandler();
    logger.info("Request handler loaded correctly");
    harvesters = configurationUtils.getHarvesters();
    logger.info("Harvesters list loaded correctly");
  }

  /**
   * Starts the harvesting process by filtering the harvesters that match each request and calling
   * the {@link Harvester#execute(Request)} method for each of them.
   *
   * <p>This method loops through all the requests in the database. For each request, it filters the
   * harvesters that match the request, and calls the {@link Harvester#execute(Request)} method for
   * each of them.
   *
   * <p>If there is an error during the execution of a harvester, the state of the request is set to
   * "error" and the error is propagated.
   *
   * <p>When all the requests have been processed, the connections are closed.
   *
   * @throws CassandraException If there is an error in establishing Cassandra connection
   * @throws HarvesterLayerException If there is an error during the harvesting process
   */
  public void start() throws CassandraException, HarvesterLayerException {

    logger.info("Starting harvesting process");

    Request[] requests = handler.getRequest();
    String state = "";

    for (Request request : requests) {
      this.shutdownHook =
          new Thread(
              () -> {
                logger.info("Closing Spark Application...");
                try {
                  RequestHandler killedHandler = configurationUtils.getRequestHandler();
                  logger.info("Request value: {}", request);
                  killedHandler.updateRequestState(request, "blocked");
                } catch (CassandraException | ConfigurationException e) {
                  throw new RuntimeException(e.getMessage(), e);
                }
              });
      Runtime.getRuntime().addShutdownHook(shutdownHook);
      logger.debug("Processing request: {}", request);
      state = "inProgress";
      handler.updateRequestState(request, state);
      try {
        this.filteredHarvesters = filterHarvesters(harvesters, request);
        for (Harvester harvester : this.filteredHarvesters) {
          logger.debug("Using harvester: {}", harvester.getClass());
          harvester.execute(request);
        }
        state = "completed";
      } catch (HarvesterException | PreprocessorException e) {
        state = "error";
        throw new HarvesterLayerException("Error during the request: " + request, e);
      } finally {
        handler.updateRequestState(request, state);
      }
      closeHarvesterConnections();
      logger.info("Request completed");
    }
    this.closeConnections();
  }

  /**
   * Filters the list of harvesters based on the harvester IDs provided in the request.
   *
   * <p>It splits the harvester IDs from the request into a set and loops through each harvester,
   * checking if its ID is present in the set. If a match is found, the harvester is added to the
   * resulting list.
   *
   * @param harvesters The list of harvester instances to be filtered.
   * @param request The request containing harvester IDs used for filtering.
   * @return A list of harvesters that match the IDs specified in the request.
   */
  private static List<Harvester> filterHarvesters(List<Harvester> harvesters, Request request) {

    logger.debug("Number of harvesters: {}", harvesters.size());
    for (Harvester harvester : harvesters) {
      logger.debug("Harvester: {}", harvester);
    }

    List<Harvester> harvesterList = new ArrayList<>();

    Set<String> set =
        Arrays.stream(request.getHarvesterIds().split(",")).collect(Collectors.toSet());

    logger.info("Request Harvetser IDs: {}", set);

    for (Harvester harvester : harvesters) {
      String harvesterId = harvester.getHarvesterId().split("\\.")[2];
      if (set.contains(harvesterId)) {
        harvesterList.add(harvester);
      }
    }

    logger.debug("Number of filtered harvesters: {}", harvesterList.size());

    return harvesterList;
  }

  /** Closes Cassandra connection and Spark Session at the end of the Harvester Layer execution. */
  private void closeConnections() {
    this.handler.closeConnection();
    this.sparkSession.close();
    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
  }

  /**
   * Closes all opened connections through filtered Harvester after their execution..
   *
   * @throws HarvesterLayerException If there is an error closing harvester pending connections.
   */
  private void closeHarvesterConnections() throws HarvesterLayerException {
    for (Harvester harvester : this.filteredHarvesters) {
      try {
        harvester.closeConnections();
      } catch (DownloaderException | PreprocessorException e) {
        throw new HarvesterLayerException(
            "Exception raised closing pending connections for Harvester: "
                + harvester.getHarvesterId(),
            e);
      }
    }
  }
}
