/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra.handler;

import java.util.Map;

/**
 * Handler for the grc record instance where data and attributes validation can happen, before
 * sending it to the requester
 */
public interface GenericRestRecordHandler {
  /**
   * Passes the record from parser to the connector
   *
   * @param record
   * @return true if the parser should keep processing else false to cancel.
   */
  boolean handle(Map<String, Object> record);

  /**
   * Returns the number of records that have been handled by this handler
   *
   * @return
   */
  int getRecordCount();
}
