# Domande Frequenti (FAQ)

## Indice
- [Generale](#generale)
- [Installazione e Setup](#installazione-e-setup)
- [Architettura e Design](#architettura-e-design)
- [Sviluppo](#sviluppo)
- [Deployment e Operations](#deployment-e-operations)
- [Performance e Scalabilità](#performance-e-scalabilità)
- [Integrazione](#integrazione)
- [Licensing e Supporto](#licensing-e-supporto)

---

## Generale

### Cos'è chaM3Leon?
chaM3Leon è un framework open-source modulare per Big Data e Machine Learning che semplifica la gestione dell'intero ciclo di vita dei dati, dalla raccolta all'analisi, fino alla distribuzione.

### Per chi è pensato chaM3Leon?
- **Data Engineers**: Per costruire pipeline dati robuste
- **Data Scientists**: Per implementare modelli ML in produzione
- **Aziende**: Per progetti Big Data senza investimenti enormi
- **Sviluppatori**: Per imparare best practices Big Data/ML

### Quali problemi risolve?
- Gestione di grandi volumi di dati
- Elaborazione real-time e batch
- Integrazione di machine learning
- Scalabilità automatica
- Riduzione time-to-market progetti data

### È gratuito?
Sì, chaM3Leon è rilasciato con licenza Apache 2.0, che permette uso commerciale gratuito.

### Chi mantiene il progetto?
Il progetto è mantenuto da Smart-Shaped e dalla community open source.

---

## Installazione e Setup

### Quali sono i prerequisiti?
**Per layer Java:**
- Java 11 (layer Spark) o Java 21 (Serving Layer)
- Maven 3.6+
- Apache Spark 3.5+

**Per ML Runner (Python):**
- Python 3.8+
- pip

### Devo installare tutti i componenti?
No, chaM3Leon è modulare. Installa e usa solo i layer di cui hai bisogno.

### Posso usare Docker?
Sì! Anzi, è il modo raccomandato. Abbiamo un repository dedicato: [docker_chaM3Leon](https://github.com/Smart-Shaped/docker_chaM3Leon)

### Quanto tempo richiede il setup?
- **Con Docker**: 30 minuti - 1 ora
- **Setup manuale**: 2-4 ore
- **Primo progetto funzionante**: 1 giornata

### Funziona su Windows?
Sì, ma raccomandiamo Linux o macOS per produzione. Su Windows usa WSL2 o Docker.

### Posso usarlo su cloud?
Sì, funziona su AWS, Azure, Google Cloud e qualsiasi provider che supporti JVM e Docker.

---

## Architettura e Design

### Cos'è la Lambda Architecture?
Un pattern architetturale che combina:
- **Batch Layer**: Elaborazione accurata di dati storici
- **Speed Layer**: Elaborazione rapida dati real-time
- **Serving Layer**: Unifica e distribuisce i risultati

Vantaggio: Hai sia velocità che accuratezza!

### Devo usare tutti i layer?
No! Usa solo quelli necessari:
- Solo batch processing → Batch Layer
- Solo real-time → Speed Layer
- Solo raccolta dati → Harvester Layer
- Solo ML → ML Runner
- Serve API? → Serving Layer

### I layer comunicano tra loro?
Sì, attraverso:
- **Kafka**: Per streaming dati
- **Cassandra**: Per storage condiviso
- **HDFS**: Per dati storici

### Posso estendere i layer?
Sì! Ogni layer è progettato per essere esteso. Crei classi che ereditano dalle classi base del framework.

### Posso usare altri database oltre Cassandra?
Il framework è ottimizzato per Cassandra, ma puoi estenderlo per supportare altri DB. Per il Serving Layer (Spring Boot) è più semplice integrare altri database.

---

## Sviluppo

### Quali linguaggi devo conoscere?
Dipende dai layer che usi:
- **Java**: Per Batch, Speed, Harvester, Serving
- **Python**: Solo per ML Runner
- **YAML**: Per configurazioni (facile!)

### Serve esperienza con Spark?
Non necessariamente. Il framework astrae molte complessità di Spark. Però una conoscenza base aiuta.

### Come debuggo la mia applicazione?
- **Locale**: Usa la tua IDE (IntelliJ, Eclipse, VS Code)
- **Log**: Configurabili tramite Log4j
- **Spark UI**: Disponibile su `http://localhost:4040` durante esecuzione
- **Cassandra**: Usa `cqlsh` per verificare dati

### Posso testare senza cluster Spark?
Sì! Puoi usare `local[*]` mode per testare su singola macchina.

### Come gestisco le configurazioni?
Attraverso file YAML:
- `framework-config.yml`: Configurazioni ambiente
- `local-config.yml`: Override locali
- `typeMapping.yml`: Mapping tipi dati

Vedi [config_list.md](docs/config_list.md) per dettagli.

### Dove metto il mio codice custom?
Crei classi che estendono quelle del framework:
- `BatchLayer` → La tua `MyBatchLayer`
- `Preprocessor` → Il tuo `MyPreprocessor`
- `BatchUpdater` → Il tuo `MyBatchUpdater`
- etc.

### Come aggiungo nuove dipendenze?
Nel tuo `pom.xml` (per Java) o `requirements.txt` (per Python).

---

## Deployment e Operations

### Come faccio il deploy in produzione?
1. Compila: `mvn clean package`
2. Genera JAR shaded
3. Submit a Spark:
```bash
spark-submit --class com.myapp.Main \
  --master spark://master:7077 \
  target/myapp.jar
```

Oppure usa Docker per containerizzazione.

### Supporta Kubernetes?
Sì, puoi fare deploy su Kubernetes usando Spark Operator o container generici.

### Come monitoro le applicazioni?
- **Spark UI**: Metriche job Spark
- **Cassandra**: Monitoring con tools come DataStax OpsCenter
- **Kafka**: Tools come Kafka Manager, Confluent Control Center
- **Custom**: Log aggregation (ELK stack, Splunk)

### Come gestisco gli errori?
Il framework include:
- Exception handling custom per ogni layer
- Checkpointing per recovery
- Retry logic configurabile
- Dead letter queues per Kafka

### Supporta alta disponibilità?
Sì, tutti i componenti supportano HA:
- **Spark**: Cluster mode con failover
- **Kafka**: Replication
- **Cassandra**: Distributed by design
- **HDFS**: Replication factor configurabile

---

## Performance e Scalabilità

### Quanto scala?
- **Dati**: Da GB a PB
- **Throughput**: Milioni di eventi/secondo
- **Latency**: Millisecondi (Speed Layer)

Dipende dall'infrastruttura che allochi.

### Come ottimizzare le performance?
- **Spark**: Tuning partitions, memory, parallelism
- **Kafka**: Partitioning topics, consumer groups
- **Cassandra**: Partition key design, replication factor
- **Codice**: Evita shuffle inutili, usa broadcast variables

Consulta la documentazione di ciascun layer.

### Qual è la latenza tipica?
- **Speed Layer**: 100ms - 1s
- **Batch Layer**: Minuti - ore (dipende dal volume)
- **ML Runner**: Secondi - minuti (training), millisecondi (inference)
- **Serving Layer**: < 100ms (API call)

### Quante risorse servono?
**Sviluppo/Test:**
- 1 macchina: 8GB RAM, 4 CPU cores
- Docker containers per servizi

**Produzione (piccola):**
- 3-5 nodi: 16GB RAM, 8 cores ciascuno
- Cluster Spark, Kafka, Cassandra

**Produzione (grande):**
- 10+ nodi specializzati
- Auto-scaling su cloud

---

## Integrazione

### Come integro con sistemi esistenti?
- **Harvester**: Chiama API esterne
- **Kafka**: Integra con producer esterni
- **Serving Layer**: Espone REST API standard
- **Custom connectors**: Estendibili

### Supporta streaming da database?
Sì, puoi usare Kafka Connect per streaming da DB come MySQL, PostgreSQL, MongoDB.

### Posso integrare con AWS/Azure?
Sì:
- **AWS**: S3, Kinesis, EMR, Redshift
- **Azure**: Blob Storage, Event Hubs, HDInsight
- **GCP**: Cloud Storage, Pub/Sub, Dataproc

### Come espongo i dati elaborati?
Attraverso il **Serving Layer** che fornisce REST API. Puoi anche:
- Scrivere su DB esterni
- Pubblicare su code (Kafka, RabbitMQ)
- Export su file (HDFS, S3)

### Supporta GraphQL?
Il Serving Layer base usa REST. Puoi estenderlo per GraphQL usando librerie Spring Boot.

---

## Licensing e Supporto

### Che licenza usa?
Apache License 2.0 - permette:
- ✅ Uso commerciale
- ✅ Modifica
- ✅ Distribuzione
- ✅ Uso privato
- ⚠️ Devi includere notice di licenza

### Posso usarlo in prodotti commerciali?
Sì, la licenza Apache 2.0 lo permette.

### C'è supporto professionale?
Attualmente il supporto è community-based tramite:
- GitHub Issues
- GitHub Discussions
- Documentazione

Per servizi professionali, contatta Smart-Shaped.

### Come contribuisco al progetto?
1. Fork il repository
2. Crea un branch per la tua feature
3. Commit le modifiche
4. Apri una Pull Request

Consulta CONTRIBUTING.md (se disponibile).

### Posso richiedere nuove feature?
Sì! Apri una GitHub Issue con:
- Descrizione della feature
- Caso d'uso
- Benefici attesi

### Dove segnalo bug?
Su [GitHub Issues](https://github.com/Smart-Shaped/chaM3Leon/issues)

Includi:
- Descrizione del problema
- Steps per riprodurre
- Versione framework
- Log rilevanti

---

## Machine Learning (ML Runner)

### Quali framework ML supporta?
Il ML Runner (Python) supporta:
- scikit-learn
- TensorFlow
- PyTorch
- XGBoost
- LightGBM
- Qualsiasi libreria Python ML

### Come gestisce i modelli ML?
Usa **MLflow** per:
- Model versioning
- Experiment tracking
- Model registry
- Model serving

### Supporta deep learning?
Sì, tramite TensorFlow, PyTorch, Keras.

### Come faccio training distribuito?
- **Spark MLlib**: Built-in
- **Horovod**: Per deep learning distribuito
- **Ray**: Per scaling Python ML

### Posso usare modelli pre-addestrati?
Sì! Puoi caricare modelli da:
- MLflow Model Registry
- File system (HDFS, S3)
- Hugging Face Hub
- Custom sources

---

## Sicurezza

### È sicuro per dati sensibili?
Sì, ma devi configurare correttamente:
- Encryption at rest (Cassandra, HDFS)
- Encryption in transit (TLS/SSL)
- Autenticazione (Kafka SASL, Cassandra auth)
- Network security (firewall, VPC)

### Supporta GDPR?
Il framework fornisce strumenti per:
- Data retention policies
- Right to be forgotten (delete API)
- Audit logging
- Data encryption

L'implementazione finale dipende dal tuo progetto.

### Come gestisce le credenziali?
Best practice:
- Usa environment variables
- Secret managers (Vault, AWS Secrets Manager)
- Non committare mai secrets nel codice

---

## Troubleshooting

### L'applicazione non parte
Controlla:
1. Versione Java corretta
2. Maven build successful
3. Configurazioni YAML valide
4. Servizi (Kafka, Cassandra) running

### Kafka connection refused
- Verifica Kafka running: `jps | grep Kafka`
- Controlla indirizzo/porta in config
- Se Docker, verifica network

### Cassandra connection timeout
- Verifica Cassandra running: `nodetool status`
- Controlla datacenter name
- Verifica porte aperte (9042)

### Out of Memory Spark
Aumenta memoria:
```bash
--driver-memory 4g --executor-memory 4g
```

### Checkpoint errors
- Verifica permessi HDFS/filesystem
- Controlla spazio disco disponibile
- Path checkpoint corretto in config

### Performance scarsa
- Aumenta parallelism (`spark.default.parallelism`)
- Ottimizza partitioning
- Verifica risorse cluster
- Profile con Spark UI

---

## Confronti

### chaM3Leon vs Apache Beam
**chaM3Leon:**
- ✅ Più opinioned, meno boilerplate
- ✅ Include ML layer integrato
- ✅ Setup più rapido
- ⚠️ Meno portabilità tra runner

**Beam:**
- ✅ Portabile (Spark, Flink, Dataflow)
- ✅ Più maturo
- ⚠️ Più complesso
- ⚠️ Non include ML

### chaM3Leon vs Databricks
**chaM3Leon:**
- ✅ Open source, no vendor lock-in
- ✅ Costi inferiori
- ✅ Controllo totale
- ⚠️ Più setup manual

**Databricks:**
- ✅ Managed service
- ✅ Ottimo tooling
- ⚠️ Costoso
- ⚠️ Vendor lock-in

### chaM3Leon vs Airflow
**Non sono alternativi!** Airflow è per orchestrazione, chaM3Leon per elaborazione dati.

Puoi usarli insieme: Airflow orchestra job chaM3Leon.

---

## Risorse Aggiuntive

### Dove trovo esempi?
- Repository GitHub (test, esempi)
- [USE_CASES.md](docs/USE_CASES.md)
- [GETTING_STARTED.md](docs/GETTING_STARTED.md)
- Video tutorial

### Ci sono video tutorial?
Sì, sulla [playlist YouTube](README.md#additional-video-resources)

### C'è una community?
- GitHub Discussions
- GitHub Issues
- (Eventuale Slack/Discord - da verificare)

### Dove approfondisco Spark?
- [Spark Official Docs](https://spark.apache.org/docs/latest/)
- [Learning Spark (O'Reilly)](https://www.oreilly.com/library/view/learning-spark-2nd/9781492050032/)
- Corsi online (Coursera, Udemy)

### Dove approfondisco Kafka?
- [Kafka Official Docs](https://kafka.apache.org/documentation/)
- [Kafka: The Definitive Guide](https://www.confluent.io/resources/kafka-the-definitive-guide/)

---

## Domande Business

### Quanto costa implementare chaM3León?
- **Software**: Gratis (open source)
- **Infrastruttura**: €15-45k setup, €24-120k/anno operativo
- **Team**: 2-3 persone (data engineer, data scientist, devops)
- Vedi [SUMMARY.md](SUMMARY.md) per dettagli

### Qual è il ROI?
Tipicamente:
- Break-even: 6-9 mesi
- ROI 3 anni: 300-500%
- Dipende dal caso d'uso

### Quanto tempo serve per andare in produzione?
- **PoC**: 1 mese
- **MVP**: 2-3 mesi
- **Production-ready**: 3-6 mesi

### Serve un team dedicato?
Inizialmente no, ma per scaling sì:
- **Start**: 1-2 persone part-time
- **Production**: 2-3 full-time
- **Scale**: Team dedicato (5-10 persone)

---

**Non trovi la risposta?**
1. Controlla [Documentation Hub](docs/README.md)
2. Cerca in [GitHub Issues](https://github.com/Smart-Shaped/chaM3Leon/issues)
3. Apri una nuova Issue o Discussion

---

*Ultimo aggiornamento: Ottobre 2025*
