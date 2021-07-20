package com.amdocs.aia.il.common.healthIndicator; //NOSONAR

import com.amdocs.aia.il.common.model.configuration.properties.KafkaConfiguration;
import com.amdocs.aia.il.common.utils.KafkaUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Properties;

@Component
public class KafkaHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaHealthIndicator.class);

    KafkaConfiguration kafkaConfiguration;
    AdminClient adminClient;

    @Autowired
    public KafkaHealthIndicator(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBrokers());
        KafkaUtils.setSecurityProperties(properties, kafkaConfiguration);
        adminClient=AdminClient.create(properties);
    }


    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        int nodeCount = kafkaConfiguration.getBrokers().split(",").length;
        LOGGER.debug("Broker count {}", nodeCount);
        try {
            DescribeClusterResult result = adminClient.describeCluster(new DescribeClusterOptions().timeoutMs(3000));
            final Collection<Node> nodes = result.nodes().get();
            LOGGER.debug("Total no of nodes available count ::{}", nodes.size());
            if ((nodes.size() < nodeCount)) {
                throw new RuntimeException("One of the Kafka node is down:"); //NOSONAR
            } else {
                builder.up();
            }
            LOGGER.debug("Inside Custom KafkaHealth Indicator Running Fine.");
        } catch (Exception e) {
            LOGGER.error("Facing issue while connecting Kafka at Custom Health Indicator {}", e.getMessage());
            builder.down();
        }
        return builder.build();
    }
}
