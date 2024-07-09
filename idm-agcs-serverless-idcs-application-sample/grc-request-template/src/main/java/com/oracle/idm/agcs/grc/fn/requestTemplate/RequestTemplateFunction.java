/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.requestTemplate;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.Config;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;
import com.oracle.idm.agcs.grc.fn.requestTemplate.manager.TemplateManager;
import com.oracle.idm.agcs.grc.fn.requestTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.RequestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;

import java.util.List;

public class RequestTemplateFunction {
  private static List<ApplicationConfig> applicationConfigs;
  private static String configFilePath = "/request/config.yaml";

  @FnConfiguration
  public void config(RuntimeContext ctx) {
    System.err.println("Start RequestTemplateFunction configuration initialization.");
    Config config = Util.getConfigFromYaml(configFilePath);
    applicationConfigs = Util.getApplicationConfigsFromYaml(config.getApplications());
    System.err.println("Finished RequestTemplateFunction configuration initialization.");
  }

  public RequestTemplateOutput handleRequest(RequestTemplateInput input) {
    System.err.println("RequestTemplateFunction input is :: " + input);
    // validate input data and get corresponding application configuration
    Util.validateInputContextData(input);
    ApplicationConfig applicationConfig =
        Util.getApplicationConfigForConnectedSystemName(
            applicationConfigs, input.getConnectedSystemName());
    ConnectedSystemConfig connectedSystemConfig =
        Util.getConnectedSystemConfigFromApplicationConfig(
            applicationConfig, input.getConnectedSystemName());
    // get request template output
    RequestTemplateOutput output =
        TemplateManager.getTemplateProvider(applicationConfig.getApplication())
            .getTemplateOutput(
                applicationConfig,
                connectedSystemConfig,
                input.getEntityName(),
                input.getOperationName(),
                input.getContextData());
    System.err.println("RequestTemplateFunction output is :: " + output);
    return output;
  }
}
