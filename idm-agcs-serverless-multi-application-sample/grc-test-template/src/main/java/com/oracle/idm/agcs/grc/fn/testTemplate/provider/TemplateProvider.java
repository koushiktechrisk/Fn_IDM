/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.testTemplate.provider;

import com.oracle.idm.agcs.grc.fn.commons.config.ApplicationConfig;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;

public abstract class TemplateProvider {
  public abstract RequestTemplateOutput getTemplateOutput(
      ApplicationConfig applicationConfig, ConnectedSystemConfig connectedSystemConfig);
}
