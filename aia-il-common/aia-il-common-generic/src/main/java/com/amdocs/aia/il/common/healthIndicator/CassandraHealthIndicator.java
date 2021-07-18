package com.amdocs.aia.il.common.healthIndicator;

import com.amdocs.aia.il.common.stores.scylla.ScyllaConnection;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.metadata.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class CassandraHealthIndicator extends AbstractHealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHealthIndicator.class);
    private static final String DOWN = "DOWN";

    @Override
    protected void doHealthCheck(Builder builder) {
        try {
            ResultSet execute = ScyllaConnection.getSession().execute("SELECT now() FROM system.local");
            Map<UUID, Node> nodes = ScyllaConnection.getSession().getMetadata().getNodes();
            LOGGER.debug("Node Size::{} ", nodes.size());
            for (Node node : nodes.values()) {
                String state = node.getState().toString();
                LOGGER.debug("Node State :: {} and Node Address:: {}", state, node.getBroadcastAddress());
                if (DOWN.equalsIgnoreCase(state)) {
                    throw new RuntimeException("One of node is down");//NOSONAR
                }
            }
            LOGGER.debug("Connecting scylla DB at Custom Health Indicator:: {}", execute);
            builder.up().build();
        } catch (Exception ex) {
            LOGGER.error("Facing issue while connecting scylla DB at Custom Health Indicator:: {}", ex);
            builder.down(ex).build();
        }
    }
}

