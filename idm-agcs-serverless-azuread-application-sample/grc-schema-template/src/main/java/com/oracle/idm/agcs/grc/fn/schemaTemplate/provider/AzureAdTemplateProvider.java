/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.schemaTemplate.provider;

import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.schemaTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.SchemaTemplateOutput;

public class AzureAdTemplateProvider extends TemplateProvider {

  @Override
  public SchemaTemplateOutput getTemplateOutput(ApplicationConfig applicationConfig) {
    System.err.println(
        String.format(
            "start getting schema template data from json file for application %s.",
            applicationConfig.getApplication().name()));

    SchemaTemplateOutput templateOutputFromResources =
        Util.getTemplateOutputFromResources(applicationConfig.getApplication());

    System.err.println(
        String.format(
            "finished getting schema template data from json file for application %s.",
            applicationConfig.getApplication().name()));

    return templateOutputFromResources;
  }
}
