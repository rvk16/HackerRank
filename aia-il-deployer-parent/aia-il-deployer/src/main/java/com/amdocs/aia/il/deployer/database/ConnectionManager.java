package com.amdocs.aia.il.deployer.database;

import com.amdocs.aia.il.deployer.exception.DeployerException;
import com.amdocs.aia.il.deployer.properties.DatabaseProperties;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private final DatabaseProperties databaseProperties;
    private CqlSession session;

    public ConnectionManager(final DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    public void init() {
        session = initCassandraCluster(databaseProperties);
    }

    public CqlSession getSession() {
        if (session == null) {
            init();
        }
        return session;
    }

    public String getKeyspace() {
        return databaseProperties.getKeyspace();
    }

    private static CqlSession initCassandraCluster(final DatabaseProperties databaseProperties) {
        final Collection<InetSocketAddress> addresses = Stream.of(databaseProperties.getConnStrings().split(","))
                .map(host -> InetSocketAddress.createUnresolved(host, databaseProperties.getNodePort())).collect(Collectors.toList());
        final CqlSessionBuilder builder = CqlSession.builder().addContactPoints(addresses)
                .withLocalDatacenter(databaseProperties.getLocalDatacenter());
        if (databaseProperties.getUsername() != null) {
            builder.withAuthCredentials(databaseProperties.getUsername(), databaseProperties.getPassword());
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("SSL enabled: {}", databaseProperties.isSSLEnabled());
        }
        if (databaseProperties.isSSLEnabled()) {
            try {
                builder.withSslContext(SSLContext.getDefault());
            } catch (final NoSuchAlgorithmException e) {
                throw new DeployerException(e);
            }
        }
        return builder.build();
    }
}