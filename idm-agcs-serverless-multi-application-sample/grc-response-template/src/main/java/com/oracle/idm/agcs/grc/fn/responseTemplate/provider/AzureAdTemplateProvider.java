/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.responseTemplate.provider;

import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;

import java.util.Map;

public class AzureAdTemplateProvider extends TemplateProvider {

  @Override
  public ResponseTemplateOutput getTemplateOutput(
      ApplicationConfig applicationConfig,
      String entityName,
      Operation operationName,
      Map<String, String> contextData) {
    return getTemplateOutputFromResources(
        applicationConfig, entityName, operationName, contextData);
  }
}
