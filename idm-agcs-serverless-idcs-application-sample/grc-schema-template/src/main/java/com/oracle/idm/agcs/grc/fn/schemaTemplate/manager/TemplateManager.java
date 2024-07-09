/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.schemaTemplate.manager;

import com.oracle.idm.agcs.grc.fn.commons.config.Application;
import com.oracle.idm.agcs.grc.fn.commons.exception.BadRequestException;
import com.oracle.idm.agcs.grc.fn.schemaTemplate.provider.IDCSTemplateProvider;
import com.oracle.idm.agcs.grc.fn.schemaTemplate.provider.TemplateProvider;

public class TemplateManager {

  public static TemplateProvider getTemplateProvider(Application provider) {
    switch (provider) {
      case idcs:
        return new IDCSTemplateProvider();
      default:
        System.err.println(
            String.format(
                "input provider %s is invalid corresponding TemplateProvider not yet implemented",
                provider.name()));
        throw new BadRequestException(
            String.format(
                "input provider %s is invalid corresponding TemplateProvider not yet implemented",
                provider.name()));
    }
  }
}
