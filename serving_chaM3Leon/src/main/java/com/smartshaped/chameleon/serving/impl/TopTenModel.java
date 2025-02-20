package com.smartshaped.chameleon.serving.impl;

import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.smartshaped.chameleon.serving.model.BatchModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Table("toptenmodel")
public class TopTenModel extends BatchModel{

	@PrimaryKey
	private String asin;
	
	private Double meanRating;
	
	private Instant calculatedAt;
}
