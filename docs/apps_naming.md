# Apps Naming guidelines

Every project will have a project_id.

## Common

Apps pom details:

```xml
<artifactId>{project_id}-{layer_name}</artifactId>
<name>{project_id}-{layer_name}</name>
<description>{layer_name} App for {project_id}</description>
```

Main classes: {base_package}.{project_id}.{layer_name}.{project_id}App

Layer classes: {base_package}.{project_id}.{layer_name}.{project_id}Layer

## Batch App

Updater class: {base_package}.{project_id}.batch.{project_id}BatchUpdater

Preprocessor class: {base_package}.{project_id}.batch.{project_id}BatchPreprocessor

Cassandra model class: {base_package}.{project_id}.batch.model.{table_name}

## Speed App

Updater class: {base_package}.{project_id}.speed.{project_id}SpeedUpdater

Preprocessor class: {base_package}.{project_id}.speed.{project_id}SpeedPreprocessor

Cassandra model class: {base_package}.{project_id}.speed.model.{table_name}

## ML App

HDFS reader class: {base_package}.{project_id}.ml.{project_id}HdfsReader

Pipeline class: {base_package}.{project_id}.ml.{project_id}Pipeline

Blackbox class: {base_package}.{project_id}.ml.{project_id}Blackbox

Model saver class: {base_package}.{project_id}.ml.{project_id}ModelSaver

Cassandra model class: {base_package}.{project_id}.ml.model.{table_name}

## Harvester App

Harvester classes: {base_package}.{project_id}.harvester.harvesters.{project_id}Harvester

Downloader classes: {base_package}.{project_id}.harvester.downloaders.{project_id}Downloader

Transformer classes: {base_package}.{project_id}.harvester.transformers.{project_id}Transformer

Preprocessor classes: {base_package}.{project_id}.harvester.preprocessors.{project_id}Preprocessor
