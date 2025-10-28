# chaM3Leon Use Cases

This document presents concrete examples of how chaM3Leon can be used in real scenarios. Each use case describes the problem, the solution with chaM3Leon, and the benefits obtained.

---

## Use Case 1: E-Commerce Monitoring System

### Scenario

An e-commerce company needs to:

* Monitor sales in real-time
* Analyze weekly trends
* Predict future demand
* Provide personalized recommendations to customers

### Architecture with chaM3Leon

```
Sales Events → Kafka → Speed Layer → Live Dashboard
                      ↓
                 Batch Layer → Weekly Reports
                      ↓
                 ML Runner → Predictive Models & Recommendations
                      ↓
             Serving Layer → Analytical Reports & Aggregates
```

### Implementation

#### Speed Layer: Real-Time Monitoring

- Function: Count sales and calculate revenue in real-time
- Input: Order stream from Kafka
- Output: Live counters saved in Cassandra
- Latency: < 1 second

#### Batch Layer: Periodic Analysis

- Function: Deep analysis every night
- Input: All daily orders
- Output: Aggregated reports in Cassandra
- Frequency: Every 24 hours

#### ML Runner: Predictive Intelligence & Model Serving

- Function: Train and serve ML models
- Training: Once a week
- Inference: Real-time via ML Runner serve model

**Models implemented:**

* Recommendations
* Demand Forecasting
* Customer Lifetime Value
* Churn Prediction

#### Serving Layer: Data Aggregation & Analytics

```
API Endpoints:
- GET /api/sales/realtime → Current sales metrics
- GET /api/analytics/weekly → Weekly aggregated reports
- GET /api/products/performance → Product performance analytics
```

---

## Use Case 2: IoT Monitoring for Smart City

### Scenario

A smart city needs to manage thousands of sensors:

* Temperature sensors
* Air quality detectors
* Traffic sensors
* Energy consumption meters

### Architecture with chaM3Leon

```
IoT Sensors → Kafka → Speed Layer → Alert System
                 ↓
            Batch Layer → Trend Analysis
                 ↓
            ML Runner → Anomaly Predictions
                 ↓
         Serving Layer → Analytics Dashboard
```

### Implementation

#### Harvester Layer: External Data Collection

- Function: Integrates weather data from external APIs
- Frequency: Every hour
- Sources: OpenWeatherMap, ARPA, etc.

#### Speed Layer: Real-Time Processing

- Volume: 10,000 readings/sec
- Latency: < 500ms
- Functions:
     - Moving averages
     - Critical threshold detection
     - Automatic alert triggering

#### Batch Layer: Historical Analysis

- Data: 6 months of readings
- Output: Seasonal patterns, correlations

#### ML Runner: Intelligent Predictions & Model Serving

Models:
- Air quality forecast (24h)
- Traffic estimation (next 2h)
- Anomaly detection
- Traffic light optimization

#### Serving Layer: Analytics

```
API Endpoints:
- GET /api/sensors/summary → Aggregated sensor data
- GET /api/alerts/history → Past alerts
- GET /api/energy/trends → Energy consumption trends
```

---

## Use Case 3: Social Media Analytics

### Scenario

A company wants to monitor brand reputation:

* Real-time sentiment analysis
* Mention trends
* Influencer identification
* Crisis management

### Architecture with chaM3Leon

```
Social APIs → Harvester → Kafka → Speed Layer → Negative Alerts
                         ↓
                    Batch Layer → Brand Reports
                         ↓
                    ML Runner → Sentiment Predictions & Insights
                         ↓
               Serving Layer → Marketing Analytics
```

### Implementation

#### Harvester Layer: Social Data Collection

- Sources: Twitter, Facebook, Instagram, Reddit
- Frequency: Every 5 minutes
- Output: Posts mentioning the brand

#### Speed Layer: Real-Time Sentiment

Processing:
- Text cleaning
- Sentiment analysis via ML Runner
- Mention counting
- Trending topics identification

#### Batch Layer: Deep Analytics

Daily analyses:
- Sentiment per product
- Geographic distribution
- Top discussion topics
- Competitor comparison

#### ML Runner: NLP & ML Model Serving

Models:
- Sentiment Classification
- Topic Modeling
- Influencer Scoring
- Trend Prediction

#### Serving Layer: Analytics Dashboard

```
API Endpoints:
- GET /api/sentiment/realtime → Current sentiment
- GET /api/mentions/trending → Trending topics
- GET /api/influencers/top → Top influencers
- GET /api/reports/daily → Daily report
```

---

## Use Case 4: Healthcare Monitoring

### Scenario

A hospital wants to monitor critical patients:

* Vital parameter monitoring
* Complication prediction
* Staff shift optimization
* Emergency management

### Architecture with chaM3Leon

```
Medical Devices → Kafka → Speed Layer → Critical Alerts
                        ↓
                   Batch Layer → Patient Analysis
                        ↓
                   ML Runner → Risk Predictions & Model Serving
                        ↓
              Serving Layer → Analytical Dashboard
```

