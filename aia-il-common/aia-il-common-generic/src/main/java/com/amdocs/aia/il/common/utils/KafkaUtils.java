package com.amdocs.aia.il.common.utils;

import com.amdocs.aia.il.common.model.configuration.properties.KafkaConfiguration;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * Kafka utilities.
 */
public final class KafkaUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtils.class);
    public static final String SECURITY_PROTOCOL = "security.protocol";
    private KafkaUtils () {
        // singleton
    }

    public static void setSecurityProperties(final Properties properties, final KafkaConfiguration kafkaConfiguration) {
        final String securityProtocol = kafkaConfiguration.getSecurityProtocol();
        LOGGER.info("Security protocol from config: {}", securityProtocol);
        if (securityProtocol != null) {
            properties.put(SECURITY_PROTOCOL, securityProtocol);
            if (securityProtocol.contains("SSL")) {
                final KafkaConfiguration.Ssl ssl = kafkaConfiguration.getSsl();
                if (ssl != null) {
                    setPropertyNullSafe(properties, SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, ssl.getTruststoreLocation());
                    LOGGER.info("Truststore location: {}", ssl.getTruststoreLocation());
                    setPropertyNullSafe(properties, SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, System.getenv(PublisherUtils.KAFKA_SSL_TRUSTSTORE_PASSWORD));
                    LOGGER.info("Truststore password: {}", System.getenv(PublisherUtils.KAFKA_SSL_TRUSTSTORE_PASSWORD));
                }
            }
            if (securityProtocol.startsWith("SASL_")) {
                // http://kafka.apache.org/10/documentation.html#security_sasl_kerberos
                setPropertyNullSafe(properties, "sasl.mechanism", kafkaConfiguration.getSaslMechanism());
                properties.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, "kafka");
            }
        }
    }

    private static void setPropertyNullSafe(final Properties properties, final String key, final String value) {
        if (StringUtils.hasText(value)) {
            properties.put(key, value);
        }
    }
}