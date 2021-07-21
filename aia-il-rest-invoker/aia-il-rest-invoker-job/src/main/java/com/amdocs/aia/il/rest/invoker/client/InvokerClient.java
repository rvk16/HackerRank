package com.amdocs.aia.il.rest.invoker.client;

import com.amdocs.aia.common.core.client.AbstractAiaClient;
import com.amdocs.aia.common.core.web.AiaHttpRequestHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;

@Component
public class InvokerClient extends AbstractAiaClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvokerClient.class);

    public InvokerClient(){
    }

    public <T> ResponseEntity<T> sendRequest(String relativeURL, HttpMethod method, Object requestEntity, Class<T> responseType) {
        return restTemplate.exchange(buildURL(relativeURL), method, new HttpEntity<>(requestEntity, buildHeaders()), responseType);
    }

    public <T> ResponseEntity<T> sendRequest(String relativeURL, HttpMethod method, Object requestEntity, Class<T> responseType, AiaHttpRequestHeaders headers) {
        return restTemplate.exchange(buildURL(relativeURL), method, new HttpEntity<>(requestEntity, buildHeaders()), responseType);
    }

    public <T> ResponseEntity<T> sendRequest(String relativeURL, HttpMethod method, Object requestEntity, ParameterizedTypeReference<T> parameterizedTypeReference) {
        return restTemplate.exchange(buildURL(relativeURL), method, new HttpEntity<>(requestEntity, buildHeaders()), parameterizedTypeReference);
    }

    public <T> ResponseEntity<T> sendRequestMultipart(String relativeURL, HttpMethod method, MultiValueMap<String, Object> requestEntity, Class<T> responseType) {
        final HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return restTemplate.exchange(buildURL(relativeURL), method, new HttpEntity<>(requestEntity, headers), responseType);
    }

    private String buildURL(String url) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Invoker client calling url {}", url);
        }
        return url;
    }

    public static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        MultipartInputStreamFileResource(String filename, InputStream inputStream) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public long contentLength() {
            // we do not want to generally read the whole stream into memory ...
            return -1;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o != null && getClass() == o.getClass()) {
                return super.equals(o);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}