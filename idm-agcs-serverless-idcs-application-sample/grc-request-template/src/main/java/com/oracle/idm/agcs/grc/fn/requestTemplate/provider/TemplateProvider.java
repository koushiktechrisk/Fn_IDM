/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.requestTemplate.provider;

import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;
import com.oracle.idm.agcs.grc.fn.requestTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;

import java.util.Map;

public abstract class TemplateProvider {
  public abstract RequestTemplateOutput getTemplateOutput(
      ApplicationConfig applicationConfig,
      ConnectedSystemConfig connectedSystemConfig,
      String entityName,
      Operation operationName,
      Map<String, String> contextData);

  public RequestTemplateOutput getTemplateOutputWithAuthorizationValue(
      ApplicationConfig applicationConfig,
      ConnectedSystemConfig connectedSystemConfig,
      String entityName,
      Operation operationName,
      Map<String, String> contextData) {
    System.err.println(
        String.format(
            "start getting request template data from json file for application %s , entityName %s and operationName %s.",
            applicationConfig.getApplication().name(), entityName, operationName));

    RequestTemplateOutput templateOutputFromResources =
        Util.getTemplateOutputFromResources(
            applicationConfig.getApplication(), entityName, operationName, contextData);

    System.err.println(
        String.format(
            "finished getting request template data from json file for application %s , entityName %s and operationName %s.",
            applicationConfig.getApplication().name(), entityName, operationName));

    return Util.getTemplateWithAuthorizationToken(
        templateOutputFromResources, applicationConfig, connectedSystemConfig);
  }

  public RequestTemplateOutput getTemplateOutputFromResources(
      ApplicationConfig applicationConfig,
      String entityName,
      Operation operationName,
      Map<String, String> contextData) {
    System.err.println(
        String.format(
            "start getting request template data from json file for application %s , entityName %s and operationName %s.",
            applicationConfig.getApplication().name(), entityName, operationName));

    RequestTemplateOutput templateOutputFromResources =
        Util.getTemplateOutputFromResources(
            applicationConfig.getApplication(), entityName, operationName, contextData);

    System.err.println(
        String.format(
            "finished getting request template data from json file for application %s , entityName %s and operationName %s.",
            applicationConfig.getApplication().name(), entityName, operationName));

    return templateOutputFromResources;
  }
}
