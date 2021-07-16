package com.amdocs.aia.il.deployer.query;


import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
public abstract class AbstractQueryGenerator implements QueryGenerator {

    /**
     * according to ANSI SQL standards, adding quotes to an object name (table, column), ensures that the original case is kept
     * (otherwise, some database implementation may change the name to be uppercase/lowercase).
     * HOWEVER, FOR NOW, we decide that we don't keep the original case, regardless of the database policies
     */
    public AbstractQueryGenerator() { // NOSONAR
    }
    protected static String toIdentifierName(String objectName) {
        return objectName;
    }


}