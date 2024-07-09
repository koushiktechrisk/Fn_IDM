/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.responseTemplate;

import com.oracle.idm.agcs.grc.fn.responseTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.ResponseTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;

public class ResponseTemplateFunction {

  public ResponseTemplateOutput handleRequest(ResponseTemplateInput input) {
    System.err.println("ResponseTemplateFunction input is :: " + input);
    // validate input data and get corresponding application configuration
    Util.validateInputContextData(input);
    // get response template output
    ResponseTemplateOutput output =
        Util.getTemplateOutputFromResources(
            input.getEntityName(), input.getOperationName(), input.getContextData());
    System.err.println("ResponseTemplateFunction output is :: " + output);
    return output;
  }
}
