/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.schemaTemplate;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.Config;
import com.oracle.idm.agcs.grc.fn.schemaTemplate.manager.TemplateManager;
import com.oracle.idm.agcs.grc.fn.schemaTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.SchemaTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.SchemaTemplateOutput;

import java.util.List;

public class SchemaTemplateFunction {

  private static List<ApplicationConfig> applicationConfigs;
  private static String configFilePath = "/schema/config.yaml";

  @FnConfiguration
  public void config(RuntimeContext ctx) {
    System.err.println("Start SchemaTemplateFunction configuration initialization.");
    Config config = Util.getConfigFromYaml(configFilePath);
    applicationConfigs = Util.getApplicationConfigsFromYaml(config.getApplications());
    System.err.println("Finished SchemaTemplateFunction configuration initialization.");
  }

  public SchemaTemplateOutput handleRequest(SchemaTemplateInput input) {
    System.err.println("SchemaTemplateFunction input is :: " + input);
    // validate input data and get corresponding application configuration
    ApplicationConfig applicationConfig =
        Util.getApplicationConfigForConnectedSystemName(applicationConfigs, input.getConnectedSystemName());
    // get schema template output
    SchemaTemplateOutput output =
        TemplateManager.getTemplateProvider(applicationConfig.getApplication())
            .getTemplateOutput(applicationConfig);
    System.err.println("SchemaTemplateFunction output is :: " + output);
    return output;
  }
}
