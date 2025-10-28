# chaM3Leon Architecture: A Simplified Guide

## Introduction

The chaM3Leon architecture is designed as a **modular system** that manages the entire data lifecycle, from collection to analysis, to distribution. This document explains how the various components work together in a simple and understandable way.

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

### Harvester Layer - The Collector

**What it does:** Collects data from external sources like APIs, web services and databases.

**Analogy:** Like a harvester that goes to the fields to collect fruits from different plants.

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

### Speed Layer - The Real-Time Processor

**What it does:** Processes data in real-time as it arrives.

**Analogy:** Like a cashier in a supermarket who processes purchases as customers arrive at the checkout.

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

### Batch Layer - The Historical Processor

**What it does:** Processes large amounts of historical data in batches.

**Analogy:** Like an archivist who organizes and analyzes historical documents at month's end.

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

### ML Runner - The Analytical Brain

**What it does:** Applies machine learning algorithms to make predictions and discover patterns in data.

**Analogy:** Like an expert who studies data and finds hidden correlations or makes predictions.

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

### Serving Layer - The Distributor

**What it does:** Makes processed data available through REST APIs.

**Analogy:** Like a waiter who brings dishes (data) to tables (client applications).

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

## Lambda Architecture: The Heart of chaM3Leon

chaM3Leon implements a **Lambda Architecture**, which combines two approaches:

### The Fast Path (Speed Layer)
```
Incoming Data → Speed Layer → Real-Time View
```
- **Speed**: Milliseconds
- **Accuracy**: Good
- **Usage**: Recent data

### The Complete Path (Batch Layer)
```
Historical Data → Batch Layer → Batch View
```
- **Speed**: Minutes/Hours
- **Accuracy**: Excellent
- **Usage**: All data

### Unified View
```
Real-Time View + Batch View = Complete View
```

**Advantage:** You get both speed and accuracy!

## How Components Communicate

### Apache Kafka - The Messenger
Transports data between different layers in real-time.
- **Analogy:** A pneumatic mail system in a building

### Apache Cassandra - The Warehouse
Stores processed data for fast access.
- **Analogy:** An organized warehouse with labeled shelves

### HDFS - The Archive
Stores large amounts of raw and historical data.
- **Analogy:** A long-term storage facility for documents

### Apache Spark - The Engine
Processes data in a distributed and parallel manner.
- **Analogy:** A team of workers collaborating on a large project

## Typical Usage Patterns

### Pattern 1: Complete Pipeline
```
External API → Harvester → Kafka → Speed Layer → Cassandra → Serving Layer → Web App
                               ↓
                          Batch Layer → ML Runner → Predictive Models
```

### Pattern 2: Real-Time Analysis
```
IoT Sensors → Kafka → Speed Layer → Cassandra → Live Dashboard
```

### Pattern 3: ML Training and Inference
```
Historical Data → Batch Layer → ML Runner → Trained Models
New Data → Speed Layer → ML Runner (Inference) → Predictions
```

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

### Option 1: Single Node (Development/Test)
All components on a single machine for testing and development.

### Option 2: Cluster (Production)
Components distributed across multiple machines for high availability and performance.

### Option 3: Cloud
Deployment on AWS, Azure, Google Cloud with auto-scaling.

### Option 4: Docker
Containerization for ease of deployment and portability.

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

## Security and Governance

### Access Management
- API authentication
- Role-based access control
- Sensitive data encryption

### Monitoring
- Centralized logging
- Performance metrics
- Anomaly alerting

### Data Quality
- Input data validation
- Cleaning and normalization
- Transformation traceability

## Conclusion

The chaM3Leon architecture is designed to be:
- **Powerful**: Handles complex Big Data and ML
- **Simple**: Clear interfaces and intuitive configurations
- **Flexible**: Adaptable to different use cases
- **Scalable**: Grows with your needs

Each layer has a specific role but all work together harmoniously to transform raw data into business value.

---

**Next Steps:**
- Check [GETTING_STARTED.md](GETTING_STARTED.md) to start using chaM3Leon
- See [USE_CASES.md](USE_CASES.md) for concrete application examples
- Read [VALUE_PROPOSITION.md](../VALUE_PROPOSITION.md) to understand the framework's advantages
