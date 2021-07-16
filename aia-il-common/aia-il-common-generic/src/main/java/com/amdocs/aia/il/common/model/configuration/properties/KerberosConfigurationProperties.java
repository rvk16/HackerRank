package com.amdocs.aia.il.common.model.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Kerberos configuration properties.
 *
 * @author SWARNIMJ
 */
@ConfigurationProperties(prefix = "security")
public class KerberosConfigurationProperties {
    private String authLoginConfig;
    private String krb5Conf;

    public String getAuthLoginConfig() {
        return authLoginConfig;
    }

    public void setAuthLoginConfig(final String authLoginConfig) {
        this.authLoginConfig = authLoginConfig;
    }

    public String getKrb5Conf() {
        return krb5Conf;
    }

    public void setKrb5Conf(final String krb5Conf) {
        this.krb5Conf = krb5Conf;
    }
}