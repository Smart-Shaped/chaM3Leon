# chaM3Leon Architecture: A Simplified Guide

## Introduction

The chaM3Leon architecture is designed as a **modular system** that manages the entire data lifecycle, from collection to analysis, to distribution. This document, starting from the architecture diagram, explains how the various components work together in a simple and understandable way.

![chaM3Leon architecture](./CM3Lv2.png)

## Overview

Imagine chaM3Leon as a **data factory** composed of different workstations, each specialized in a specific task:

```
Incoming Data → Collection → Processing → Analysis → Distribution → Applications
```

### Data Flow

1. **Collection**: Data arrives from various sources (APIs, sensors, databases)
2. **Processing**: Data is cleaned, transformed and organized
3. **Analysis**: Machine learning models and analysis are applied
4. **Distribution**: Results are made available via APIs
5. **Usage**: Applications consume the processed data

## Main Components (Layers)

### Batch Layer

**What it does:** Processes large amounts of historical data in batches.

**Features:**
- Processing of large data volumes
- Deep and complex analysis
- Greater accuracy compared to streaming
- Optimized for efficiency on large datasets

**When to use it:**
- Historical analysis and long-term trends
- Periodic reports (daily, weekly, monthly)
- Processing that requires the entire dataset
- Training machine learning models on historical data

---

### Speed Layer

**What it does:** Processes data in real-time as it arrives.

**Features:**
- Immediate streaming data processing
- Low latency (fast responses)
- Perfect for situations requiring immediate responses
- Integrated with Apache Kafka to handle continuous data streams

**When to use it:**
- Real-time monitoring (live dashboards)
- Immediate anomaly detection
- Counters and statistics that update continuously
- Alert systems

---

### Harvester Layer

**What it does:** Collects data from external sources like APIs, web services and databases.

**Features:**
- Downloads data from multiple sources
- Supports different formats (JSON, XML, binary files)
- Handles complex requests with customizable parameters
- Request management system with states (in progress, completed, errors)

**When to use it:**
- You need to collect data from external APIs
- You need to download large amounts of data from the internet
- You want to automate information collection from different sources

---

### ML Runner

**What it does:** Applies machine learning algorithms to make predictions and discover patterns in data.

**Features:**
- Integration with MLflow for model management
- Support for Metaflow for ML pipelines
- Model training and deployment
- ML model lifecycle management

**When to use it:**
- Predictions (sales, demand, behaviors)
- Classification (spam, sentiment analysis)
- Recommendations
- Complex anomaly detection

---

### Serving Layer

**What it does:** Makes processed data available through REST APIs.

**Features:**
- Modern and standard REST APIs
- Fast data access via Cassandra
- Configurable endpoints
- Spring Boot integration

**When to use it:**
- You need to expose data to web or mobile applications
- You want to create interactive dashboards
- You need programmatic access to processing results

---

## **Lambda Architecture: The Heart of chaM3Leon (with MLOps Extensions)**

chaM3Leon implements a custom **Lambda Architecture**, which combines two parallel data processing paths—the Fast Path for immediate results and the Complete Path for comprehensive accuracy—while adding specialized layers for **ML Model Training** and **Serving**.

### 1. The Fast Path (Speed Layer)
This path focuses on low-latency processing to provide a near real-time view of the incoming data.

$$\text{Incoming Data} \to \text{Speed Layer (Spark Streaming)} \to \text{Real-Time View (Cassandra)}$$

| Metric | Detail |
| :--- | :--- |
| **Speed** | Milliseconds (low-latency) |
| **Accuracy** | Good (results are incremental and approximate) |
| **Usage** | Recent data analysis |

### 2. The Complete Path (Batch Layer)
This path provides high-accuracy processing of all data. It creates the definitive, static master dataset that is used for historical reporting and robust ML training.

$$\text{Historical Data} \to \text{Batch Layer (Spark)} \to \text{Master Dataset (HDFS)}$$

| Metric | Detail |
| :--- | :--- |
| **Speed** | Minutes/Hours (high-latency) |
| **Accuracy** | Excellent (full data aggregation) |
| **Usage** | All data (historical persistence) |

### 3. MLOps Extension: Data Harvesting and Model Training

Our architecture extends the classic Lambda pattern by introducing two specialized components essential for MLOps:

* **Harvester Layer (Spark):** This component is responsible for retrieving and preparing the comprehensive **Master Dataset** stored in **HDFS**. It ensures the ML Runner always trains on the highest quality, most complete static data available.
* **ML Runner (MLflow / Spark):** This layer consumes the data prepared by the Harvester and/or the Batch to train, manage, and deploy the machine learning models. The resulting models are then served to the end-user, often integrating with real-time analysis.

### 4. Unified View and Serving

The final results are merged and served to the client application:

$$\text{Real-Time View} + \text{Batch View (Historical Analysis)} \xrightarrow{\text{Serving Layer}} \text{Complete Analysis API}$$

