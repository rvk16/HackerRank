package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.il.common.model.configuration.properties.DatabaseConfigurationProperties;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.internal.core.connection.ExponentialReconnectionPolicy;
import com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy;
import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ConditionalOnExpression("'${aia.il.transformer.publisherType}'=='SCYLLA' || '${aia.il.replicator.publisherType}' == 'SCYLLA' || '${aia.il.bulk.transformer.publisherType}' == 'SCYLLA' || '${aia.il.bulk.replicator.publisherType}' == 'SCYLLA' || '${aia.il.compute.leading.key.publisherType}' == 'SCYLLA' ")
public class ScyllaConnection implements Serializable {
    private static final long serialVersionUID = 5349176675212442829L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScyllaConnection.class);

    private static CqlSession session;
    private static CompletionStage<CqlSession> completionStage;

    @Autowired
    private DatabaseConfigurationProperties props;

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @PostConstruct
    public void initScyllaSession() {
        initializeCQLSession(props, meterRegistry);
    }

    public static void initScyllaSession(final DatabaseConfigurationProperties props) {
        initializeCQLSession(props, null);
    }

    private static void initializeCQLSession(DatabaseConfigurationProperties props, MeterRegistry meterRegistry) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Initializing scylla connection with below properties");
            LOGGER.info(props.toString());
        }
        final Collection<InetSocketAddress> addresses = Stream.of(props.getConnStrings().split(","))
                .map(host -> InetSocketAddress.createUnresolved(host, props.getPort())).collect(Collectors.toList());

        ProgrammaticDriverConfigLoaderBuilder loader = DriverConfigLoader.programmaticBuilder();
        setPoolingOptions(loader, props);
        setReconnectionPolicy(loader, props);
        setQueryOptions(loader, props);
        setSocketOptions(loader, props);
        setRetryOptions(loader, props);
        setLoadBalancingPolicy(loader);
        setPageSize(loader, props);
        setMetricsOptions(loader, props);

        final CqlSessionBuilder builder = CqlSession.builder().addContactPoints(addresses)
                .withLocalDatacenter(props.getLocalDatacenter())
                .withKeyspace(props.getKeyspace())
                .withConfigLoader(loader.build());

        if (meterRegistry != null) {
            builder.withMetricRegistry(meterRegistry);
        }
        if (props.getUser() != null) {
            builder.withAuthCredentials(props.getUser(), props.getPassword());
        }
        if (props.isSSLEnabled()) {
            try {
                builder.withSslContext(SSLContext.getDefault());
            } catch (final NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        completionStage = builder.buildAsync();
        session = CompletableFutures.getUninterruptibly(completionStage);
    }

    private static void setMetricsOptions(ProgrammaticDriverConfigLoaderBuilder loader, DatabaseConfigurationProperties props) {
        LOGGER.debug("Initialized DB Metrics");
        setIfNotNull(loader, DefaultDriverOption.METRICS_FACTORY_CLASS, "MicrometerMetricsFactory");
        loader.withStringList(DefaultDriverOption.METRICS_SESSION_ENABLED,Arrays.asList(props.getSessionMetrics().split(",")));
        loader.withStringList(DefaultDriverOption.METRICS_NODE_ENABLED,Arrays.asList(props.getNodeMetrics().split(",")));
    }

    private static void setSocketOptions(ProgrammaticDriverConfigLoaderBuilder loader, DatabaseConfigurationProperties props) {
        setIfNotNull(loader, DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, String.valueOf(props.getConnectionTimeOut()));
        setIfNotNull(loader, DefaultDriverOption.REQUEST_TIMEOUT, String.valueOf(props.getReadTimeOut()));
    }

    private static void setRetryOptions(ProgrammaticDriverConfigLoaderBuilder loader, DatabaseConfigurationProperties props) {
        ScyllaRetryPolicy.setRetryConfig(props.getRetryConfig());
        ScyllaRetryPolicy.setMaxNumOfRetry(props.getNumOfRetry());
        loader.withClass(DefaultDriverOption.RETRY_POLICY_CLASS, ScyllaRetryPolicy.class);
    }

    private static void setLoadBalancingPolicy(ProgrammaticDriverConfigLoaderBuilder loader) {
        loader.withClass(DefaultDriverOption.LOAD_BALANCING_POLICY_CLASS, DefaultLoadBalancingPolicy.class);
    }

    private static void setPageSize(ProgrammaticDriverConfigLoaderBuilder loader, DatabaseConfigurationProperties props) {
        setIfNotNull(loader, DefaultDriverOption.REQUEST_PAGE_SIZE, String.valueOf(props.getPageSize()));
    }

    private static void setReconnectionPolicy(ProgrammaticDriverConfigLoaderBuilder loader, DatabaseConfigurationProperties props) {
        loader.withClass(DefaultDriverOption.RECONNECTION_POLICY_CLASS, ExponentialReconnectionPolicy.class);
        setIfNotNull(loader, DefaultDriverOption.RECONNECTION_BASE_DELAY, String.valueOf(props.getBaseDelayMs()));
        setIfNotNull(loader, DefaultDriverOption.RECONNECTION_MAX_DELAY, String.valueOf(props.getMaxDelayMs()));
    }

    public static void setPoolingOptions(ProgrammaticDriverConfigLoaderBuilder loader, final DatabaseConfigurationProperties props) {
        setIfNotNull(loader, DefaultDriverOption.CONNECTION_POOL_LOCAL_SIZE, String.valueOf(props.getMaxLocalConnPerHost()));
        setIfNotNull(loader, DefaultDriverOption.CONNECTION_POOL_REMOTE_SIZE, String.valueOf(props.getMaxRemoteConnPerHost()));
        // there is no local/remote option
        setIfNotNull(loader, DefaultDriverOption.CONNECTION_MAX_REQUESTS, String.valueOf(props.getMaxLocalRequestPerConnection()));
        setIfNotNull(loader, DefaultDriverOption.HEARTBEAT_INTERVAL, String.valueOf(props.getHeartbeatIntervalSeconds()));
    }

    public static void setIfNotNull(ProgrammaticDriverConfigLoaderBuilder loader, DefaultDriverOption option, String value) {
        if (StringUtils.hasText(value) && !"0".equals(value)) {
            loader.withString(option, value);
        }
    }

    public static void setQueryOptions(ProgrammaticDriverConfigLoaderBuilder loader, final DatabaseConfigurationProperties props) {
        setIfNotNull(loader, DefaultDriverOption.REQUEST_CONSISTENCY, props.getConsistencyLevel().toUpperCase());
        setIfNotNull(loader, DefaultDriverOption.REQUEST_DEFAULT_IDEMPOTENCE, "true");
    }

    public static CqlSession getSession() {
        return session;
    }

    public static CompletionStage<CqlSession> getCompletionStage() {
        return completionStage;
    }

    public DatabaseConfigurationProperties getProps() {
        return props;
    }

    public void setProps(DatabaseConfigurationProperties props) {
        this.props = props;
    }
}