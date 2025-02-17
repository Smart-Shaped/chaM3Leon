package com.smartshaped.chameleon.harvester;

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

import com.smartshaped.chameleon.common.exception.CassandraException;
import com.smartshaped.chameleon.common.exception.ConfigurationException;
import com.smartshaped.chameleon.harvester.exception.HarvesterException;
import com.smartshaped.chameleon.harvester.exception.HarvesterLayerException;
import com.smartshaped.chameleon.harvester.request.Request;
import com.smartshaped.chameleon.harvester.request.RequestHandler;
import com.smartshaped.chameleon.harvester.utils.HarvesterConfigurationUtils;
import com.smartshaped.chameleon.ml.exception.HdfsReaderException;
import com.smartshaped.chameleon.preprocessing.exception.PreprocessorException;

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

  public HarvesterLayer() throws ConfigurationException, CassandraException {

    configurationUtils = HarvesterConfigurationUtils.getHarvesterConf();
    logger.info("Harvester configurations loaded correctly");
    handler = configurationUtils.getRequestHandler();
    logger.info("Request handler loaded correctly");
    harvesters = configurationUtils.getHarvesters();
    logger.info("Harvesters list loaded correctly");

    try {
      logger.info("Loading configuration for spark session...");
      SparkConf sparkConf = configurationUtils.getSparkConf();
      SparkSession config = SedonaContext.builder().config(sparkConf).getOrCreate();
      sparkSession = SedonaContext.create(config);
      logger.info("Spark session successfully created");
    } catch (Exception e) {
      throw new ConfigurationException("Error getting or creating Sedona SparkSession", e);
    }
  }

  /**
   * Start the harvesting process.
   *
   * <p>The method reads all the requests from the database, filters the harvesters that match the
   * request, and calls the {@link Harvester#execute(Request)} method for each of them. If an
   * exception is thrown during the harvesting process, the request is marked as error and the
   * exception is re-thrown.
   *
   * @throws ConfigurationException If there is an error in the configuration
   * @throws HdfsReaderException If there is an error when reading from HDFS
   * @throws CassandraException If there is an error when interacting with Cassandra
   * @throws HarvesterException If there is an error during the harvesting process
   */
  public void start()
      throws ConfigurationException,
          HdfsReaderException,
          CassandraException,
          HarvesterLayerException {

    logger.info("Starting harvesting process");

    Request[] requests = handler.getRequest();
    String state = "";
    List<Harvester> filteredHarvesters;

    for (Request request : requests) {
      logger.debug("Processing request: {}", request);
      try {
        filteredHarvesters = filterHarvesters(harvesters, request);
        for (Harvester harvester : filteredHarvesters) {
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

      logger.info("Request completed");
    }
    handler.closeConnection();
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
}
