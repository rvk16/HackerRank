package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.configuration.transformation.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContextRepositoryUnitTest {

    @Spy
    @InjectMocks
    private ContextRepository publisherContextRepository;

    @Test
    void when_findAllPublisherContextsByProjectKey_shouldReturn() {
        doReturn(Collections.singletonList(getPublisherContext())).when(publisherContextRepository).findByProperties(Mockito.any());
        List<Context> contexts = publisherContextRepository.findAllPublisherContextsByProjectKey("project_key");
        assertEquals(1, contexts.size());
    }

    @Test
    void when_findPublisherContextByKeys_shouldReturn() {
        Context context = getPublisherContext();
        doReturn(context).when(publisherContextRepository).findOneByProperties(Mockito.any());
        Context actual = publisherContextRepository.findPublisherContextByKeys("project_key", "context_key");

        assertEquals(context.getProjectKey(), actual.getProjectKey());
        assertEquals(context.getOrigin(), actual.getOrigin());
        assertEquals(context.getId(), actual.getId());
        assertEquals(context.getContextKey(), actual.getContextKey());
        assertEquals(context.getPublisherName(), actual.getPublisherName());
    }

    private static Context getPublisherContext() {
        Context context = new Context();
        context.setProjectKey("project_key");
        context.setOrigin("origin");
        context.setId("id");
        context.setContextKey("context_key");
        context.setPublisherName("publisher_name");
        return context;
    }
}