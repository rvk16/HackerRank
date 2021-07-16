package com.amdocs.aia.il.configuration.service;

import java.util.List;

/**
 * @param <D> DTO type
 */
public interface ConfigurationService<D> {

    D get(final String projectKey, final String key);

    List<D> list(final String projectKey);

    D save(final String projectKey, final D s);

    D update(final String projectKey, final String key, final D s);

    void delete(String projectKey, String key);

}