/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.responseTemplate;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.Config;
import com.oracle.idm.agcs.grc.fn.responseTemplate.manager.TemplateManager;
import com.oracle.idm.agcs.grc.fn.responseTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.ResponseTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;

import java.util.List;

public class ResponseTemplateFunction {
  private static List<ApplicationConfig> applicationConfigs;
  private static String configFilePath = "/response/config.yaml";

  @FnConfiguration
  public void config(RuntimeContext ctx) {
    System.err.println("Start ResponseTemplateFunction configuration initialization.");
    Config config = Util.getConfigFromYaml(configFilePath);
    applicationConfigs = Util.getApplicationConfigsFromYaml(config.getApplications());
    System.err.println("Finished ResponseTemplateFunction configuration initialization.");
  }

  public ResponseTemplateOutput handleRequest(ResponseTemplateInput input) {
    System.err.println("ResponseTemplateFunction input is :: " + input);
    // validate input data and get corresponding application configuration
    Util.validateInputContextData(input);
    ApplicationConfig applicationConfig =
        Util.getApplicationConfigForConnectedSystemName(
            applicationConfigs, input.getConnectedSystemName());
    // get response template output
    ResponseTemplateOutput output =
        TemplateManager.getTemplateProvider(applicationConfig.getApplication())
            .getTemplateOutput(
                applicationConfig,
                input.getEntityName(),
                input.getOperationName(),
                input.getContextData());
    System.err.println("ResponseTemplateFunction output is :: " + output);
    return output;
  }
}
