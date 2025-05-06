# Common Module - chaM3Leon

## Usage
The common module is used as a dependency by other modules of the chaM3Leon framework. To include it in your project, add the following Maven dependency.

## Features
The common module provides several utilities and base classes for the chaM3Leon framework:

### Configuration Management
- `ConfigurationUtils`: Base class for configuration management
  - Loads and processes YAML configuration files
  - Supports configuration file inclusion
  - Provides methods to retrieve Spark configuration
  - Handles Cassandra connection settings

### Database Interaction
- `TableModel`: Base class for Cassandra database interaction
  - Validates model structure
  - Generates Cassandra table creation queries
  - Handles primary key management
  - Supports type mapping between Java and Cassandra

### Kafka Integration
- `KafkaConsumer`: Utility for Kafka stream consumption
  - Creates Spark streaming datasets from Kafka topics
  - Handles connection configuration

### Exception Handling
- `ConfigurationException`: For configuration-related errors
- `PreprocessorException`: For preprocessing-related errors
- `CassandraException`: For database-related errors

## Configuration
The module uses YAML configuration files:
- `framework-config.yml`: Main configuration file
- Environment-specific configuration files (e.g., `test-config.yml`)
- `typeMapping.yml`: Maps Java types to Cassandra types
