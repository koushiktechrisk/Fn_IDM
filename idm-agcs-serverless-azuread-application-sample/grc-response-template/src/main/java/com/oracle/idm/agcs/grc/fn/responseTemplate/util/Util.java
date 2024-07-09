/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.responseTemplate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oracle.idm.agcs.grc.fn.commons.config.Application;
import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.Config;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;
import com.oracle.idm.agcs.grc.fn.commons.exception.BadRequestException;
import com.oracle.idm.agcs.grc.fn.commons.exception.ProcessingFailedException;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.RequestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.ResponseTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {

  public static final String applicationConfigFilePath =
      "/response/applications/<APPLICATION_NAME>/config.yaml";
  public static final String templateJsonFilePath =
      "/response/applications/<APPLICATION_NAME>/<ENTITY_NAME>/<OPERATION_NAME>_TEMPLATE.json";
  public static final String membershipAttributeTemplateJsonFilePath =
      "/response/applications/<APPLICATION_NAME>/<ENTITY_NAME>/<MEMBERSHIP_ATTRIBUTE_NAME>/<OPERATION_NAME>_TEMPLATE.json";
  public static final String applicationNamePlaceHolder = "<APPLICATION_NAME>";
  public static final String entityNamePlaceHolder = "<ENTITY_NAME>";
  public static final String operationNamePlaceHolder = "<OPERATION_NAME>";
  public static final String membershipAttributeNamePlaceHolder = "<MEMBERSHIP_ATTRIBUTE_NAME>";

  public static final ObjectMapper objectMapper = new ObjectMapper();

  private Util() {
    throw new IllegalStateException("Util is a Utility class");
  }

  public static Config getConfigFromYaml(String filePath) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    try {
      return mapper.readValue(Util.class.getResourceAsStream(filePath), Config.class);
    } catch (IOException e) {
      System.err.println(
          "ResponseTemplateFunction configuration initialization is failed while mapping config yaml to config model.");
      throw new ProcessingFailedException(
          "ResponseTemplateFunction configuration initialization is failed while mapping config yaml to config model.",
          e);
    }
  }

  public static List<ApplicationConfig> getApplicationConfigsFromYaml(
      List<String> applicationNames) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    List<ApplicationConfig> applications = new ArrayList<>();
    for (String applicationName : applicationNames) {
      try {
        String configFilePath =
            applicationConfigFilePath.replace(applicationNamePlaceHolder, applicationName);
        applications.add(
            mapper.readValue(
                Util.class.getResourceAsStream(configFilePath), ApplicationConfig.class));
      } catch (IOException e) {
        System.err.println(
            String.format(
                "ResponseTemplateFunction application %s configuration initialization is failed while mapping config yaml to config model.",
                applicationName));
        throw new ProcessingFailedException(
            String.format(
                "ResponseTemplateFunction application %s configuration initialization is failed while mapping config yaml to config model.",
                applicationName),
            e);
      }
    }
    return applications;
  }

  public static ApplicationConfig getApplicationConfigForConnectedSystemName(
      List<ApplicationConfig> applicationConfigs, String connectedSystemName) {
    // validate connectedSystemName is blank or not
    if (null == connectedSystemName || connectedSystemName.trim().length() == 0) {
      System.err.println(
          "ResponseTemplateFunction input is invalid. connectedSystemName is not present.");
      throw new BadRequestException(
          "ResponseTemplateFunction input is invalid. connectedSystemName is not present.");
    }
    // get ApplicationConfiguration corresponding to connectedSystemName
    ApplicationConfig applicationConfiguration = null;
    for (ApplicationConfig applicationConfig : applicationConfigs) {
      if (null != applicationConfig.getConnectedSystemConfigs()
          && !applicationConfig.getConnectedSystemConfigs().isEmpty()) {
        for (ConnectedSystemConfig connectedSystemConfig :
            applicationConfig.getConnectedSystemConfigs()) {
          if (null != connectedSystemConfig
              && null != connectedSystemConfig.getConnectedSystemName()
              && connectedSystemConfig
                  .getConnectedSystemName()
                  .equalsIgnoreCase(connectedSystemName)) {
            applicationConfiguration = applicationConfig;
          }
        }
      }
    }
    // validate applicationConfig corresponding to connectedSystemName is present or not
    if (null == applicationConfiguration) {
      System.err.println(
          String.format(
              "ResponseTemplateFunction input connectedSystemName %s is invalid",
              connectedSystemName));
      throw new BadRequestException(
          String.format(
              "ResponseTemplateFunction input connectedSystemName %s is invalid",
              connectedSystemName));
    }

    return applicationConfiguration;
  }

  public static ResponseTemplateOutput getTemplateOutputFromResources(
      Application application,
      String entityName,
      Operation operationName,
      Map<String, String> contextData) {
    try {
      String jsonFilePath =
          templateJsonFilePath
              .replace(applicationNamePlaceHolder, application.name())
              .replace(entityNamePlaceHolder, entityName)
              .replace(operationNamePlaceHolder, operationName.name());
      if (operationName == Operation.ADD_CHILD_DATA
          || operationName == Operation.REMOVE_CHILD_DATA) {
        jsonFilePath =
            membershipAttributeTemplateJsonFilePath
                .replace(applicationNamePlaceHolder, application.name())
                .replace(entityNamePlaceHolder, entityName)
                .replace(membershipAttributeNamePlaceHolder, contextData.get(operationName.name()))
                .replace(operationNamePlaceHolder, operationName.name());
      }
      return objectMapper.readValue(
          Util.class.getResourceAsStream(jsonFilePath), ResponseTemplateOutput.class);
    } catch (IOException e) {
      System.err.println(
          String.format(
              "ResponseTemplateFunction is failed while mapping response template json to output model for application %s , entityName %s and operationName %s.",
              application.name(), entityName, operationName.name()));
      throw new ProcessingFailedException(
          String.format(
              "ResponseTemplateFunction is failed while mapping response template json to output model for application %s , entityName %s and operationName %s.",
              application.name(), entityName, operationName.name()),
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
      throw new BadRequestException(
          "ResponseTemplateFunction input is invalid. contextData is not present.");
    }
  }
}
