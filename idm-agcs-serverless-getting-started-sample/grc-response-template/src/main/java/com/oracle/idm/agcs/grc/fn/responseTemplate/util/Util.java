/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.responseTemplate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.ResponseTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;

import java.io.IOException;
import java.util.Map;

public class Util {

  public static final String templateJsonFilePath =
      "/response/applications/idcs/<ENTITY_NAME>/<OPERATION_NAME>_TEMPLATE.json";
  public static final String membershipAttributeTemplateJsonFilePath =
      "/response/applications/idcs/<ENTITY_NAME>/<MEMBERSHIP_ATTRIBUTE_NAME>/<OPERATION_NAME>_TEMPLATE.json";
  public static final String entityNamePlaceHolder = "<ENTITY_NAME>";
  public static final String operationNamePlaceHolder = "<OPERATION_NAME>";
  public static final String membershipAttributeNamePlaceHolder = "<MEMBERSHIP_ATTRIBUTE_NAME>";

  public static final ObjectMapper objectMapper = new ObjectMapper();

  private Util() {
    throw new IllegalStateException("Util is a Utility class");
  }

  public static ResponseTemplateOutput getTemplateOutputFromResources(
      String entityName, Operation operationName, Map<String, String> contextData) {
    try {
      String jsonFilePath =
          templateJsonFilePath
              .replace(entityNamePlaceHolder, entityName)
              .replace(operationNamePlaceHolder, operationName.name());
      if (operationName == Operation.ADD_CHILD_DATA
          || operationName == Operation.REMOVE_CHILD_DATA) {
        jsonFilePath =
            membershipAttributeTemplateJsonFilePath
                .replace(entityNamePlaceHolder, entityName)
                .replace(membershipAttributeNamePlaceHolder, contextData.get(operationName.name()))
                .replace(operationNamePlaceHolder, operationName.name());
      }
      return objectMapper.readValue(
          Util.class.getResourceAsStream(jsonFilePath), ResponseTemplateOutput.class);
    } catch (IOException e) {
      System.err.println(
          String.format(
              "ResponseTemplateFunction is failed while mapping response template json to output model for entityName %s and operationName %s.",
              entityName, operationName.name()));
      throw new RuntimeException(
          String.format(
              "ResponseTemplateFunction is failed while mapping response template json to output model for entityName %s and operationName %s.",
              entityName, operationName.name()),
          e);
    }
  }

  public static void validateInputContextData(ResponseTemplateInput input) {
    if (null != input.getOperationName()
        && (input.getOperationName() == Operation.ADD_CHILD_DATA
            || input.getOperationName() == Operation.REMOVE_CHILD_DATA)
        && (null == input.getContextData()
            || input.getContextData().isEmpty()
            || null == input.getContextData().get(input.getOperationName().name()))) {
      System.err.println("ResponseTemplateFunction input is invalid. contextData is not present.");
      throw new RuntimeException(
          "ResponseTemplateFunction input is invalid. contextData is not present.");
    }
  }
}
