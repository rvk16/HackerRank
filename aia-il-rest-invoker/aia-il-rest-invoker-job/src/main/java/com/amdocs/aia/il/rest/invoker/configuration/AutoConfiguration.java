package com.amdocs.aia.il.rest.invoker.configuration;

import com.amdocs.aia.common.model.extensions.typesystems.TypeSystemFactory;
import com.amdocs.aia.common.spring.monitor.MetricsConfiguration;
import com.amdocs.aia.il.rest.invoker.InvokerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Locale;

/**
 * Auto configuration.
 */
@Configuration
@ComponentScan(basePackageClasses = {InvokerApplication.class, TypeSystemFactory.class, MetricsConfiguration.class},
        basePackages = {"com.amdocs.aia.common.core.web"})
@EnableConfigurationProperties({InvokerConfiguration.class})
@EnableScheduling
public class AutoConfiguration { // NOSONAR
    static {
        final Logger logger = LoggerFactory.getLogger(AutoConfiguration.class); // NOSONAR
        if (logger.isInfoEnabled()) {
            final StringBuilder sb = new StringBuilder("Java Runtime Environment");
            sb.append("\n    Version: ").append(System.getProperty("java.version"));
            sb.append("\n    Vendor: ").append(System.getProperty("java.vendor"));
            sb.append("\n    Default locale: ").append(Locale.getDefault().getDisplayName());
            sb.append("\nOperating system");
            sb.append("\n    Name: ").append(System.getProperty("os.name"));
            sb.append("\n    Architecture: ").append(System.getProperty("os.arch"));
            sb.append("\n    Version: ").append(System.getProperty("os.version"));
            sb.append("\nJava Virtual Machine");
            sb.append("\n    Number of available processors: ").append(Runtime.getRuntime().availableProcessors());
            sb.append("\n    Total amount of memory in bytes: ").append(Runtime.getRuntime().totalMemory());
            logger.info(sb.toString());
        }
    }
}