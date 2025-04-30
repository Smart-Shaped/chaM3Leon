# Speed Layer - chaM3Leon Project
## Overview
The Speed module is a component of the chaM3Leon project that handles real-time data processing using Apache Spark and Kafka. It provides functionality for processing streaming data, applying transformations, and storing results in Cassandra for immediate access.

## Features
- Real-time Kafka stream processing
- Spark Streaming integration
- Cassandra data storage
- Configurable speed updates
- Flexible topic handling
- YAML-based configuration

## Architecture
The module consists of several key components:

### SpeedLayer
- Abstract class responsible for orchestrating the real-time processing pipeline
- Handles Spark session management
- Coordinates data flow between Kafka and storage
- Manages streaming query lifecycle

### SpeedUpdater
- Abstract class for implementing real-time update logic
- Manages Cassandra interactions
- Provides customizable data transformation capabilities
- Handles streaming data processing

### SpeedConfigurationUtils
- Handles configuration management
- Supports YAML-based configuration
- Manages Kafka, Spark, and Cassandra settings

## Configuration
The module uses YAML configuration files:

- `framework-config.yml` : Environment-specific configuration
- `local-config.yml` : Local environment configuration
- `typeMapping.yml` : Data type mapping configuration

### Key Configuration Parameters
- Kafka server settings
- Processing intervals
- Topic configurations
- Spark session parameters
- Cassandra connection details
- Speed updater class specifications

To see how to set up the configuration, refer to the [Configuration Guide](../../docs/config_list.md).

## Dependencies
- Apache Spark
- Apache Kafka
- Cassandra
- Log4j2 for logging
- YAML for configuration

## Usage

To develop a batch application using the Speed Layer, follow these steps:

### 1. Create a Class that Extends `com.smartshaped.chameleon.speed.SpeedLayer`
- Ensure that the class constructor is **public**.

### 2. Create a Class that Extends `com.smartshaped.chameleon.speed.SpeedUpdater`
- Ensure that the class constructor is **public**.
- This class permits you to export some partial analysis/statisctics from your streaming data arrived during a window time.
- Declare this class in the YAML file (speed.updater.class).
- Override the `updateSpeed` method to implement the specific logic (working on Spark Dataframe).
- It will automatically save results on Cassandra DB.

### 3. Create a Class that Extends `com.smartshaped.chameleon.common.utils.TableModel`
- Define the table fields as class attributes.
- Specify the name of the primary key as a **string**.
- Create a `typeMapping.yml` file to define the mapping between Java field types and CQL (Cassandra Query Language) types.
- Declare this class in the YAML file (speed.cassandra.model.class).

### 4. Create a Class Containing the `main` Method
- Call the `start` method of `SpeedLayer` inside the `main` method.
- Specify this class in the `spark-submit` command.

---

## Error Handling
The module includes comprehensive exception handling:

- SpeedLayerException
- SpeedUpdaterException
- ConfigurationException
- KafkaConsumerException
- CassandraException

## Testing
Includes unit tests for:

- Exception handling
- Configuration management
- Speed processing logic

## Project Structure
```
speed/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smartshaped/chameleon/speed/
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

For more information about the chaM3Leon project, please refer to the main project documentation.
