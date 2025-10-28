# Harvester Layer - chaM3Leon Project

## Overview
The Harvester module is a component of the chaM3Leon project that handles data collection from various sources. It provides functionality for downloading data from APIs and other sources, processing it using Apache Spark, and storing the results for further analysis.

## Features
- Data downloading: Supports downloading textual and binary data from APIs and other sources
- Distributed processing: Uses Apache Spark for parallel data processing
- Configurable preprocessing: Allows defining custom preprocessors for data transformation
- Request management: System for managing requests with states (inProgress, completed, error, blocked)
- Flexible configuration: Extensible configuration through configuration files
- Geospatial capabilities: Integration with Apache Sedona for geospatial data processing
- Exception handling: Comprehensive error management system

## Architecture
The module consists of several key components:

### HarvesterLayer
- Main entry point of the module
- Initializes the Spark session
- Manages requests through the RequestHandler
- Filters and coordinates the execution of appropriate harvesters

### Harvester
- Abstract class that defines the workflow for the data collection process:
  1. Parameter extraction from the request
  2. Data download through a Downloader
  3. Data preprocessing through a Preprocessor
  4. Saving the processed data

### Downloader
- Abstract class for implementing data download logic
- Provides methods for creating URI lists and downloading data
- Includes specialized implementations like TextualDownloader

### HarvesterConfigurationUtils
- Handles configuration management
- Supports configuration file-based settings
- Manages harvesters, downloaders, and preprocessor settings

## Configuration
The module uses YAML configuration files:
- `framework-config.yml`: Environment-specific configuration
- `local-config.yml` : Local environment configuration
- `typeMapping.yml`: Data type mapping configuration

### Key Configuration Parameters
- Spark session settings
- Request handler configuration
- Harvester class specifications
- Downloader settings
- Preprocessor configurations
- Input/output paths
  
To see how to set up the configuration, refer to the [Configuration Guide](../../docs/CONFIG_LIST.md).

## Dependencies
- Apache Spark: for distributed data processing
- Apache Sedona: for geospatial capabilities
- Apache Commons Configuration: for configuration management
- HttpClient: for HTTP requests
- Log4j: for logging

## Usage

To develop a harvester application using the Harvester Layer, follow these steps:

### 1. Create a Class that Extends `com.smartshaped.chameleon.harvester.Harvester`
- Ensure that the class constructor is **public**.
- Override the `extractParams` method to define how parameters are extracted from requests.

### 2. Create a Class that Extends `com.smartshaped.chameleon.harvester.downloader.Downloader`
- Implement the `createUriList` method to generate URIs for data download.
- Implement the `download` method to define how data is downloaded.
- Implement the `closeConnections` method to handle resource cleanup.

### 3. Create a Class that Extends `com.smartshaped.chameleon.preprocessing.Preprocessor` (Optional)
- Override the `preprocess` method to add custom preprocessing for the downloaded data.
- Implement the `closeConnections` method if needed.

### 4. Configure Your Harvester in Configuration Files
- Define harvester IDs
- Specify downloader and preprocessor classes
- Configure input and output paths

### 5. Create a Class Containing the `main` Method
- Call the `start` method of `com.smartshaped.chameleon.harvester.HarvesterLayer` inside the `main` method.
- Specify this class in the Spark submit command.

## Error Handling
The module includes comprehensive exception handling:
- DownloaderException: for errors during the download process
- HarvesterException: for errors during the harvesting process
- HarvesterLayerException: for errors at the layer level

## Testing
Includes unit tests for:
- Exception handling
- Configuration management
- Harvester layer functionality
- Downloader implementations

## Project Structure
```
harvester/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smartshaped/chameleon/harvester/
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
