package com.amdocs.aia.il.deployer.query;

import org.springframework.stereotype.Component;

/**sss
 * SQL generator.
 *
 * @author ALEXKRA
 */
@Component
public interface QueryGenerator {
    String buildCreateStatement(String table);

    String buildAlterStatement(String table,String tableOption);

    String buildCreateSchemaRelationStatement(String table);
}