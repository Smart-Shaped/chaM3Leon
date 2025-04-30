# Batch Layer - chaM3Leon Project

## Overview
The Batch module is a component of the chaM3Leon project that handles batch processing of data using Apache Spark and Kafka. It provides functionality for processing streaming data, applying transformations, and storing results in HDFS and Cassandra.

## Features
- Kafka stream processing
- Data preprocessing capabilities
- HDFS data storage
- Cassandra integration
- Configurable batch updates
- Flexible topic handling
- YAML-based configuration

## Architecture
The module consists of several key components:

### BatchLayer
- Main component responsible for orchestrating the batch processing pipeline
- Handles Spark session management
- Coordinates data flow between Kafka, preprocessors, and storage

### BatchUpdater
- Abstract class for implementing batch update logic
- Manages Cassandra interactions
- Provides customizable data transformation capabilities

### HdfsSaver
- Handles data storage in HDFS
- Supports checkpointing for fault tolerance
- Supports data partitioning and compression

### BatchConfigurationUtils
- Handles configuration management
- Supports YAML-based configuration
- Manages Kafka, preprocessor, and batch updater settings

## Configuration
The module uses YAML configuration files:
- `framework-config.yml`: Environment-specific configuration
- `local-config.yml` : Local environment configuration
- `typeMapping.yml`: Data type mapping configuration

### Key Configuration Parameters
- Kafka server settings
- Processing intervals
- Topic configurations
- Spark session parameters
- HDFS paths and checkpoints
- Cassandra connection details
- Batch updater class specifications

To see how to set up the configuration, refer to the [Configuration Guide](../../docs/config_list.md).

## Dependencies
- Apache Spark
- Apache Kafka 
- Cassandra
- Log4j2 for logging
- YAML for configuration

## Usage

To develop a batch application using the Batch Layer, follow these steps:

### 1. Create a Class that Extends `com.smartshaped.chameleon.batch.BatchLayer`
- Ensure that the class constructor is **public**.

### 2. Create one or more Classes that Extend `com.smartshaped.chameleon.common.preprocessing.Preprocessor`
- Declare this class in the YAML file along with the kafka topics configurations (batch.kafka.topics.<topic_name>.class).
- Override the `preprocess` method to add custom preprocessing for the incoming streaming data.
- You can define a Preprocessor for each of the declared kafka topics.

### 3. Create a Class that Extends `com.smartshaped.chameleon.batch.BatchUpdater`
- Ensure that the class constructor is **public**.
- This is an optional step, create this class if you want to export some analysis/statisctics from your data.
- Declare this class in the YAML file (batch.updater.class).
- Override the `updateBatch` method to implement the specific logic (working on Spark Dataframe).
- It will automatically save results on Cassandra DB.

### 4. Create a Class that Extends `com.smartshaped.chameleon.common.utils.TableModel`
- Define the table fields as class attributes.
- Specify the name of the primary key as a **string**.
- Create a `typeMapping.yml` file to define the mapping between Java field types and CQL (Cassandra Query Language) types.
- Declare this class in the YAML file (batch.cassandra.model.class).

### 5. Create a Class Containing the `main` Method
- Call the `start` method of `com.smartshaped.chameleon.batch.BatchLayer` inside the `main` method.
- Specify this class in the `spark-submit` command.

---

## Error Handling
The module includes comprehensive exception handling:
- BatchLayerException
- BatchUpdaterException
- ConfigurationException
- PreprocessorException
- HdfsSaverException

## Testing
Includes unit tests for:
- Exception handling
- Configuration management
- Batch processing logic

## Project Structure
```
batch/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smartshaped/chameleon/batch/
│   │   └── resources/
│   └── test/
│       ├── java/
│       └── resources/
└── pom.xml
```

## Contributing
When contributing to this module, please ensure:
- Follow the existing code style
- Add appropriate unit tests
- Update configuration documentation
- Maintain backward compatibility

## License
This project is licensed under the terms included in the root project directory.

---
For more information about the chaM3Leon project, please refer to the main project documentation.
