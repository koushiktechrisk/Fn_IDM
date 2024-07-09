/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.icfconnectors.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ResponseAttribute.ResponseAttributeBuilder.class)
public class ResponseAttribute {

  @JsonProperty("name")
  private String name;

  @JsonProperty("value")
  private String value;

  @JsonProperty("responseOfSubRequestId")
  private String responseOfSubRequestId;

  @JsonProperty("items")
  private String items;

  @JsonProperty("subAttributes")
  private List<ResponseAttribute> subAttributes;
}
