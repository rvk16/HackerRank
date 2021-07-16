package com.amdocs.aia.il.configuration.discovery.csv;

import com.amdocs.aia.il.configuration.discovery.csv.ExternalCsvDiscoveryConfigurationProperties.DatatypePattern;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Used when injecting from properties (no need when using yaml)
 */
@Component
@ConfigurationPropertiesBinding
public class DatatypePatternBinding implements Converter<String, DatatypePattern> {
    @Override
    public DatatypePattern convert(String source) {
        final String[] tokens = source.split(":");
        return new DatatypePattern(tokens[0], tokens[1]);
    }
}
