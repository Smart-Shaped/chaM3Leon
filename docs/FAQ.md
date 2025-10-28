# Frequently Asked Questions (FAQ)

## Table of Contents
- [General](#general)
- [Installation and Setup](#installation-and-setup)
- [Architecture and Design](#architecture-and-design)
- [Development](#development)
- [Deployment and Operations](#deployment-and-operations)
- [Performance and Scalability](#performance-and-scalability)
- [Integration](#integration)
- [Licensing and Support](#licensing-and-support)

---

## General

### What is chaM3Leon?
chaM3Leon is a modular open-source framework for Big Data and Machine Learning that simplifies managing the entire data lifecycle, from collection to analysis, to distribution.

### Who is chaM3Leon designed for?
- **Data Engineers**: To build robust data pipelines
- **Data Scientists**: To implement ML models in production
- **Companies**: For Big Data projects without huge investments
- **Developers**: To learn Big Data/ML best practices

### What problems does it solve?
- Managing large data volumes
- Real-time and batch processing
- Machine learning integration
- Automatic scalability
- Reducing time-to-market for data projects

### Is it free?
Yes, chaM3Leon is released under Apache 2.0 license, which allows free commercial use.

### Who maintains the project?
The project is maintained by Smart-Shaped and the open source community.

---

## Installation and Setup

### What are the prerequisites?
**For Java layers:**
- Java 11 (Spark layers) or Java 21 (Serving Layer)
- Maven 3.6+
- Apache Spark 3.5+

**For ML Runner (Python):**
- Python 3.9+ (specifically, `>=3.9, <3.13`)
- pip

### Do I need to install all components?
No, chaM3Leon is modular. Install and use only the layers you need.

### Can I use Docker?
Yes! In fact, it's the recommended way. We have a dedicated repository: [docker_chaM3Leon](https://github.com/Smart-Shaped/docker_chaM3Leon)

### How long does setup take?
- **With Docker**: 30 minutes - 1 hour
- **Manual setup**: 2-4 hours
- **First working project**: 1 day

### Does it work on Windows?
Yes, but we recommend Linux or macOS for production. On Windows is reccomended to use Docker.

### Can I use it on cloud?
Yes, it works on AWS, Azure, Google Cloud and any provider that supports JVM and Docker.

---

## Architecture and Design

### What is Lambda Architecture?
An architectural pattern that combines:
- **Batch Layer**: Accurate processing of historical data
- **Speed Layer**: Fast processing of real-time data
- **Serving Layer**: Unifies and distributes results

Advantage: You get both speed and accuracy!

### Do I need to use all layers?
No! Use only those necessary:
- Only batch processing → Batch Layer
- Only real-time → Speed Layer
- Only data collection → Harvester Layer
- Only ML → ML Runner
- Need APIs? → Serving Layer

### Do layers communicate with each other?
Yes, through:
- **Kafka**: For data streaming
- **Cassandra**: For shared storage
- **HDFS**: For historical data

### Can I extend the layers?
Yes! Each layer is designed to be extended. You create classes that inherit from the framework's base classes.

### Can I use databases other than Cassandra?
The framework is optimized for Cassandra, but you can extend it to support other DBs. For the Serving Layer (Spring Boot) it's easier to integrate other databases.

---

## Development

### Which languages do I need to know?
Depends on the layers you use:
- **Java**: For Batch, Speed, Harvester, Serving
- **Python**: Only for ML Runner
- **YAML**: For configurations (easy!)

### Do I need Spark experience?
Not necessarily. The framework abstracts many Spark complexities. However, basic knowledge helps.

### How do I debug my application?
- **Local**: Use your IDE (IntelliJ, Eclipse, VS Code)
- **Logs**: Configurable via Log4j
- **Spark UI**: Available at `http://localhost:4040` during execution
- **Cassandra**: Use `cqlsh` to verify data

### Can I test without a Spark cluster?
Yes! You can use `local[*]` mode to test on a single machine. However, it's always better to use our [Docker environment](https://github.com/Smart-Shaped/docker_chaM3Leon) in order to get all the necessary technologies.

### How do I manage configurations?

Configuration is handled through YAML files:

- **`framework-config.yml`** — Defines available environment profiles (e.g. `dev`, `test`, `prod`, `local`) and determines which configuration should be loaded at runtime.
- **`{env}-config.yml`** — Contains environment-specific settings that override the defaults based on the active environment.
- **`typeMapping.yml`** — Specifies data type mappings used by the framework.

See [CONFIG_LIST.md](docs/CONFIG_LIST.md) for details.

### Where do I put my custom code?
You create classes that extend those from the framework:
- `BatchLayer` → Your `MyBatchLayer`
- `Preprocessor` → Your `MyPreprocessor`
- `BatchUpdater` → Your `MyBatchUpdater`
- etc.

### How do I add new dependencies?
In your `pom.xml` (for Java) or `requirements.txt` (for Python).

---

## Deployment and Operations

### How do I deploy to production?
1. Compile: `mvn clean package`
2. Generate shaded JAR
3. Submit to Spark:
```bash
spark-submit --class com.myapp.Main \
  --master spark://master:7077 \
  target/myapp.jar
```

Or use [Docker](https://github.com/Smart-Shaped/docker_chaM3Leon) for containerization.

### Does it support Kubernetes?
Yes, you can deploy on Kubernetes using Spark Operator or generic containers.

### How do I monitor applications?

You can monitor applications using:

- **Spark UI** — View real-time Spark job metrics and execution details.
- **Prometheus / Loki / Grafana Stack** — A full observability stack is provided in our [Docker repository](https://github.com/Smart-Shaped/docker_chaM3Leon) to monitor all applications.


### How do I handle errors?
The framework includes:
- Custom exception handling for each layer
- Checkpointing for recovery
- Configurable retry logic
- Dead letter queues for Kafka

### Does it support high availability?
Yes, all components support HA:
- **Spark**: Cluster mode with failover
- **Kafka**: Replication
- **Cassandra**: Distributed by design
- **HDFS**: Configurable replication factor

---

## Performance and Scalability

### How well does it scale?
- **Data**: From GB to PB
- **Throughput**: Millions of events/second
- **Latency**: Milliseconds (Speed Layer)

Depends on the infrastructure you allocate.

### How do I optimize performance?
- **Spark**: Tuning partitions, memory, parallelism
- **Kafka**: Partitioning topics, consumer groups
- **Cassandra**: Partition key design, replication factor
- **Code**: Avoid unnecessary shuffles, use broadcast variables

Check the documentation for [each layer](../DOCUMENTATION_INDEX.md#layer-documentation).

### What is typical latency?
- **Speed Layer**: 100ms - 1s
- **Batch Layer**: Minutes - hours (depends on volume)
- **ML Runner**: Seconds - minutes (training), milliseconds (inference)
- **Serving Layer**: < 100ms (API call)

### How many resources are needed?
**Development/Test:**
- 1 machine: 8GB RAM, 4 CPU cores
- Docker containers for services

**Production (small):**
- 3-5 nodes: 16GB RAM, 8 cores each
- Spark, Kafka, Cassandra cluster

**Production (large):**
- 10+ specialized nodes
- Auto-scaling on cloud

---

## Integration

### How do I integrate with existing systems?
- **Harvester**: Calls external APIs
- **Kafka**: Integrates with external producers
- **Serving Layer**: Exposes standard REST APIs
- **Custom connectors**: Extensible

### Does it support database streaming?
Yes, you can use Kafka Connect for streaming from DBs like MySQL, PostgreSQL, MongoDB.

### How do I expose processed data?
Through the **Serving Layer** which provides REST APIs. You can also:
- Write to external DBs
- Publish to queues (Kafka, RabbitMQ)
- Export to files (HDFS, S3)

### Does it support GraphQL?
The base Serving Layer uses REST. You can extend it for GraphQL using Spring Boot libraries.

---

## Licensing and Support

### What license does it use?
Apache License 2.0 - allows:
- Commercial use
- Modification
- Distribution
- Private use
- Must include license notice

### Can I use it in commercial products?
Yes, Apache 2.0 license allows it.

### Is there professional support?
Currently support is community-based through:
- [GitHub Issues](https://github.com/Smart-Shaped/chaM3Leon/issues)
- [Documentation](./README.md)

For professional services, contact Smart-Shaped.

### How do I contribute to the project?
1. Fork the repository
2. Create a branch for your feature
3. Commit changes
4. Open a Pull Request

### Can I request new features?
Yes! Open a GitHub Issue with:
- Feature description
- Use case
- Expected benefits

### Where do I report bugs?
On [GitHub Issues](https://github.com/Smart-Shaped/chaM3Leon/issues)

Include:
- Problem description
- Steps to reproduce
- Framework version
- Relevant logs

---

## Machine Learning (ML Runner)

### Which ML frameworks does it support?

The ML Runner (Python) fully supports the following frameworks with built-in custom components:
- **scikit-learn**
- **TensorFlow**
- **PyTorch**

It can also run models from **any other Python ML library**, leaving full flexibility and responsibility to the user for custom integration.

### How does it manage ML models?
Uses **MLflow** for:
- Model versioning
- Experiment tracking
- Model registry
- Model serving

### Does it support deep learning?
Yes, through TensorFlow, PyTorch.

### How do I do distributed training?
- **Spark MLlib**: Built-in

### Can I use pre-trained models?
Yes! You can load models from:
- MLflow Model Registry
- File system (HDFS, S3)
- Hugging Face Hub
- Custom sources

---

## Security

### Is it safe for sensitive data?
Yes, but you must configure correctly:
- Encryption at rest (Cassandra, HDFS)
- Encryption in transit (TLS/SSL)
- Authentication (Kafka SASL, Cassandra auth)
- Network security (firewall, VPC)

### Does it support GDPR?
The framework provides tools for:
- Data retention policies
- Right to be forgotten (delete API)
- Audit logging
- Data encryption

Final implementation depends on your project.

### How does it handle credentials?
Best practices:
- Use environment variables
- Secret managers (Vault, AWS Secrets Manager)
- Never commit secrets in code

---

## Troubleshooting

### Application won't start
Check:
1. Correct Java version
2. Maven build successful
3. Valid YAML configurations
4. Services (Kafka, Cassandra) running

### Kafka connection refused
- Verify Kafka running: `jps | grep Kafka`
- Check address/port in config
- If Docker, verify network

### Cassandra connection timeout
- Verify Cassandra running: `nodetool status`
- Check datacenter name
- Verify open ports (9042)

### Spark Out of Memory
Increase memory:
```bash
--driver-memory 2g --executor-memory 4g
```

### Checkpoint errors
- Verify HDFS/filesystem permissions
- Check available disk space
- Correct checkpoint path in config

### Poor performance
- Increase parallelism (`spark.default.parallelism`)
- Optimize partitioning
- Verify cluster resources
- Profile with Spark UI

---

## Comparisons

### chaM3Leon vs Apache Beam
**chaM3Leon:**
- More opinionated, less boilerplate
- Includes integrated ML layer
- Faster setup
- Less portability between runners

**Beam:**
- Portable (Spark, Flink, Dataflow)
- More mature
- More complex
- No ML included

### chaM3Leon vs Databricks
**chaM3Leon:**
- Open source, no vendor lock-in
- Lower costs
- Total control
- More manual setup

**Databricks:**
- Managed service
- Excellent tooling
- Expensive
- Vendor lock-in

### chaM3Leon vs Airflow
**They are not alternatives!** Airflow is for orchestration, chaM3Leon for data processing.

You can use them together: Airflow orchestrates chaM3Leon jobs.

---

## Additional Resources

### Where do I find examples?
- GitHub repository (tests, examples)
- [USE_CASES.md](docs/USE_CASES.md)
- [GETTING_STARTED.md](docs/GETTING_STARTED.md)
- Video tutorials

### Are there video tutorials?
Yes, on the [YouTube playlist](README.md#additional-video-resources)

### Is there a community?
- GitHub Issues

### Where can I learn more about Spark?
- [Spark Official Docs](https://spark.apache.org/docs/latest/)
- [Learning Spark (O'Reilly)](https://www.oreilly.com/library/view/learning-spark-2nd/9781492050032/)
- Online courses (Coursera, Udemy)

### Where can I learn more about Kafka?
- [Kafka Official Docs](https://kafka.apache.org/documentation/)
- [Kafka: The Definitive Guide](https://www.confluent.io/resources/kafka-the-definitive-guide/)

---

**Can't find the answer?**
1. Check [Documentation Hub](docs/README.md)
2. Search in [GitHub Issues](https://github.com/Smart-Shaped/chaM3Leon/issues)
3. Open a new Issue or Discussion

---

*Last updated: October 2025*
