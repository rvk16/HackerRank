package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.common.model.Relation;
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
public class EntityReferentialIntegrityRepositoryUnitTest {

    protected static final String PROJECT_KEY = "projectKey";

    @Spy
    @InjectMocks
    private  EntityReferentialIntegrityRepository entityReferentialIntegrityRepository;

    @Test
    void when_findAllEntityReferentialIntegrityByProjectKey_shouldReturn() {
        doReturn(Collections.singletonList(getEntityReferentialIntegrity())).when(entityReferentialIntegrityRepository).findByProjectKey(Mockito.any());
        List<EntityReferentialIntegrity> entityReferentialIntegrities = entityReferentialIntegrityRepository.findByProjectKey(PROJECT_KEY);
        assertEquals(1, entityReferentialIntegrities.size());
    }

    @Test
    void when_findEntityReferentialIntegrityProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey_shouldReturn() {
        EntityReferentialIntegrity entityReferentialIntegrity = getEntityReferentialIntegrity();
        doReturn(entityReferentialIntegrity).when(entityReferentialIntegrityRepository).findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(Mockito.any(), Mockito.any(), Mockito.any());
        EntityReferentialIntegrity result = entityReferentialIntegrityRepository.findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(PROJECT_KEY, entityReferentialIntegrity.getLogicalSchemaKey(), entityReferentialIntegrity.getLogicalEntityKey());
        assertEquals(entityReferentialIntegrity.getLogicalSchemaKey(), result.getLogicalSchemaKey());
        assertEquals(entityReferentialIntegrity.getLogicalEntityKey(), result.getLogicalEntityKey());
        assertEquals(1, result.getRelations().size());
        assertEquals(entityReferentialIntegrity.getRelations().get(0).getAttributeKey(), result.getRelations().get(0).getAttributeKey());
        assertEquals(entityReferentialIntegrity.getRelations().get(0).getParentAttributeKey(), result.getRelations().get(0).getParentAttributeKey());
        assertEquals(entityReferentialIntegrity.getRelations().get(0).getParentEntityKey(), result.getRelations().get(0).getParentEntityKey());
        assertEquals(entityReferentialIntegrity.getRelations().get(0).getParentSchemaKey(), result.getRelations().get(0).getParentSchemaKey());
    }

    private static EntityReferentialIntegrity getEntityReferentialIntegrity() {
        final EntityReferentialIntegrity model = new EntityReferentialIntegrity();
        model.projectKey(PROJECT_KEY)
        .setLogicalSchemaKey("aLDMCustomer")
        .setLogicalEntityKey("Customer");

        model.addRelationsItem(new Relation().setAttributeKey("mainContactKey")
                .setParentAttributeKey("contactKey")
                .setParentEntityKey("Contact")
                .setParentSchemaKey("aLDMCustomer"));
        return model;
    }
}
