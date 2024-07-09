/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.testTemplate.provider;

import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;
import com.oracle.idm.agcs.grc.fn.testTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;

public class AzureAdTemplateProvider extends TemplateProvider {
  @Override
  public RequestTemplateOutput getTemplateOutput(
      ApplicationConfig applicationConfig, ConnectedSystemConfig connectedSystemConfig) {
    System.err.println(
        String.format(
            "start getting test template data from json file for application %s.",
            applicationConfig.getApplication().name()));

    RequestTemplateOutput templateOutputFromResources =
        Util.getTemplateOutputFromResources(applicationConfig.getApplication());

    System.err.println(
        String.format(
            "finished getting test template data from json file for application %s.",
            applicationConfig.getApplication().name()));

    return Util.getTemplateWithAuthorizationToken(
        templateOutputFromResources, applicationConfig, connectedSystemConfig);
  }
}
