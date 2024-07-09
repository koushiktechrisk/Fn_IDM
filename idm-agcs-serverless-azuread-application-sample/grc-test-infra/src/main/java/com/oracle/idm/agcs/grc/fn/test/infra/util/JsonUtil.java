/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {
  private JsonUtil() {
    throw new IllegalStateException("Utility class.");
  }

  public static String getJsonPathFromItem(String itemJsonPath) {
    if (itemJsonPath == null) {
      return null;
    }
    String pattern = "<JP>(.*)<\\/JP>";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(itemJsonPath);
    if (m.find()) {
      return m.group(1);
    }
    System.out.println("Items must be enclosed in <JP></JP> tags.Incorrect path : " + itemJsonPath);
    return null;
  }
}
