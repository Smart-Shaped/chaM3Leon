# Serving Layer - chaM3Leon Project
## Overview
The Serving module is a component of the chaM3Leon project that provides API endpoints for data access and request management. It serves as the interface layer between clients and the underlying data processing capabilities of the chaM3Leon framework.

## Features
- RESTful API: Provides HTTP endpoints for data retrieval and request submission
- Request Management: System for creating and tracking requests with unique IDs
- Cassandra Integration: Uses Apache Cassandra for data storage and retrieval
- Spring Boot Framework: Built on Spring Boot for robust web service capabilities
- Flexible Data Models: Supports various data models including batch processing and ML models
- Repository Pattern: Implements repository pattern for data access abstraction
## Architecture
The module consists of several key components:

### Controllers
- RequestController: Handles HTTP requests for creating and retrieving requests
- BatchController: Manages batch processing operations
- SpeedController: Handles real-time data processing requests
### Models
- Request: Data model for client requests with state tracking
- BatchModel: Abstract base class for batch processing models
- SpeedModel: Base class for real-time data models
- MLModel: Base class for machine learning models
### Repositories
- RequestRepository: Data access for request entities
- BatchRepository: Generic repository for batch processing data
- SpeedRepository: Repository for real-time data access
- MLRepository: Repository for machine learning model data
## Configuration
The module uses application.properties to define:

- Spring application name
- Cassandra connection settings
- Keyspace configuration
- Schema creation policies
### Key Configuration Parameters
- Cassandra keyspace name
- Contact points for database connection
- Port configuration
- Local datacenter settings
- Schema action policies
## Dependencies
- Spring Boot: Core framework for web application development
- Spring Modulith: For modular application architecture
- Spring Data Cassandra: For Cassandra database integration
- Project Lombok: For reducing boilerplate code
- Spring Boot Test: For testing framework
## Usage
To use the Serving Layer API, you can make HTTP requests to the following endpoints:

### Request Endpoints
- POST /api/requests: Create a new request
- GET /api/requests: Retrieve all requests
- GET /api/requests/{id}: Retrieve a specific request by ID
### Data Models
When creating requests, use the following JSON structure:

```json
{
  "content": "Request content",
  "harvesterIds": "comma,separated,harvester,ids",
  "state": "inProgress"
}
```
Note that these endpoints are already configured and ready to use in the framework. No additional logic implementation is needed to use these basic functionalities.

## Usage
To develop an application using the Serving Layer, follow these steps:

## Usage
### 0. Prerequisites
- Execute mvn clean install of the chaM3Leon framework
- Create a new Maven project
- Add the following dependencie to the POM to include the framework serving layer:
```xml
        <dependency>
            <groupId>com.smartshaped.chameleon</groupId>
            <artifactId>serving</artifactId>
            <version>1.0.0</version>
            <scope>compile</scope>
        </dependency>
```

### 1. Create a Class that extends `com.smartshaped.chameleon.serving.model.**Model`
- Ensure the class is annotated with @Table.
- Define table fields as class attributes.
- Specify the primary key name.
### 2. Create a Repository Interface that extends CassandraRepository with the model type and primary key type
- Define custom data access methods if needed.
- Make sure to specify the model type and primary key type as generic parameters.
### 3. Create a Controller that extends `com.smartshaped.chameleon.serving.model.**Controller` and esposes API endpoints
- Annotate the class with @RestController and @RequestMapping.
- Inject the repository created in the previous step.
- Implement methods to handle HTTP requests (GET, POST, PUT ...).
### 4. Configure the application in the application.properties file
- Specify Cassandra connection settings.
- Configure the keyspace name.
- Set schema creation policies.
### 5. Create a Class containing the main method
- Annotate the class with @SpringBootApplication.
- Call the SpringApplication.run() method inside the main method.
- Specify this class in the application startup command.

## Error Handling
The module includes standard Spring Boot error handling:

- HTTP status codes for different error conditions
- JSON error responses with descriptive messages
- Exception handling for database connectivity issues

## Project Structure
serving_chaM3Leon/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smartshaped/chameleon/serving/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── ServingChaM3LeonApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/smartshaped/chameleon/serving/
└── pom.xml

## Contributing
When contributing to this module, please ensure:

- Follow the existing code style
- Add appropriate unit tests
- Update API documentation
- Maintain backward compatibility
## License
This project is licensed under the terms included in the root project directory.

For more information about the chaM3Leon project, please refer to the main project documentation.