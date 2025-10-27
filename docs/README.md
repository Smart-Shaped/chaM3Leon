# Documentazione chaM3Leon

Benvenuto nella documentazione completa del framework chaM3Leon. Questa pagina ti guiderà attraverso tutte le risorse disponibili.

## Panoramica della Documentazione

### Per Iniziare

| Documento | Descrizione | Pubblico |
|-----------|-------------|----------|
| [SUMMARY.md](../SUMMARY.md) | Executive summary per decision makers | C-Level, Manager |
| [VALUE_PROPOSITION.md](../VALUE_PROPOSITION.md) | Scopri il valore e i vantaggi di chaM3León | Tutti |
| [GETTING_STARTED.md](GETTING_STARTED.md) | Guida passo-passo per il primo progetto | Sviluppatori |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Comprendi l'architettura del framework | Architetti, Team Lead |
| [USE_CASES.md](USE_CASES.md) | Esempi pratici di applicazioni reali | Business, Sviluppatori |
| [FAQ.md](FAQ.md) | Domande frequenti e risposte | Tutti |

### Documentazione Tecnica

| Documento | Descrizione |
|-----------|-------------|
| [config_list.md](config_list.md) | Guida completa alle configurazioni |
| [apps_naming.md](apps_naming.md) | Convenzioni per i nomi delle applicazioni |

### Documentazione Layer Specifici

| Layer | Documentazione | Linguaggio | Descrizione |
|-------|----------------|------------|-------------|
| **Batch** | [README](../chaM3Leon/batch/README.md) | Java | Elaborazione dati storici in batch |
| **Speed** | [README](../chaM3Leon/speed/README.md) | Java | Processing real-time streaming |
| **Harvester** | [README](../chaM3Leon/harvester/README.md) | Java | Raccolta dati da fonti esterne |
| **Serving** | [README](../serving_chaM3Leon/README.md) | Java/Spring Boot | API REST per distribuzione dati |
| **ML Runner** | [GitHub](https://github.com/Smart-Shaped/PyChaM3Leon) | Python | Machine Learning e MLOps |

---

## Risorse Video

### YouTube Tutorials
- [Presentazione del Framework](https://www.youtube.com/watch?v=wtVyYUDlRQc) - Overview generale
- [Demo Batch e Speed Layer](https://www.youtube.com/watch?v=UjzYc9C1krU) - Esempio pratico
- [Demo Harvester e ML Runner](https://www.youtube.com/watch?v=pwE223S0-oU) - ML integration

---

## Repository Correlati

| Repository | Scopo | Link |
|------------|-------|------|
| **chaM3Leon Main** | Framework principale | [GitHub](https://github.com/Smart-Shaped/chaM3Leon) |
| **PyChaM3Leon** | ML Runner (Python) | [GitHub](https://github.com/Smart-Shaped/PyChaM3Leon) |
| **docker_chaM3Leon** | Docker setup | [GitHub](https://github.com/Smart-Shaped/docker_chaM3Leon) |

---

## Mappa Concettuale

```
chaM3Leon Framework
│
├── Concetti Base
│   ├── VALUE_PROPOSITION.md (Perché usarlo?)
│   └── ARCHITECTURE.md (Come funziona?)
│
├── Pratica
│   ├── GETTING_STARTED.md (Come iniziare?)
│   └── USE_CASES.md (Esempi reali)
│
├── Layer (Java/Spark)
│   ├── Batch Layer → Elaborazione storica
│   ├── Speed Layer → Real-time processing
│   ├── Harvester Layer → Data collection
│   └── Serving Layer → API distribution
│
├── Layer (Python)
│   └── ML Runner → Machine Learning
│
└── Riferimenti
    ├── config_list.md (Configurazioni)
    └── apps_naming.md (Convenzioni)
```
## Supporto

### Hai bisogno di aiuto?
1. **Controlla la documentazione** - Probabilmente la risposta è qui
2. **Guarda i video** - Tutorial passo-passo
3. **GitHub Issues** - Per bug e feature request
4. **Discussions** - Per domande generali

### Link utili:
- [GitHub Issues](https://github.com/Smart-Shaped/chaM3Leon/issues)
- [GitHub Discussions](https://github.com/Smart-Shaped/chaM3Leon/discussions)

---

*Torna al [README principale](../README.md)*
