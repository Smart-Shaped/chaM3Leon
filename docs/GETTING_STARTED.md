# Guida Introduttiva a chaM3Leon

## Benvenuto!

Questa guida ti aiuterà a fare i primi passi con chaM3Leon, dal setup iniziale alla creazione della tua prima applicazione di elaborazione dati. Non preoccuparti se non sei un esperto: ti guideremo passo dopo passo.

## Prerequisiti

Prima di iniziare, assicurati di avere installato:

### Software Necessario

- **Java 11** (per i layer Spark) o **Java 21** (per il Serving Layer)
  - Verifica: `java -version`
  - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) o [OpenJDK](https://adoptium.net/)

- **Maven 3.6+** (per la gestione delle dipendenze)
  - Verifica: `mvn -version`
  - Download: [Apache Maven](https://maven.apache.org/download.cgi)

- **Apache Spark 3.5+** (per l'elaborazione distribuita)
  - Download: [Apache Spark](https://spark.apache.org/downloads.html)

- **Docker** (opzionale ma raccomandato)
  - Download: [Docker Desktop](https://www.docker.com/products/docker-desktop)

### Per il Layer Python (ML Runner)

- **Python 3.12**
  - Verifica: `python --version`
  - Download: [Python.org](https://www.python.org/downloads/)

- **pip** (gestore pacchetti Python)
  - Verifica: `pip --version`

## Fase 1: Installazione di chaM3Leon

### Opzione A: Clone del Repository

```bash
# Clona il repository principale
git clone https://github.com/Smart-Shaped/chaM3Leon.git
cd chaM3Leon

# Inizializza il submodule Python (ML Runner)
git submodule init
git submodule update
```

### Opzione B: Download ZIP

1. Vai su [GitHub - chaM3Leon](https://github.com/Smart-Shaped/chaM3Leon)
2. Clicca su "Code" → "Download ZIP"
3. Estrai il file ZIP in una cartella di tua scelta

### Compilazione del Framework

```bash
# Naviga nella cartella chaM3Leon
cd chaM3Leon

# Compila il framework
mvn clean install
```

Questo comando:
- Scarica tutte le dipendenze necessarie
- Compila il codice sorgente
- Esegue i test
- Crea i file JAR utilizzabili

**Nota:** La prima compilazione può richiedere alcuni minuti perché Maven scarica tutte le dipendenze.

## Fase 2: Setup dell'Ambiente

### Opzione Raccomandata: Usando Docker

Il modo più semplice per iniziare è usare Docker, che configura automaticamente tutti i servizi necessari.

```bash
# Clona il repository Docker
git clone https://github.com/Smart-Shaped/docker_chaM3Leon.git
cd docker_chaM3Leon

# Avvia tutti i servizi
docker-compose up -d
```

Questo avvia:
- **Apache Kafka**: Per lo streaming dei dati
- **Apache Cassandra**: Per la persistenza
- **HDFS**: Per lo storage distribuito
- **Hadoop Yarn**: Per l'esecuzione distribuita
- **Spark Cluster**: Per l'elaborazione

## Fase 3: Il Tuo Primo Progetto

Creiamo un semplice progetto che usa il **Batch Layer** per elaborare dati.

### Struttura del Progetto

```bash
my-chameleon-app-batch/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mycompany/
│       │           └── my-app/
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

### 1. Crea il file `pom.xml`

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

### 2. Crea il Data Model

File: `src/main/java/com/mycompany/demo/model/MyDataModel.java`

```java
package com.mycompany.demo.model;

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

### 3. Crea il Preprocessor

File: `src/main/java/com/mycompany/demo/DemoPreprocessor.java`

```java
package com.mycompany.demo;

import com.smartshaped.chameleon.common.preprocessing.Preprocessor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DemoPreprocessor extends Preprocessor {
    
    @Override
    public Dataset<Row> preprocess(Dataset<Row> inputData) {
        // Esempio: filtra solo i record con value > 0
        return inputData.filter("value > 0");
    }
}
```

### 4. Crea il Batch Updater (Opzionale)

File: `src/main/java/com/mycompany/demo/DemoBatchUpdater.java`

```java
package com.mycompany.demo;

import com.smartshaped.chameleon.batch.BatchUpdater;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DemoBatchUpdater extends BatchUpdater {
    
    public DemoBatchUpdater() {
        super();
    }
    
    @Override
    public Dataset<Row> updateBatch(Dataset<Row> batchData) {
        // Esempio: calcola statistiche aggregate
        return batchData.groupBy("message")
                       .count();
    }
}
```

### 5. Crea il Batch Layer

File: `src/main/java/com/mycompany/demo/DemoBatchLayer.java`

```java
package com.mycompany.demo;

import com.smartshaped.chameleon.batch.BatchLayer;

public class DemoBatchLayer extends BatchLayer {
    
    public DemoBatchLayer() {
        super();
    }
}
```

### 6. Crea la Main Class

File: `src/main/java/com/mycompany/demo/DemoApp.java`

```java
package com.mycompany.demo;

public class DemoApp {
    
    public static void main(String[] args) {
        DemoBatchLayer batchLayer = new DemoBatchLayer();
        batchLayer.start();
    }
}
```

### 7. Configura il Framework

File: `src/main/resources/framework-config.yml`

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
        class: "com.mycompany.demo.DemoPreprocessor"
        path: "/data/demo"
        checkpoint: "/checkpoints/demo"
  
  cassandra:
    model.class: "com.mycompany.demo.model.MyDataModel"
    datacenter: "datacenter1"
    keyspace:
      name: "demo_keyspace"
      replication_factor: 1
    checkpoint: "/checkpoints/cassandra"
  
  updater:
    class: "com.mycompany.demo.DemoBatchUpdater"
```

File: `src/main/resources/typeMapping.yml`

```yaml
java.lang.String: "text"
java.lang.Long: "bigint"
java.lang.Double: "double"
java.lang.Integer: "int"
java.lang.Boolean: "boolean"
```

### 8. Compila il Progetto

```bash
mvn clean package
```

Questo crea un file JAR in `target/demo-batch-1.0.0.jar`

### 9. Esegui l'Applicazione

#### Con Spark Locale

```bash
spark-submit \
  --class com.mycompany.demo.DemoApp \
  --master local[*] \
  target/demo-batch-1.0.0.jar
```

#### Con Spark Cluster

```bash
spark-submit \
  --class com.mycompany.demo.DemoApp \
  --master spark://your-spark-master:7077 \
  --deploy-mode cluster \
  target/demo-batch-1.0.0.jar
```

## Fase 4: Verifica il Funzionamento

### Invia Dati di Test a Kafka

```bash
# Crea il topic
kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --topic demo-topic \
  --partitions 1 \
  --replication-factor 1

# Invia messaggi di test
kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic demo-topic

# Inserisci alcuni JSON (premi Enter dopo ognuno)
{"id":"1","message":"test","timestamp":1234567890,"value":100.5}
{"id":"2","message":"test","timestamp":1234567891,"value":200.3}
{"id":"3","message":"demo","timestamp":1234567892,"value":150.7}
```

### Verifica i Dati in Cassandra

```bash
# Connettiti a Cassandra
cqlsh localhost

# Usa il keyspace
USE demo_keyspace;

# Visualizza i dati
SELECT * FROM mydatamodel;
```

## Prossimi Passi

Congratulazioni! Hai creato la tua prima applicazione chaM3Leon. 🎉

### Cosa Fare Dopo

1. **Esplora gli Altri Layer**
   - Prova il [Speed Layer](../chaM3Leon/speed/README.md) per l'elaborazione in tempo reale
   - Sperimenta con l'[Harvester Layer](../chaM3Leon/harvester/README.md) per raccogliere dati da API
   - Usa il [ML Runner](https://github.com/Smart-Shaped/PyChaM3Leon) per machine learning

2. **Approfondisci le Configurazioni**
   - Leggi la [Guida alle Configurazioni](config_list.md)
   - Impara le best practices per i nomi: [Apps Naming](apps_naming.md)

3. **Studia Casi d'Uso Reali**
   - Consulta [USE_CASES.md](USE_CASES.md) per esempi pratici

4. **Esplora l'Architettura**
   - Comprendi meglio come funziona: [ARCHITECTURE.md](ARCHITECTURE.md)

## Risoluzione Problemi Comuni

### Problema: "Cannot find symbol" durante la compilazione

**Soluzione:** Assicurati di aver eseguito `mvn clean install` nella cartella principale di chaM3Leon prima di compilare il tuo progetto.

### Problema: Kafka non si connette

**Soluzione:** 
- Verifica che Kafka sia in esecuzione: `jps` (dovresti vedere `Kafka`)
- Controlla che l'indirizzo nel file di configurazione sia corretto
- Se usi Docker, assicurati che i container siano attivi: `docker ps`

### Problema: Cassandra connection refused

**Soluzione:**
- Verifica che Cassandra sia in esecuzione
- Controlla le porte (default: 9042)
- Verifica il datacenter nella configurazione

### Problema: Out of Memory durante l'esecuzione Spark

**Soluzione:**
Aumenta la memoria disponibile per Spark:

```bash
spark-submit \
  --class com.mycompany.demo.DemoApp \
  --master local[*] \
  --driver-memory 2g \
  --executor-memory 2g \
  target/demo-batch-1.0.0.jar
```

## Risorse Utili

### Documentazione
- [README Principale](../README.md)
- [Value Proposition](../VALUE_PROPOSITION.md)
- [Documentazione Batch Layer](../chaM3Leon/batch/README.md)
- [Documentazione Speed Layer](../chaM3Leon/speed/README.md)
- [Documentazione Harvester](../chaM3Leon/harvester/README.md)

### Video Tutorial
- [Presentazione Framework](https://www.youtube.com/watch?v=wtVyYUDlRQc)
- [Demo Batch e Speed](https://www.youtube.com/watch?v=UjzYc9C1krU)
- [Demo Harvester](https://www.youtube.com/watch?v=pwE223S0-oU)

### Repository
- [chaM3Leon Main](https://github.com/Smart-Shaped/chaM3Leon)
- [PyChaM3Leon (ML Runner)](https://github.com/Smart-Shaped/PyChaM3Leon)
- [Docker Setup](https://github.com/Smart-Shaped/docker_chaM3Leon)

## Supporto

Se hai domande o problemi:
1. Controlla questa documentazione
2. Consulta i video tutorial
3. Apri una issue su GitHub
4. Contatta la community

---

**Buon coding con chaM3Leon!**
