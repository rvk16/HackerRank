package com.amdocs.aia.il.common.sqlite.groovyResult;


import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

import java.util.Iterator;

public class GroovyQueryResultSet implements Iterable<GroovyQueryResultRow>, GroovyObject {

    public GroovyQueryResultSet(IQueryResultSet queryResultSet) {
        // To Be implemented
    }

    public MetaClass getMetaClass() {
        return (MetaClass) null;
    }

    public void setMetaClass(MetaClass mc) {
        // To Be implemented
    }

    public Object invokeMethod(String method, Object arguments) {
        return null;
    }

    public Object getProperty(String property) {
        return null;
    }

    public void setProperty(String property, Object value) {
        // To Be implemented
    }

    public IQueryResultSet getQueryResultSet() {
        return null;
    }

    public void setQueryResultSet(IQueryResultSet value) {
        // To Be implemented
    }

    @Override()
    public Iterator iterator() {
        return (Iterator) null;
    }
}
