package com.smartshaped.chameleon.serving.impl;

import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.smartshaped.chameleon.serving.model.BatchBaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Table("toptenmodel")
public class BatchModel extends BatchBaseModel{

	@PrimaryKey
	private String asin;
	
	private Double meanRating;
	
	private Instant calculatedAt;
}
