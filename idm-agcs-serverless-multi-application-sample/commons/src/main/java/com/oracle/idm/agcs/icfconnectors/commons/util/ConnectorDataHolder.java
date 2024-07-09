/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.icfconnectors.commons.util;

import java.util.HashMap;
import java.util.Map;

public class ConnectorDataHolder {
    private static ThreadLocal<Map<ConnectorDataType, Object>> dataHolder = new ThreadLocal<>();

    public static Object getData(ConnectorDataType connectorDataType) {
        if (dataHolder.get() == null) {
            dataHolder.set(new HashMap<>());
        }
        return dataHolder.get().get(connectorDataType);
    }

    public static void setData(ConnectorDataType connectorDataType, Object data) {
        if (dataHolder.get() == null) {
            dataHolder.set(new HashMap<>());
        }
        dataHolder.get().put(connectorDataType, data);
    }
}
