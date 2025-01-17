/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.testTemplate;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.Config;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;
import com.oracle.idm.agcs.grc.fn.testTemplate.manager.TemplateManager;
import com.oracle.idm.agcs.grc.fn.testTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.TestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;

import java.util.List;

public class TestTemplateFunction {

  private static List<ApplicationConfig> applicationConfigs;
  private static String configFilePath = "/test/config.yaml";

  @FnConfiguration
  public void config(RuntimeContext ctx) {
    System.err.println("Start TestTemplateFunction configuration initialization.");
    Config config = Util.getConfigFromYaml(configFilePath);
    applicationConfigs = Util.getApplicationConfigsFromYaml(config.getApplications());
    System.err.println("Finished TestTemplateFunction configuration initialization.");
  }

  public RequestTemplateOutput handleRequest(TestTemplateInput input) {
    System.err.println("TestTemplateFunction input is :: " + input);
    // validate input data and get corresponding application configuration
    ApplicationConfig applicationConfig =
        Util.getApplicationConfigForConnectedSystemName(applicationConfigs, input.getConnectedSystemName());
    ConnectedSystemConfig connectedSystemConfig =
        Util.getConnectedSystemConfigFromApplicationConfig(applicationConfig, input.getConnectedSystemName());
    // get test template output
    RequestTemplateOutput output =
        TemplateManager.getTemplateProvider(applicationConfig.getApplication())
            .getTemplateOutput(applicationConfig, connectedSystemConfig);
    System.err.println("TestTemplateFunction output is :: " + output);
    return output;
  }
}
