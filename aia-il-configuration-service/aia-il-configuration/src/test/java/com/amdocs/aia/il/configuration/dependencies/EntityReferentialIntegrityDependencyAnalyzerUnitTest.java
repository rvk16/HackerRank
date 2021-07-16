package com.amdocs.aia.il.configuration.dependencies;

import com.amdocs.aia.common.model.logical.AttributeRelation;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.logical.LogicalAttribute;
import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.properties.PropertyData;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.common.model.Relation;
import com.amdocs.aia.il.configuration.shared.AiaSharedProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EntityReferentialIntegrityDependencyAnalyzerUnitTest {

    protected static final String PROJECT_KEY = "projectKey";

    @Mock
    private AiaSharedProxy aiaSharedProxy;

    @InjectMocks
    private EntityReferentialIntegrityDependencyAnalyzer entityReferentialIntegrityDependencyAnalyzer;

    @Test
    void whenGetEntityReferentialIntegrityDependency_shouldReturnDependency() {
        EntityReferentialIntegrity entityReferentialIntegrity = getEntityReferentialIntegrity();
        LogicalEntity logicalEntity = createLogicalEntityForTest();
        when(aiaSharedProxy.searchLogicalEntityByEntityKeyAndSchemaKey(eq(PROJECT_KEY), Mockito.any(),Mockito.any())).thenReturn(Optional.of(logicalEntity));
        List<ElementDependency> dependencies = entityReferentialIntegrityDependencyAnalyzer.getDependencies(entityReferentialIntegrity);
        assertEquals(2, dependencies.size());
        assertEquals(entityReferentialIntegrity.getLogicalEntityKey(), dependencies.get(0).getElementName());
        assertTrue(dependencies.get(0).getFeatureKeys().contains(entityReferentialIntegrity.getRelations().get(0).getAttributeKey()));assertEquals(entityReferentialIntegrity.getLogicalEntityKey(), dependencies.get(0).getElementName());
        assertTrue(dependencies.get(1).getFeatureKeys().contains(entityReferentialIntegrity.getRelations().get(0).getParentAttributeKey()));
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

    private LogicalEntity createLogicalEntityForTest(){
        final LogicalAttribute logicalAttribute1 = new LogicalAttribute();
        logicalAttribute1.setAttributeKey("mainContactKey");
        logicalAttribute1.setName("Main Contact Key");
        logicalAttribute1.setKeyPosition(1);
        Datatype datatype1 = new Datatype();
        datatype1.setDatatypeKey("STRING");
        logicalAttribute1.setDatatype(datatype1);
        logicalAttribute1.setSortOrder(1000);
        AttributeRelation attributeRelation = new AttributeRelation();
        attributeRelation.setParentSchemaKey("aLDMCustomer");
        attributeRelation.setParentEntityKey("Contact");
        attributeRelation.setParentAttributeKey("contactKey");
        logicalAttribute1.setAttributeRelation(attributeRelation);

        final LogicalAttribute logicalAttribute2 = new LogicalAttribute();
        logicalAttribute2.setAttributeKey("bssModificationDate");
        logicalAttribute2.setName("BSS Modification Date");
        logicalAttribute2.setKeyPosition(null);
        Datatype datatype2 = new Datatype();
        datatype2.setDatatypeKey("TIMESTAMP");
        logicalAttribute2.setDatatype(datatype2);
        logicalAttribute2.setAttributeRelation(null);

        final LogicalAttribute logicalAttribute3 = new LogicalAttribute();
        logicalAttribute3.setAttributeKey("forTest");
        logicalAttribute3.setName("for test not in attributeStores");
        logicalAttribute3.setKeyPosition(null);
        logicalAttribute3.setDatatype(datatype2);
        logicalAttribute3.setAttributeRelation(null);

        final List<LogicalAttribute> logicalAttributes = new ArrayList<>();
        logicalAttributes.add(logicalAttribute1);
        logicalAttributes.add(logicalAttribute2);
        logicalAttributes.add(logicalAttribute3);

        final LogicalEntity logicalEntity = new LogicalEntity();
        logicalEntity.setEntityKey("Customer");
        logicalEntity.setAttributes(logicalAttributes);
        logicalEntity.setEntityType("Master");
        logicalEntity.setName("Customer");
        logicalEntity.setSchemaKey("aLDMCustomer");
        logicalEntity.setId("SHARED_aia_LGE_Customer");

        return logicalEntity;
    }
}
