/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.schemaTemplate;

import com.oracle.idm.agcs.grc.fn.schemaTemplate.util.Util;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.SchemaTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.SchemaTemplateOutput;

public class SchemaTemplateFunction {

  public SchemaTemplateOutput handleRequest(SchemaTemplateInput input) {
    System.err.println("SchemaTemplateFunction input is :: " + input);
    // get schema template output
    SchemaTemplateOutput output =
            Util.getTemplateOutputFromResources();
    System.err.println("SchemaTemplateFunction output is :: " + output);
    return output;
  }
}
