package com.smartshaped.chameleon.serving.impl.ml;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Setter
@Getter
@AllArgsConstructor
@PrimaryKeyClass
public class PredictionModelId implements Serializable {

  @PrimaryKeyColumn(name = "time_seconds", type = PrimaryKeyType.PARTITIONED)
  private long timeSeconds;

  @PrimaryKeyColumn(name = "lat", type = PrimaryKeyType.CLUSTERED)
  private float lat;

  @PrimaryKeyColumn(name = "lon", type = PrimaryKeyType.CLUSTERED)
  private float lon;

  @PrimaryKeyColumn(name = "level", type = PrimaryKeyType.CLUSTERED)
  private int level;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PredictionModelId that = (PredictionModelId) o;
    return timeSeconds == that.timeSeconds
        && Float.compare(that.lat, lat) == 0
        && Float.compare(that.lon, lon) == 0
        && level == that.level;
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeSeconds, lat, lon, level);
  }
}
