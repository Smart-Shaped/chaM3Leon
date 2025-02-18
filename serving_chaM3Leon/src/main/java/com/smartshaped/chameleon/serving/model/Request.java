package com.smartshaped.chameleon.serving.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("request")
public class Request {

  @PrimaryKey private UUID id;
  private String content;
  private String harvesterIds;
  private String state;

  public Request() {
    this.id = UUID.randomUUID();
  }

  public Request(String content, String harvesterIds, String state) {
    this.id = UUID.randomUUID();
    this.content = content;
    this.harvesterIds = harvesterIds;
    this.state = state;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getHarvesterIds() {
    return harvesterIds;
  }

  public void setHarvesterIds(String harvesterIds) {
    this.harvesterIds = harvesterIds;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
