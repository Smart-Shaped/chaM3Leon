# chaM3Leon

A modular and scalable framework designed to support machine learning applications - emphasising transparency, interoperability, and usability. It implements a custom lambda architecture, and additional components designed to tackle the limitation of the Speed-Batch coupling for data ingestion and processing.

The chaM3Leon architecture is illustrated in the following Component Diagram, highlighting the connections between layers through provided and required interfaces.

![chaM3Leon architecture](docs/chaM3LeonCD.png)

As of now, we have released three layers (Batch Layer, Speed Layer, and ML Layer). You can refer to our [roadmap](#roadmap) to see the planned release dates for other components.

To implement your own version of any abstract layer you have to:

- Build the project running at the level of the chaM3Leon pom.xml the following command:
```bash
mvn clean install
```
- Generate a Maven project and add chaM3Leon as dependency on your maven pom.xml as below: 

```bash
<dependency>
	<groupId>com.smartshaped</groupId>
	<artifactId>chameleon</artifactId>
	<version>0.0.1</version>
</dependency>
```
- Add the maven-shade-plugin to generate a shaded jar in order to submit your layer implementation as a Spark application (keep in mind the framework is based on Java 11)

```bash
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
									implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
									<resource>
										META-INF/services/org.apache.spark.sql.sources.DataSourceRegister
									</resource>
							</transformer>
						</transformers>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

After this, you can choose to extend any of the following layers:

- [Batch Layer](#batch-layer-documentation)
- [Speed Layer](#speed-layer-documentation)
- [ML Layer](#ml-layer-documentation)

# Batch Layer Documentation

## How to Develop a Batch Application

To develop a batch application using the Batch Layer, follow these steps:

### 1. Create a Class that Extends `com.smartshaped.chameleon.batch.BatchLayer`
- Ensure that the class constructor is **public**.

### 2. Create one or more Classes that Extend `com.smartshaped.chameleon.preprocessing.Preprocessor`
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
- Call the `start` method of `BatchLayer` inside the `main` method.
- Specify this class in the `spark-submit` command.

---

# Speed Layer Documentation

## How to Develop a Speed Application

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

# ML Layer Documentation

## How to Develop an ML Application

To develop a machine learning application using the ML Layer, follow these steps:

### 1. Create a Class that Extends `com.smartshaped.chameleon.ml.MLLayer`
- Ensure that the class constructor is **public**.

### 2. Create at Least One Class that Extends `com.smartshaped.chameleon.ml.HdfsReader`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file along with the HDFS path from which the data will be read.
- Optionally, override the `processRawData` method to add custom processing for the raw data.

### 3. Create a Class that Extends `com.smartshaped.chameleon.ml.Pipeline`
- Declare this class in the YAML file.
- Override the `start` method to implement the specific machine learning logic. 
  - Ensure that the `setModel` and `setPredictions` methods are called at the end of the pipeline.

### 4. Create a Class that Extends `com.smartshaped.chameleon.ml.ModelSaver`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file.

### 5. Create a Class that Extends `com.smartshaped.chameleon.common.utils.TableModel`
- Define the table fields as class attributes.
- Specify the name of the primary key as a **string**.
- Create a `typeMapping.yml` file to define the mapping between Java field types and CQL (Cassandra Query Language) types.
- Declare this class in the YAML file.

### 6. Create a Class Containing the `main` Method
- Call the `start` method of `MLLayer` inside the `main` method.
- Specify this class in the `spark-submit` command.

---

# Execution Instructions

To generate the `.jar` file, run the following command from your project directory:

```bash
mvn clean install
```

Then go to our [Docker repository](https://github.com/Smart-Shaped/docker_chaM3Leon) and follow the [Docker documentation](https://github.com/Smart-Shaped/docker_chaM3Leon/blob/public/README.md)

---

# Roadmap

- Harvester (JAN 2025)

- API Gateway (MAR 2025)

- Serving Layer (Q2 2025)

- Workflow Designer (To be determined, probably Q3 2025)
