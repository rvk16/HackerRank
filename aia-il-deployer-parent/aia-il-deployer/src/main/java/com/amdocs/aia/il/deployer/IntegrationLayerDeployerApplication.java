package com.amdocs.aia.il.deployer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;

@SpringBootApplication
@ComponentScan(basePackages = {"com.amdocs.aia.il.deployer", "com.amdocs.aia.repo.client"})
public class IntegrationLayerDeployerApplication implements CommandLineRunner, ExitCodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationLayerDeployerApplication.class);

    private final IntegrationLayerDeployer deployer;

    @Autowired
    public IntegrationLayerDeployerApplication(final IntegrationLayerDeployer deployer) {
        this.deployer = deployer;
    }

    @Override
    public void run(final String... args) {
        try {
            deployer.execute();
        } finally {
            System.exit(getExitCode()); // NOSONAR
        }
    }

    public static void main(final String... args) {
        setVmOptions();
        try (final ConfigurableApplicationContext context = new SpringApplicationBuilder(IntegrationLayerDeployerApplication.class)
                .web(WebApplicationType.NONE).run(args)) {
            // empty
        }
    }

    private static void setVmOptions() {
        setProperty("javax.net.ssl.trustStoreType", "TRUSTSTORE_TYPE");
        setProperty("javax.net.ssl.trustStorePassword", "TRUSTSTORE_PASSWORD");

        final String trustStore = System.getenv("TRUSTSTORE_KEYSTORE");
        if (StringUtils.hasText(trustStore)) {
            final String property = "javax.net.ssl.trustStore";
            System.setProperty(property, System.getenv("KEYSTORE_MOUNT") + '/' + trustStore);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("setting VM option: {}", property);
            }
        }
    }

    private static void setProperty(final String property, final String env) {
        final String value = System.getenv(env);
        if (StringUtils.hasText(value)) {
            System.setProperty(property, value);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("setting VM option: {}", property);
            }
        }
    }

    @Override
    public int getExitCode() {
        return deployer.getExitCode();
    }
}