### Implementation

#### Speed Layer: Vital Monitoring

- Parameters: Heart rate, BP, O2 saturation, temperature
- Alerts: Immediate on anomalies

#### Batch Layer: Clinical Analysis

Nightly analyses:
- Recovery patterns
- Treatment effectiveness
- Resource utilization
- Department performance

#### ML Runner: Predictive Medicine

Models:
- Post-operative complication prediction
- Recovery time estimation
- Drug dosage optimization
- Emergency spike prediction

#### Serving Layer: Analytics

```
APIs for hospital systems:
- GET /api/patients/{id}/vitals → Current parameters
- GET /api/alerts/summary → Historical alerts
- GET /api/resources/optimization → Resource allocation analytics
```

---

## Use Case 5: Manufacturing Predictive Maintenance

### Scenario

Reduce unplanned machine downtime:

* Sensor monitoring
* Failure prediction
* Maintenance optimization
* Waste reduction

### Architecture with chaM3Leon

```
Machine Sensors → Kafka → Speed Layer → Anomaly Alerts
                         ↓
                    Batch Layer → Failure Patterns
                         ↓
                    ML Runner → Predictive Models
                         ↓
               Serving Layer → Maintenance Analytics
```

### Implementation

#### Speed Layer: Condition Monitoring

- Sensors: Vibration, temperature, pressure, energy, noise
- Frequency: 100 readings/sec per machine
- Alerts: Values out of range

#### Batch Layer: Historical Analysis

Analysis:
- Sensor correlation with failures
- Component degradation
- Maintenance effectiveness
- Downtime costs

#### ML Runner: Predictive Analytics & Serving

Models:
- Time-to-Failure
- Failure Type Classification
- Remaining Useful Life
- Optimal Maintenance Schedule

#### Serving Layer: Analytics

```
API Endpoints:
- GET /api/machines/analytics → Downtime trends
- GET /api/maintenance/history → Maintenance reports
```

---
## Use Case 6: Precision Agriculture

### Scenario

An agricultural company wants to optimize harvests using technology:

* Soil condition monitoring
* Smart irrigation
* Harvest prediction
* Plant disease detection

### Architecture with chaM3Leon

```

Field Sensors + Drones → Harvester → Batch Layer → Soil Maps
↓
ML Runner → Yield, Disease & Irrigation Predictions
↓
Weather API → Harvester → Speed Layer → Real-Time Irrigation Decisions
↓
Serving Layer → Historical Analytics & Reports

```

### Implementation

#### Harvester Layer: Multi-Source Data Collection

Data sources:

* Soil moisture sensors
* Satellite imagery
* Weather API data
* Drones with multispectral cameras

#### Speed Layer: Real-Time Irrigation Decisions

- Function: Compute immediate irrigation actions
- Input: Current sensor and weather data
- Output: Irrigation commands or recommendations
- Latency: < 1 second

#### Batch Layer: Seasonal and Historical Analysis

- Function: Aggregate long-term data
- Input: Collected soil, weather, and yield data
- Output: Soil fertility maps, seasonal trends, and treatment effectiveness


#### ML Runner: Predictive Intelligence & Model Serving

- Function: Train and serve models for:
     - Yield Prediction (harvest estimates)
     - Disease Detection (plant health from images)
     - Irrigation Optimization (amount and timing)
- Training: Once per season or as configured
- Inference: Real-time or batch via ML Runner serve model


#### Serving Layer: Analytics and Historical Reports

Function: Expose aggregated and historical insights
```
API Endpoints:

* GET /api/soil/maps → Soil fertility and condition maps
* GET /api/yield/predictions → Predicted yield statistics
* GET /api/disease/reports → Detected plant disease summaries
* GET /api/irrigation/history → Historical irrigation recommendations

```
---

## 💡 Template for Your Use Case

### 1. Define the Problem

- Which business problem are you solving?
- What data do you have?
- What is the measurable goal?

### 2. Choose Required Layers

```
☐ Harvester: External data needed?
☐ Speed: Real-time processing needed?
☐ Batch: Historical analysis needed?
☐ ML Runner: Predictive intelligence needed?
☐ Serving: Analytics access needed?
```

### 3. Define Data Flow

```
[Source] → [Layer 1] → [Layer 2] → [Output]
```

### 4. Identify KPIs

- What will you measure?
- How will you evaluate success?
- Expected improvements?

### 5. Estimate Resources

- Data volume
- Processing frequency
- Computational resources

---

## Conclusion

chaM3Leon is versatile across domains:

* **E-commerce**: Sales & recommendations
* **IoT**: Smart cities & monitoring
* **Social Media**: Brand reputation
* **Healthcare**: Predictive medicine
* **Manufacturing**: Preventive maintenance
* **Agriculture**: Precision farming

**Key to success:**

1. Understand your specific problem
2. Choose appropriate layers
3. Configure the data flow correctly
4. Iterate and improve based on results

---

**Ready to create your use case?** See the [Getting Started Guide](GETTING_STARTED.md) to begin!