$$\text{Trained Model} \xrightarrow{\text{ML Runner}} \text{ML Model Serving API}$$

**Advantage:** You get both **speed** and **accuracy**, plus the dedicated capability to train and serve ML models on the most **comprehensive historical data**.

## How Components Communicate

### Apache Kafka
Transports data between different layers in real-time.

### Apache Cassandra
Stores processed data for fast access.

### HDFS
Stores large amounts of raw and historical data.

### Apache Spark
Processes data in a distributed and parallel manner.

## Typical Usage Patterns

These two patterns illustrate the primary end-to-end flows, showing how data is ingested and processed to generate both analysis and predictive model results, incorporating the custom use of the **Harvester Layer** in the second pattern.

### Pattern 1: Core Lambda Flow (Analysis and Training from Batch)

This pattern represents the standard continuous data flow where all layers of the Lambda architecture are active. The **Batch Layer** saves the **Master Dataset** to HDFS, which is then used by the **ML Runner** for training.

$$\text{Producer} \to \text{Kafka} \xrightarrow{\text{Parallel Consumption}} \begin{cases} \text{Batch Layer} \to \text{HDFS (Master Dataset)} \\ \text{Speed Layer} \to \text{Cassandra} \end{cases}$$

$$\dots \to \text{Cassandra (Unified Analysis)} \to \text{Serving Layer} \to \text{Serve Analysis}$$

$$\text{HDFS} \to \text{ML Runner (for Training)} \to \text{Serve ML Model (Inference)}$$

### Pattern 2: Static Data Ingestion and ML Training (Custom Harvester Use)

This pattern showcases a process where **static data** is initially ingested by the **Harvester** and directly saved to HDFS, bypassing Kafka, specifically for ML training purposes. This is the **only pattern** where the Harvester is used as an ingestion tool.

$$\text{External Static Data} \to \text{Harvester Layer} \to \text{Hadoop HDFS (Static Dataset)}$$

$$\text{Hadoop HDFS} \to \text{ML Runner (for Training)} \to \text{Serve ML Model}$$

$$\text{ML Runner} \to \text{Serve ML Model (Inference)}$$

### Final considerations

Of course, since chaM3Leon is modular, you can choose and combine layers based on specific needs, but these two patterns represent the most common use cases.

A critical scenario not explicitly detailed above is the case where you need to integrate real-time analysis (from the Speed Layer) and the static, historical data (from Harvester and Batch) to train an ML model. This scenario would involve utilizing all layers of the architecture to achieve the most comprehensive training dataset and deploy a highly accurate, context-aware model.

## Design Principles

### Modularity
Each layer is independent and can be used individually or in combination.

### Scalability
Add more resources when load increases without modifying code.

### Flexibility
Customize each component for your specific needs.

### Reliability
Checkpointing system and automatic recovery from errors.

### Transparency
Open source code, declarative configurations, complete logging.

## Deployment

### Option 1: Docker
Containerization for ease of deployment and portability.

### Option 2: Cluster (Production)
Components distributed across multiple machines for high availability and performance.

### Option 3: Cloud
Deployment on AWS, Azure, Google Cloud with auto-scaling.

## Practical Example: E-Commerce Monitoring System

Let's see how the layers work together in a real case:

1. **Harvester**: Collects data from third-party APIs (competitor prices, reviews)

2. **Speed Layer**: 
   - Processes orders in real-time
   - Updates live sales counters
   - Detects immediate fraud

3. **Batch Layer**:
   - Analyzes weekly sales trends
   - Calculates aggregate statistics
   - Prepares data for ML training

4. **ML Runner**:
   - Trains recommendation models
   - Predicts future demand
   - Classifies customers by value

5. **Serving Layer**:
   - Provides recommendations to the web app
   - Exposes metrics for dashboards
   - APIs for mobile app

## Conclusion

The chaM3Leon architecture is built to provide the right balance between power and usability:

- **Powerful** — Designed for advanced Big Data processing and end-to-end ML pipelines
- **Simple** — Clear interfaces and configuration-driven behaviors minimize development effort
- **Flexible** — Adapts to virtually any data domain or pipeline strategy
- **Scalable** — Grows seamlessly from prototypes to full enterprise workloads

Each layer has a well-defined responsibility, but together they form a cohesive ecosystem that efficiently transforms raw data into actionable business value.

For a deeper view of the internal components — including their provided and required interfaces — check out the *[UML Component Diagram](./chaM3LeonCDv2.png)*


---

**Next Steps:**
- Check [GETTING_STARTED.md](GETTING_STARTED.md) to start using chaM3Leon
- See [USE_CASES.md](USE_CASES.md) for concrete application examples
- Read [VALUE_PROPOSITION.md](../VALUE_PROPOSITION.md) to understand the framework's advantages
