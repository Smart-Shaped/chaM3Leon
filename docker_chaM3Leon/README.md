# How to Run a Spark Application on YARN and View Logs

1. **Create a `/jars` Directory in `docker_chaM3Leon`**  
   - Create a directory named `/jars` in `docker_chaM3Leon`.

2. **Copy the Application JAR**  
   - Place the application `.jar` file inside the `docker_chaM3Leon/jars` directory.

3. **Build the Docker Environment**  
   - Run the following command:  
     ```bash
     docker-compose build
     ```

4. **Start the Docker Containers**  
   - Launch the environment with:  
     ```bash
     docker-compose up
     ```

5. **Insert Hadoop's XML Configurations into Spark Master**  
   - Copy Hadoop configurations from the NameNode to your local directory:  
     ```bash
     docker cp docker_chaM3Leon-namenode-1:/opt/hadoop/etc/hadoop/. ./hadoop
     ```

6. **Copy Spark JARs into Hadoop**  
   - Transfer Spark JARs from the Spark Master to Hadoop:  
     ```bash
     docker cp docker_chaM3Leon-spark-master-1:/opt/bitnami/spark/jars/. ./spark_jars
     ```

7. **Set Up HDFS Directories for Spark JARs and Logs**  
   - From the NameNode terminal, execute:  
     ```bash
     hdfs dfs -mkdir -p /user/spark/eventLog
     hdfs dfs -mkdir /spark
     hdfs dfs -mkdir /spark/jars
     hdfs dfs -mkdir /spark/logs
     hdfs dfs -put /opt/hadoop/dfs/spark/jars/* /spark/jars
     ```

8. **Set Permissions for Spark**  
   - Grant permissions for Spark operations on HDFS:  
     ```bash
     hdfs dfs -chmod -R 777 /user/spark/eventLog
     hdfs dfs -chown -R spark:hadoop /spark
     hdfs dfs -chmod -R 777 /spark
     ```
   - Run the following (for testing purposes):  
     ```bash
     hdfs dfs -chmod -R 777 /
     hdfs dfs -chown -R spark:hadoop /
     ```

9. **Create Kafka Topics**  
   - From the Kafka container terminal, use the following command to create topics:  
     ```bash
     kafka-topics --create --topic <topic_name> --partitions <num_partitions> --replication-factor <replication_factor> --bootstrap-server kafka1:19092
     ```

10. **Run Spark Applications from Spark Master**  
    - **Local Mode:**  
      ```bash
      spark-submit --class <main_class> --master spark://spark-master:7077 ./extra_jars/<application_name>.jar
      ```
    - **YARN Cluster Mode:**  
      ```bash
      spark-submit --class <main_class> --master yarn --deploy-mode cluster ./extra_jars/<application_name>.jar
      ```
    - **YARN Client Mode:**  
      ```bash
      spark-submit --class <main_class> --master yarn --deploy-mode client ./extra_jars/<application_name>.jar
      ```

11. **View Logs for Cluster Mode Execution**  
    - Do the following:  
      - Run:  
        ```bash
        yarn logs -applicationId <applicationId> -log_files_pattern stderr
        ```
      - View logs in the ResourceManager at:  
        ```
        /var/log/hadoop/userlogs/<applicationId>/<containerId>/stderr
        ```
      - Download logs locally:  
        ```bash
        docker cp <container_id>:/var/log/hadoop/userlogs/<applicationId>/<containerId>/stderr ./local_dir
        ```
