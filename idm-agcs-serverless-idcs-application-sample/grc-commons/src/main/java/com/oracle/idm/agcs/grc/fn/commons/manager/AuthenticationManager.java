/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.commons.manager;

import com.oracle.idm.agcs.grc.fn.commons.config.Application;
import com.oracle.idm.agcs.grc.fn.commons.exception.BadRequestException;
import com.oracle.idm.agcs.grc.fn.commons.provider.AuthenticationProvider;
import com.oracle.idm.agcs.grc.fn.commons.provider.IDCSAuthenticationProvider;

public class AuthenticationManager {

  public static AuthenticationProvider getAuthenticationProvider(Application provider) {
    switch (provider) {
      case idcs:
        return new IDCSAuthenticationProvider();
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
