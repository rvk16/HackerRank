package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.ValidationResponseDTO;
import com.amdocs.aia.common.model.repo.ValidationStatus;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.common.model.Relation;
import com.amdocs.aia.il.configuration.dependencies.EntityReferentialIntegrityDependencyAnalyzer;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.EntityReferentialIntegrityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EntityReferentialIntegrityServiceUnitTest {

    protected static final String PROJECT_KEY = "projectKey";

    @InjectMocks
    private EntityReferentialIntegrityServiceImpl service;

    @Mock
    private EntityReferentialIntegrityRepository repository;

    @Mock
    private EntityReferentialIntegrityDependencyAnalyzer dependencyAnalyzer;

    @Mock
    private MessageHelper messageHelper;

    @Test
    void whenCreateNewEntityReferentialIntegrity_ShouldReturn() {
        EntityReferentialIntegrity model = getEntityReferentialIntegrity();
        when(repository.findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(eq(PROJECT_KEY), eq(model.getLogicalSchemaKey()), eq(model.getLogicalEntityKey()))).thenReturn(null);
        when(repository.validateBeforeSave(Mockito.any())).thenReturn(new ValidationResponseDTO(ValidationStatus.OK, 0, false, Collections.emptyList()));
        when(repository.save(Mockito.any())).thenReturn(model);
        final EntityReferentialIntegrity result = service.createOrUpdate(model);
        assertEquals(model.getLogicalEntityKey(), result.getLogicalEntityKey());
        assertEquals(model.getLogicalSchemaKey(), result.getLogicalSchemaKey());
        assertEquals(model.getRelations().size(), result.getRelations().size());
    }

    @Test
    void whenUpdateExistingEntityReferentialIntegrity_ShouldReturn() {
        EntityReferentialIntegrity model = getEntityReferentialIntegrity();
        when(repository.findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(eq(PROJECT_KEY), eq(model.getLogicalSchemaKey()), eq(model.getLogicalEntityKey()))).thenReturn(model);
        when(repository.validateBeforeSave(Mockito.any())).thenReturn(new ValidationResponseDTO(ValidationStatus.OK, 0, false, Collections.emptyList()));
        EntityReferentialIntegrity updatedModel = getEntityReferentialIntegrity();
        updatedModel.getRelations().get(0).setAttributeKey("updatedAttributeKey");
        updatedModel.getRelations().get(0).setParentAttributeKey("updatedParentAttributeKey");
        updatedModel.getRelations().get(0).setParentEntityKey("updatedParentEntityKey");
        when(repository.save(Mockito.any())).thenReturn(updatedModel);
        final EntityReferentialIntegrity result = service.createOrUpdate(model);
        assertEquals(updatedModel.getLogicalEntityKey(), result.getLogicalEntityKey());
        assertEquals(updatedModel.getLogicalSchemaKey(), result.getLogicalSchemaKey());
        assertEquals(updatedModel.getRelations().size(), result.getRelations().size());
        assertEquals(updatedModel.getRelations().get(0).getAttributeKey(), result.getRelations().get(0).getAttributeKey());
        assertEquals(updatedModel.getRelations().get(0).getParentAttributeKey(), result.getRelations().get(0).getParentAttributeKey());
        assertEquals(updatedModel.getRelations().get(0).getParentEntityKey(), result.getRelations().get(0).getParentEntityKey());
    }


    @Test
    void whenGetEntityReferentialIntegrity_ShouldReturn() {
        EntityReferentialIntegrity model = getEntityReferentialIntegrity();
        when(repository.findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(eq(PROJECT_KEY), eq(model.getLogicalSchemaKey()), eq(model.getLogicalEntityKey()))).thenReturn(model);
        final EntityReferentialIntegrity result = service.get(PROJECT_KEY, model.getLogicalSchemaKey(),
            model.getLogicalEntityKey()).get();
        assertEquals(model.getLogicalEntityKey(), result.getLogicalEntityKey());
        assertEquals(model.getLogicalSchemaKey(), result.getLogicalSchemaKey());
        assertEquals(model.getRelations().size(), result.getRelations().size());
    }

    @Test
    void whenGetEntityReferentialIntegrity_LogicalSchemaKeyInvalidShouldThrowException() {
        when(messageHelper.createIDNotSetException(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(getException1(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,"EntityReferentialIntegrity Logical Schema key doesn't exist"));
        final ApiException ex1 = assertThrows(ApiException.class, () -> service.get(PROJECT_KEY, "", "Customer"));
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex1.getStatusCode());
    }

    @Test
    void whenGetEntityReferentialIntegrity_LogicalEntityKeyInvalidShouldThrowException() {
        when(messageHelper.createIDNotSetException(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(getException1(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,"EntityReferentialIntegrity Logical Entity key doesn't exist"));
        final ApiException ex1 = assertThrows(ApiException.class, () -> service.get(PROJECT_KEY, "aLDMCustomer", ""));
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex1.getStatusCode());
    }

    @Test
    void whenGetEntityReferentialIntegrity_NotExistShouldThrowException() {
        when(repository.findByProjectKeyAndLogicalSchemaKeyAndLogicalEntityKey(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(null);
        when(messageHelper.createIDNotSetException(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(getException1(AiaApiException.AiaApiHttpCodes.NOT_FOUND,"EntityReferentialIntegrity doesn't exist"));
        final ApiException ex1 = assertThrows(ApiException.class, () -> service.get(PROJECT_KEY, "aLDMCustomer", ""));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex1.getStatusCode());
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

    private ApiException getException1(AiaApiException.AiaApiHttpCodes rerquestCode, String msg) {
        return new ApiException(rerquestCode, AiaApiMessages.GENERAL.ID_NOT_SET,
                "test",
                msg);
    }


}
