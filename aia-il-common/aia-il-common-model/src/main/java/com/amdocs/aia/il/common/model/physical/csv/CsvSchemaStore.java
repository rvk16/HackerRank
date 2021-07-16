package com.amdocs.aia.il.common.model.physical.csv;

import com.amdocs.aia.il.common.model.physical.AbstractPhysicalSchemaStore;

/**
 * CSV schema store.
 *
 * @author ALEXKRA
 */
public class CsvSchemaStore extends AbstractPhysicalSchemaStore {
    private static final long serialVersionUID = 7486219834452615969L;

    public static final String ELEMENT_TYPE = "CsvSchemaStore";

    public CsvSchemaStore() {
        super(ELEMENT_TYPE);
    }
}