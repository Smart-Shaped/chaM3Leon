package com.smartshaped.chameleon.harvester.request;

import com.smartshaped.chameleon.common.utils.TableModel;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Request extends TableModel {

  UUID id;
  String state;
  String content;
  String harvesterIds;

  @Override
  protected String choosePrimaryKey() {
    return "id";
  }
}
