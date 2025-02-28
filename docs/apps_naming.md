# Application Naming Guidelines

Each project will have a `project_id`. The following naming conventions apply to different layers of applications.

## General Naming Conventions

### Maven POM Configuration

Each application's `pom.xml` should follow these conventions:

```xml
<artifactId>{project_id}-{layer_name}</artifactId>
<name>{project_id}-{layer_name}</name>
<description>{layer_name} App for {project_id}</description>
```

### Java Class Naming

- **Main Application Class**: `{base_package}.{project_id}.{layer_name}.{project_id}{layer_name}App`
- **Layer Class**: `{base_package}.{project_id}.{layer_name}.{project_id}{layer_name}Layer`

---

## Batch Application

- **Updater Class**: `{base_package}.{project_id}.batch.{project_id}BatchUpdater`
- **Preprocessor Class**: `{base_package}.{project_id}.batch.{project_id}BatchPreprocessor`
- **Cassandra Model Class**: `{base_package}.{project_id}.batch.model.{table_name}`

---

## Speed Application

- **Updater Class**: `{base_package}.{project_id}.speed.{project_id}SpeedUpdater`
- **Preprocessor Class**: `{base_package}.{project_id}.speed.{project_id}SpeedPreprocessor`
- **Cassandra Model Class**: `{base_package}.{project_id}.speed.model.{table_name}`

---

## Machine Learning (ML) Application

- **HDFS Reader Class**: `{base_package}.{project_id}.ml.{project_id}HdfsReader`
- **Pipeline Class**: `{base_package}.{project_id}.ml.{project_id}Pipeline`
- **Blackbox Class**: `{base_package}.{project_id}.ml.{project_id}Blackbox`
- **Model Saver Class**: `{base_package}.{project_id}.ml.{project_id}ModelSaver`
- **Cassandra Model Class**: `{base_package}.{project_id}.ml.model.{table_name}`

---

## Harvester Application

- **Harvester Class**: `{base_package}.{project_id}.harvester.harvesters.{project_id}Harvester`
- **Downloader Class**: `{base_package}.{project_id}.harvester.downloaders.{project_id}Downloader`
- **Transformer Class**: `{base_package}.{project_id}.harvester.transformers.{project_id}Transformer`
- **Preprocessor Class**: `{base_package}.{project_id}.harvester.preprocessors.{project_id}Preprocessor`

---

By adhering to these naming conventions, consistency across different layers of applications is maintained, ensuring better readability, maintainability, and scalability.
