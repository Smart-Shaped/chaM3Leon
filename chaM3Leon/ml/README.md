# ML Layer - chaM3Leon Project
## Overview
The ML module is a component of the chaM3Leon project that handles machine learning tasks using Apache Spark and HDFS. It provides functionality for data processing, model training, and predictions.

## Features
- Machine learning pipeline implementation
- HDFS data integration
- Model training and saving
- Cassandra data storage
- Configurable ML workflows
- YAML-based configuration
- Multi-language support

## Architecture
The module consists of several key components:

### MLLayer
- Abstract class responsible for orchestrating the ML pipeline
- Handles Spark session management
- Coordinates data flow between HDFS and storage
- Manages ML pipeline lifecycle

### HdfsReader
- Abstract class for reading data from HDFS
- Provides customizable data processing capabilities
- Handles raw data transformation
- Supports multiple data formats

### Pipeline
- Abstract class for implementing ML workflows
- Manages model training and prediction processes
- Provides customizable pipeline steps
- Handles model evaluation

### Blackbox
- Abstract class for implementing blackbox ML workflows
- Manages model training and prediction processes
- Provides customizable pipeline steps
- Handles model evaluation
- Supports Python implementation

### ModelSaver
- Abstract class for saving ML models
- Manages model and predictions persistence

### TableModel
- Base class for defining data models
- Handles Cassandra table mapping
- Provides type conversion utilities
- Supports custom field definitions

## Configuration
The module uses YAML configuration files:

- `framework-config.yml` : Environment-specific configuration
- `local-config.yml` : Local environment configuration
- `typeMapping.yml` : Data type mapping configuration

### Key Configuration Parameters
- HDFS reader configurations
- Pipeline or Blackbox configurations
  - Optional Python Blackbox configurations
- Model saver configurations
- Spark session parameters
- Cassandra connection details

## Dependencies
- Apache Spark
- HDFS
- Cassandra
- Optional Python

## How to Develop an ML Application

To develop a machine learning application using the ML Layer, follow these steps:

### 1. Create a Class that Extends `com.smartshaped.chameleon.ml.MLLayer`
- Ensure that the class constructor is **public**.

### 2. Create at Least One Class that Extends `com.smartshaped.chameleon.ml.HdfsReader`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file along with the HDFS path from which the data will be read.
- Optionally, override the `processRawData` method to add custom processing for the raw data.

### 3.A Create a Class that Extends `com.smartshaped.chameleon.ml.Pipeline`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file.
- Override the `start` method to implement the specific machine learning logic.
- Optionally override the `evaluatePredictions` and `evaluateModel` methods to implement custom evaluation logic.
- Optionally override the `readModelFromHDFS` method to implement custom model reading logic.
- Ensure that the `setModel` and `setPredictions` methods are called at the end of the pipeline.

### 3.B Create a Class that Extends `com.smartshaped.chameleon.ml.Blackbox`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file.
- Override the `runML` method to implement the specific machine learning logic.
- Override the `makeDatasetAccessible` and `readOutput` method to implement custom dataset accessibility logic.
- Optionally override the `extraPreparation` and `postRunning` methods to implement custom logic after and before the ML process.
- Optionally override the `mergeDatasetsIfNecessary` method to implement custom dataset merging logic.
- Optionally override the `validateParams` method to implement custom parameter validation logic.
- Optionally override the `cleanBlackBoxFolder` method to add custom cleanup logic.

### 4. Create a Class that Extends `com.smartshaped.chameleon.ml.ModelSaver`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file.

### 5. Create a Class that Extends `com.smartshaped.chameleon.common.utils.TableModel`
- Define the table fields as class attributes.
- Specify the name of the primary key or the composite primary key as a **string**.
- Create a `typeMapping.yml` file to define the mapping between Java field types and CQL (Cassandra Query Language) types.
- Declare this class in the YAML file.

### 6. Create a Class Containing the `main` Method
- Call the `start` method of `MLLayer` inside the `main` method.
- Specify this class in the `spark-submit` command.

---

## Error Handling
The module includes comprehensive exception handling:

- MLLayerException
- HdfsReaderException
- ModelSaverException
- ConfigurationException
- CassandraException
- PipelineException
- BlackboxException

---

## Testing
Includes unit tests for:

- Exception handling
- Configuration management
- HdfsReader functionality
- Pipeline functionality
- Blackbox functionality
  - Python integration
- ModelSaver functionality

## Project Structure
```
ml/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smartshaped/chameleon/ml/
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
