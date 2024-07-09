/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.schemaTemplate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.SchemaTemplateOutput;

import java.io.IOException;

public class Util {

  public static final String templateJsonFilePath = "/schema/applications/idcs/TEMPLATE.json";
  public static final ObjectMapper objectMapper = new ObjectMapper();

  private Util() {
    throw new IllegalStateException("Util is a Utility class");
  }

  public static SchemaTemplateOutput getTemplateOutputFromResources() {
    try {
      return objectMapper.readValue(
          Util.class.getResourceAsStream(templateJsonFilePath), SchemaTemplateOutput.class);
    } catch (IOException e) {
      System.err.println(
          "SchemaTemplateFunction is failed while mapping schema template json to output model");
      throw new RuntimeException(
          "SchemaTemplateFunction is failed while mapping schema template json to output model", e);
    }
  }
}
