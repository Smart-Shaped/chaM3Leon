# Getting Started with chaM3Leon

## Welcome!

This guide will help you take your first steps with chaM3Leon, from initial setup to creating your first data processing application. Don't worry if you're not an expert: we'll guide you step by step.

## Prerequisites

Before starting, make sure you have installed:

### Required Software

- **Java 11** (for Spark layers) or **Java 21** (for Serving Layer)
  - Verify: `java -version`
  - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)

- **Maven 3.6+** (for dependency management)
  - Verify: `mvn -version`
  - Download: [Apache Maven](https://maven.apache.org/download.cgi)

- **Apache Spark 3.5+** (for distributed processing)
  - Download: [Apache Spark](https://spark.apache.org/downloads.html)

- **Docker** (optional but recommended)
  - Download: [Docker Desktop](https://www.docker.com/products/docker-desktop)

### For Python Layer (ML Runner)

- **Python 3.9+ (specifically, `>=3.9, <3.13`)**
  - Verify: `python --version`
  - Download: [Python.org](https://www.python.org/downloads/)

- **pip** (Python package manager)
  - Verify: `pip --version`

## Phase 1: Installing chaM3Leon

### Option A: Repository Clone

```bash
```bash
# Clone the main repository
git clone https://github.com/Smart-Shaped/chaM3Leon.git
cd chaM3Leon

# Initialize the Python submodule (ML Runner)
git submodule init
git submodule update
```

### Option B: ZIP Download

1. Go to [GitHub - chaM3Leon](https://github.com/Smart-Shaped/chaM3Leon)
2. Click on "Code" → "Download ZIP"
3. Extract the ZIP file to a folder of your choice

### Framework Compilation

```bash
# Navigate to the chaM3Leon folder
cd chaM3Leon

# Compile the framework
mvn clean install
```

This command:
- Downloads all necessary dependencies
- Compiles the source code
- Runs the tests
- Creates usable JAR files

**Note:** The first compilation may take a few minutes because Maven downloads all dependencies.

## Phase 2: Environment Setup

### Recommended Option: Using Docker

The easiest way to start is using Docker, which automatically configures all necessary services.

```bash
# Clone the Docker repository
git clone https://github.com/Smart-Shaped/docker_chaM3Leon.git
cd docker_chaM3Leon

# Start all services
docker-compose up -d
```

This starts:
- **Apache Kafka**: For data streaming
- **Apache Cassandra**: For persistence
- **HDFS**: For distributed storage
- **Hadoop Yarn**: For distributed execution
- **Spark Cluster**: For processing

## Phase 3: Your First Project

Let's create a simple project that uses the **Batch Layer** to process data.

### Project Structure

```bash
my-chameleon-app-batch/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mycompany/
│       │           └── demo/
│       │               └── batch/
│       │                   ├── DemoApp.java
│       │                   ├── DemoBatchLayer.java
│       │                   ├── DemoPreprocessor.java
│       │                   ├── DemoBatchUpdater.java
│       │                   └── model/
│       │                       └── MyDataModel.java
│       └── resources/
│           ├── framework-config.yml
│           ├── local-config.yml
│           └── typeMapping.yml
```

### 1. Create the `pom.xml` file

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mycompany</groupId>
    <artifactId>demo-batch</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- chaM3Leon Batch Layer -->
        <dependency>
            <groupId>com.smartshaped.chameleon</groupId>
            <artifactId>batch</artifactId>
            <version>2.0.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                            <transformers>
                            <transformer                                                    
                                implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <manifestEntries>
                                    <Specification-Title> Java Advanced Imaging Image I/O Tools</Specification-Title>
                                    <Specification-Version>1.1</Specification-Version>          
                                    <Specification-Vendor> Sun Microsystems, Inc. </Specification-Vendor>
                                    <Implementation-Title> com.sun.media.imageio</Implementation-Title>
                                    <Implementation-Version> 1.1</Implementation-Version>       
                                    <Implementation-Vendor> Sun Microsystems, Inc.</Implementation-Vendor>
                                    <Multi-Release>true</Multi-Release>
                                </manifestEntries>                                            
                            </transformer>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. Create the Data Model

File: `src/main/java/com/mycompany/demo/batch/model/MyDataModel.java`

```java
package com.mycompany.demo.batch.model;

import com.smartshaped.chameleon.common.utils.TableModel;
import lombok.Data;

@Data
public class MyDataModel extends TableModel {
    private String id;           // Primary key
    private String message;
    private Long timestamp;
    private Double value;
    
    @Override
    public String getPrimaryKeyName() {
        return "id";
    }
}
```

### 3. Create the Preprocessor

File: `src/main/java/com/mycompany/demo/batch/DemoPreprocessor.java`

```java
package com.mycompany.demo.batch;

import com.smartshaped.chameleon.common.preprocessing.Preprocessor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DemoPreprocessor extends Preprocessor {
    
    @Override
    public Dataset<Row> preprocess(Dataset<Row> inputData) {
        // Example: filter only records with value > 0
        return inputData.filter("value > 0");
    }
}
```

### 4. Create the Batch Updater (Optional)

File: `src/main/java/com/mycompany/demo/batch/DemoBatchUpdater.java`

```java
package com.mycompany.demo.batch;

import com.smartshaped.chameleon.batch.BatchUpdater;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DemoBatchUpdater extends BatchUpdater {
    
    public DemoBatchUpdater() {
        super();
    }
    
    @Override
    public Dataset<Row> updateBatch(Dataset<Row> batchData) {
        // Example: calculate aggregate statistics
        return batchData.groupBy("message")
                       .count();
    }
}
```

### 5. Create the Batch Layer

File: `src/main/java/com/mycompany/demo/batch/DemoBatchLayer.java`

```java
package com.mycompany.demo.batch;

import com.smartshaped.chameleon.batch.BatchLayer;

public class DemoBatchLayer extends BatchLayer {
    
    public DemoBatchLayer() {
        super();
    }
}
```

### 6. Create the Main Class

File: `src/main/java/com/mycompany/demo/batch/DemoApp.java`

```java
package com.mycompany.demo.batch;

public class DemoApp {
    
    public static void main(String[] args) {
        DemoBatchLayer batchLayer = new DemoBatchLayer();
        batchLayer.start();
    }
}
```

### 7. Configure the Framework

File: `src/main/resources/local-config.yml`

```yaml
batch:
  spark:
    app-name: "DemoBatchApp"
    master: "local[*]"
    interval-sec: 60
  
  kafka:
    server: "localhost:9092"
    intervalMs: 60000
    topics:
      demo-topic:
        name: "demo-topic"
        class: "com.mycompany.demo.batch.DemoPreprocessor"
        path: "/data/demo"
        checkpoint: "/checkpoints/demo"
  
  cassandra:
    model.class: "com.mycompany.demo.batch.model.MyDataModel"
    datacenter: "datacenter1"
    keyspace:
      name: "demo_keyspace"
      replication_factor: 1
    checkpoint: "/checkpoints/cassandra"
  
  updater:
    class: "com.mycompany.demo.batch.DemoBatchUpdater"
```

File: `src/main/resources/typeMapping.yml`

```yaml
java.lang.String: "text"
java.lang.Long: "bigint"
java.lang.Double: "double"
java.lang.Integer: "int"
java.lang.Boolean: "boolean"
```

### 8. Compile the Project

```bash
mvn clean package
```

This creates a JAR file in `target/demo-batch-1.0.0.jar`

### 9. Run the Application

#### With YARN Cluster Mode (with our [Docker setup](https://github.com/Smart-Shaped/docker_chaM3Leon))

```bash
spark-submit \
  --class com.mycompany.demo.batch.DemoApp \
  --master yarn --deploy-mode cluster \
  ./extra_jars/demo-batch-1.0.0.jar
```

#### With YARN Client Mode (with our [Docker setup](https://github.com/Smart-Shaped/docker_chaM3Leon))

```bash
spark-submit \
  --class com.mycompany.demo.batch.DemoApp \ 
  --master yarn --deploy-mode client \
  ./extra_jars/demo-batch-1.0.0.jar
```

#### With Local Spark

```bash
spark-submit \
  --class com.mycompany.demo.batch.DemoApp \
  --master local[*] \
  target/demo-batch-1.0.0.jar
```

#### With Spark Cluster

```bash
spark-submit \
  --class com.mycompany.demo.batch.DemoApp \
  --master spark://your-spark-master:7077 \
  --deploy-mode cluster \
  target/demo-batch-1.0.0.jar
```

## Phase 4: Verify Functionality

### Send Test Data to Kafka

```bash
### Send Test Data to Kafka

```bash
# Create the topic
kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --topic demo-topic \
  --partitions 1 \
  --replication-factor 1

# Send test messages
kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic demo-topic

# Insert some JSON (press Enter after each one)
{"id":"1","message":"test","timestamp":1234567890,"value":100.5}
{"id":"2","message":"test","timestamp":1234567891,"value":200.3}
{"id":"3","message":"demo","timestamp":1234567892,"value":150.7}
```

### Verify Data in Cassandra

```bash
# Connect to Cassandra
cqlsh localhost

# Use the keyspace
USE demo_keyspace;

# Display the data
SELECT * FROM mydatamodel;
```

## Common Troubleshooting

### Problem: "Cannot find symbol" during compilation

**Solution:** Make sure you've run `mvn clean install` in the main chaM3Leon folder before compiling your project.

### Problem: Kafka won't connect

**Solution:** 
- Verify that Kafka is running: `jps` (you should see `Kafka`)
- Check that the address in the configuration file is correct
- If using Docker, make sure containers are active: `docker ps`

### Problem: Cassandra connection refused

**Solution:**
- Verify that Cassandra is running
- Check the ports (default: 9042)
- Verify the datacenter in the configuration

### Problem: Out of Memory during Spark execution

**Solution:**
Increase the available memory for Spark by adjusting the driver and executor memory parameters.

## Next Steps

Congratulations! You've created your first chaM3Leon application.

### What to Do Next

1.  **Explore Other Layers**
    -   Try the [Speed Layer](../chaM3Leon/speed/README.md) for real-time processing
    -   Experiment with the [Harvester Layer](../chaM3Leon/harvester/README.md) to collect data from APIs
    -   Use the [ML Runner](https://github.com/Smart-Shaped/PyChaM3Leon) for machine learning

2.  **Deep Dive into Configurations**
    -   Read the [Configuration Guide](CONFIG_LIST.md)
    -   Learn the best practices for naming: [Apps Naming](APPS_NAMING.md)

3.  **Study Real Use Cases**
    -   Consult [USE_CASES.md](USE_CASES.md) for practical examples

4.  **Explore the Architecture**
    -   Better understand how it works: [ARCHITECTURE.md](ARCHITECTURE.md)

## Useful Resources

### Documentation
- [Main README](../README.md)
- [Value Proposition](../VALUE_PROPOSITION.md)
- [Batch Layer Documentation](../chaM3Leon/batch/README.md)
- [Speed Layer Documentation](../chaM3Leon/speed/README.md)
- [Harvester Documentation](../chaM3Leon/harvester/README.md)

### Video Tutorials
- [Framework Presentation](https://www.youtube.com/watch?v=wtVyYUDlRQc) (Work in progress)
- [Batch and Speed Demo](https://www.youtube.com/watch?v=UjzYc9C1krU) (Work in progress)

### Repositories
- [chaM3Leon Main](https://github.com/Smart-Shaped/chaM3Leon)
- [PyChaM3Leon (ML Runner)](https://github.com/Smart-Shaped/PyChaM3Leon)
- [Docker Setup](https://github.com/Smart-Shaped/docker_chaM3Leon)

## Support

If you have questions or problems:
1. Check this documentation
2. Consult the video tutorials
3. Open an [issue](https://github.com/Smart-Shaped/chaM3Leon/issues) on GitHub
4. Contact the community

---

**Happy coding with chaM3Leon!**
