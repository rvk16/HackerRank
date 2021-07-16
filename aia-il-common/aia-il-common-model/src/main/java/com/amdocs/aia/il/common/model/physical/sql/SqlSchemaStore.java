package com.amdocs.aia.il.common.model.physical.sql;

import com.amdocs.aia.il.common.model.physical.AbstractPhysicalSchemaStore;

public class SqlSchemaStore extends AbstractPhysicalSchemaStore {
    private static final long serialVersionUID = 8902691860924648305L;

    public static final String ELEMENT_TYPE = "SqlSchemaStore";

    public SqlSchemaStore() {
        super(ELEMENT_TYPE);
    }
}