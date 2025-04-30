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
