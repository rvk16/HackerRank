package com.amdocs.aia.il.busdeployer.configurations;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration properties.
 *
 * @author MAYURIBH
 */
@Configuration
public class RuntimeConfiguration {

    @Value("${aia.repo.elements.local.path}")
    private String repoElementsLocalPath;


    public String getRepoElementsLocalPath() {
        return repoElementsLocalPath;
    }

    public void setRepoElementsLocalPath(final String repoElementsLocalPath) {
        this.repoElementsLocalPath = repoElementsLocalPath;
    }

}