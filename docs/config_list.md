# chaM3Leon Configuration

## Batch Layer

batch.updater.class: Specifies the fully qualified class name of the Updater, including package path

## Speed Layer 

speed.updater.class: Specifies the fully qualified class name of the Updater, including package path

## ML Layer

ml.hdfs.readers.default: Defines the default path for storing data processed by the reader

ml.hdfs.readers.{reader_identifier}.class: Specifies the fully qualified class name of the reader, including package path

ml.hdfs.readers.{reader_identifier}.path: Defines the destination path for storing data processed by the reader

ml.hdfs.modelDir: Specifies the storage path for the model when using Pipeline class for machine learning

ml.blackBox.class: Specifies the fully qualified class name of the BlackBox (including package). Note: not compatible with pipeline usage

ml.blackBox.inputs: Defines the parameters required for data acquisition by the blackbox

ml.blackBox.output: Defines the parameters required for acquiring output produced by the blackbox

ml.blackBox.modelPath: Specifies the storage path for the BlackBox model

ml.blackBox.pythonScriptPath: Defines the path where the PythonBlackBox script will be copied

ml.blackBox.pythonExtraScripts: Lists the paths of additional PythonBlackBox scripts, comma-separated

ml.blackBox.pythonLibraries: Lists the Python dependencies required for PythonBlackBox, comma-separated

ml.pipeline.class: Specifies the fully qualified class name of the pipeline (including package). Note: not compatible with blackbox usage

ml.modelSaver.class: Specifies the fully qualified class name of the model saver, including package path

## Harvester Layer

harvester.RequestHandler: Specifies the fully qualified class name of the RequestHandler (including package). Use "default" for standard RequestHandler

harvester.downloader.{downloader_class}.params.*: Defines the configuration parameters for the downloader

harvester.downloader.{downloader_class}.url.*: Defines the URL-related parameters for the downloader

harvester.preprocessor.{preprocessor_class}.params.*: Defines the configuration parameters for the preprocessor

harvester.harvesters.{harvester_identifier}.inputPath: Specifies the input path for harvester data

harvester.harvesters.{harvester_identifier}.class: Specifies the fully qualified class name of the harvester, including package path

harvester.harvesters.{harvester_identifier}.downloader.class: Specifies the fully qualified class name of the downloader, including package path

harvester.harvesters.{harvester_identifier}.transformer: Specifies the fully qualified class name of the transformer, including package path

harvester.harvesters.{harvester_identifier}.preprocessor: Specifies the fully qualified class name of the preprocessor, including package path

harvester.harvesters.{harvester_identifier}.saver.path: Defines the destination path for storing data processed by the saver

## Common Configurations

### Apache Spark

{layer}.spark.*: Defines Spark environment configurations (master, deploy-mode, app-name, interval-sec, etc.)

### Apache Cassandra

The following configurations are shared among Batch, Speed, and ML layers:

{layer}.spark.cassandra.connection.host: Specifies the Cassandra node hostname

{layer}.spark.cassandra.connection.port: Specifies the Cassandra node connection port

{layer}.cassandra.model.class: Specifies the fully qualified class name of the Cassandra model, including package path

{layer}.cassandra.datacenter: Defines the reference Cassandra datacenter

{layer}.cassandra.checkpoint: Specifies the path for storing Cassandra data checkpoints

{layer}.cassandra.keyspace.name: Defines the Cassandra keyspace name (will be created if non-existent)

{layer}.cassandra.keyspace.replication_factor: Defines the replication factor for the Cassandra keyspace (required for keyspace creation)

### Apache Kafka

The following configurations are shared between Batch and Speed layers:

{layer}.kafka.server: Specifies the Kafka server in hostname:port format

{layer}.kafka.intervalMs: Defines the interval in milliseconds between consecutive Kafka reads

{layer}.kafka.topics.{topic_identifier}.name: Specifies the Kafka topic name

{layer}.kafka.topics.{topic_identifier}.class: Specifies the fully qualified class name of the preprocessor for Kafka topic data, including package path

{layer}.kafka.topics.{topic_identifier}.path: Defines the destination path for storing data from the Kafka topic

{layer}.kafka.topics.{topic_identifier}.checkpoint: Specifies the path for storing checkpoints of data from the Kafka topic
