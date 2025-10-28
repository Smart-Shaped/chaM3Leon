# 1. OVERVIEW

The framework is designed to create from scratch and manage end-to-end AI/ML and big data pipelines. It reduces the time and resources needed to go from a pipeline idea to its implementation, cutting down the time — and thus the costs — of moving into production.

## Target Audience

1. State research institutions (mostly universities)
2. Private research institutions
3. Companies
4. Anyone working with large datasets who would like to migrate or create big data processing pipelines with or without ML inferences — especially in the absence of internal developers and/or with non-multimillion-dollar budgets.

---

# 2. KEY BENEFITS

## Core Value Proposition

The framework is generic (technically abstract) enough to be versatile, allowing the implementation of AI/ML or big data pipelines of virtually any nature. At the same time, it is simple enough to implement, significantly reducing time-to-market/time-to-production. The system is based on technologies designed for big data management (described in the [TECHNICAL FEATURES](#3-technical-features) section), making it inherently scalable.

It allows users to focus exclusively on their use case without worrying about the specifics of designing architectural and infrastructural components. The framework's goal is to leave space for ideas, allowing users to focus on the *what* rather than the *how*.

In the future, the framework, combined with a specially designed web platform, will enable the creation of simple pipelines without the need for any consulting, through the use of the Workflow Designer.

## Efficiency and Productivity

Creating pipelines from the framework eliminates the need to design and implement the architecture, saving significant time and costly technical expertise. For example, companies without internal developers would otherwise spend considerable amounts on consulting for components that are already developed and ready to use.

Time and cost savings are further enhanced by the fact that, if the ML or big data pipeline generates value for the client, early implementation creates value sooner than anticipated, also providing a potential competitive edge.

---

# 3. TECHNICAL FEATURES

Inferences in big data often require real-time insights along with in-depth historical analyses. The architecture balances computational efficiency and flexibility, leveraging distributed processing frameworks like Apache Spark for standard Lambda architecture components (Batch Layer and Speed Layer), a custom Harvester for large historical data ingestion, and an ML Runner to manage and execute ML applications.

End-to-end pipelines are orchestrated via **Apache Airflow**, enabling the creation, scheduling, and execution of workflows defined as DAGs (Directed Acyclic Graphs). Airflow represents complex processes as a series of interdependent tasks, making it particularly useful for managing big data or ML pipelines.

In chaM3Leon, Airflow executes manually written DAGs for pipeline flows. In the future, DAGs will be automatically generated through a workflow designer, ensuring a 1:1 correspondence between the visual pipeline and execution in Airflow.

Airflow organizes the execution order, scheduling, and management of each functional layer of the framework (Batch, Speed, Harvester, ML).

The platform’s architecture is a customized Lambda pattern organized into seven main layers:

---

## Batch Layer

A standard Lambda component based on Spark Structured Streaming, dedicated to processing non-real-time data streams from one or more Kafka topics. Processed results are saved to **HDFS**, while partial statistics are stored in **Cassandra** for historical analysis. The batch ingestion interval is configurable.

> **Note:** Kafka message size limit is 1MB (extendable to 10MB). Files larger than this should be ingested through the Harvester.

---

## Speed Layer

Manages real-time data streams and calculates partial statistics to provide quick insights. It relies on Spark Structured Streaming, handling low-latency updates. The layer does not store raw data in HDFS or Cassandra; instead, the **SpeedUpdater** calculates summary statistics stored in Cassandra. The time window for real-time processing is configurable.

> Data is fed via Kafka, with message size considerations identical to the Batch Layer.

---

## Harvesting Layer (Harvester)

Responsible for collecting data from heterogeneous external sources when Batch and Speed Layers are insufficient, such as historical archives or unsupported data formats. It processes and formats data for optimal ML consumption. Supports integration with numerous APIs and data services.

> Prototype implementations include massive file ingestion of formats like GeoJSON and TIF/TIFF, widely used in geoclimatic analysis.

---

## ML Runner

Fully developed in Python, this layer handles AI/ML modeling, reading required data for training and inference. Users define specifications to produce ML models, including training management. The library supports integration with common frameworks like **PyTorch** and **TensorFlow**, thanks to the **MLflow** library. Atomic steps are generated from a configuration file, automatically translated into **Metaflow** instructions with custom decorators.

---

## Serving Layer

Aggregates data from Cassandra, including summary statistics and ML inferences, and exposes them via APIs for easy access to processed results.
