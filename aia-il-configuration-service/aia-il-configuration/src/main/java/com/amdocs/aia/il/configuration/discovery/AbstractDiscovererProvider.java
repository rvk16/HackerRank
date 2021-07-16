package com.amdocs.aia.il.configuration.discovery;

import javax.inject.Inject;
import javax.inject.Provider;

public abstract class AbstractDiscovererProvider<T extends ExternalModelDiscoverer> implements DiscovererProvider<T> {
    private Provider<T> provider;

    @Inject
    public void setProvider(Provider<T> provider) {
        this.provider = provider;
    }

    @Override
    public T get() {
        return provider.get();
    }
}
