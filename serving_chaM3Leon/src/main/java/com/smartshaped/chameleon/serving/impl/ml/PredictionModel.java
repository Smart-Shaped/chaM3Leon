package com.smartshaped.chameleon.serving.impl.ml;

import com.smartshaped.chameleon.serving.model.MLModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@Table("gencastprediction")
public class PredictionModel extends MLModel {

  @PrimaryKey private PredictionModelId id;

  @Column("geopotential")
  private Float geopotential;

  @Column("mean_sea_level_pressure")
  private Float meanSeaLevelPressure;

  @Column("sea_surface_temperature")
  private Float seaSurfaceTemperature;

  @Column("specific_humidity")
  private Float specificHumidity;

  @Column("temperature")
  private Float temperature;

  @Column("temperature_2m")
  private Float temperature2m;

  @Column("total_precipitation_12hr")
  private Float totalPrecipitation12hr;

  @Column("u_component_of_wind")
  private Float uComponentOfWind;

  @Column("u_component_of_wind_10m")
  private Float uComponentOfWind10m;

  @Column("v_component_of_wind")
  private Float vComponentOfWind;

  @Column("v_component_of_wind_10m")
  private Float vComponentOfWind10m;

  @Column("vertical_velocity")
  private Float verticalVelocity;
}
