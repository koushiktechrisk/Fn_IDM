/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra.handler.impl;

import com.oracle.idm.agcs.grc.fn.test.infra.handler.GenericRestRecordHandler;

import java.util.Map;

public class GenericRestRecordHandlerTestImpl implements GenericRestRecordHandler {

  private static int progressCheckPoint = 10;

  private int recordCount;

  public GenericRestRecordHandlerTestImpl() {
    this.recordCount = 0;
  }

  @Override
  public boolean handle(Map<String, Object> record) {
    recordCount++;
    System.out.println("record in TestHandler :: " + record);
    // we can validate data here as well.
    if ((recordCount % progressCheckPoint) == 0) {
      System.out.println("****** NO. OF RECORDS PROCESSED :: " + recordCount + " ********");
    }
    return true;
  }

  @Override
  public int getRecordCount() {
    return recordCount;
  }
}
