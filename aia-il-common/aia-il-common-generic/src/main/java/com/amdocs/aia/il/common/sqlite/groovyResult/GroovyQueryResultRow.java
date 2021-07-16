package com.amdocs.aia.il.common.sqlite.groovyResult;


import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

import java.util.Map;

public class GroovyQueryResultRow implements GroovyObject {

    public MetaClass getMetaClass() {
        return (MetaClass) null;
    }

    public Object getResultMap() {
        return null;
    }

    public void setResultMap(Object value) {
        // To be implemented
    }

    @Override
    public Object getProperty(String propertyName) {
        return null;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return null;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        // To be implemented
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        // To be implemented
    }

    public Map<String, Object> asMap() {
        return (Map<String, Object>) null;
    }
}
