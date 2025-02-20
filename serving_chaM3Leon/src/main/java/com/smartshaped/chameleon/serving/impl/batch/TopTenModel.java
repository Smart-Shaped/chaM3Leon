package com.smartshaped.chameleon.serving.impl.batch;

import com.smartshaped.chameleon.serving.model.BatchModel;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@Table("toptenmodel")
public class TopTenModel extends BatchModel {

  @PrimaryKey private String asin;

  private Double meanRating;

  private Instant calculatedAt;
}
