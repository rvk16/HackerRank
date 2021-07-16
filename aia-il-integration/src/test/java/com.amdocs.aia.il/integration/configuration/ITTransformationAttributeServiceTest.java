package com.amdocs.aia.il.integration.configuration;

import com.amdocs.aia.common.core.test.utils.RestAssuredUtils;
import com.amdocs.aia.il.configuration.client.dto.TransformationAttributeDTO;
import com.amdocs.aia.il.integration.BaseIntegrationTest;
import com.amdocs.aia.il.utils.LogUtils;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

import static com.amdocs.aia.common.core.test.utils.RestAssuredUtils.assertGetResponseGet;

public class ITTransformationAttributeServiceTest  extends BaseIntegrationTest {

    @BeforeClass
    public void setUpConf() {
        //Every service operation requires change request to be created
        createChangeRequest("IL Transformation Attribute Changes - Test");
        LogUtils.log("Start running test 'ITTransformationAttributeServiceTest'");
    }

    @Override
    protected File getConfigurationZip(){
        return new File("src/test/resources/data/TransformationConfig.zip");
    }

    @Test
    void GET_TransformationAttributeList_Return200() {
        String schemaKey = "aLDMCustomer";
        String entityKey = "Address";
        final Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-attributes/" + schemaKey + "/entity-stores/" + entityKey, HttpStatus.SC_OK);
        TransformationAttributeDTO[] transformationAttributeDTOS = response.as(TransformationAttributeDTO[].class);
        TransformationAttributeDTO transformationAttributeDTO = Arrays.stream(transformationAttributeDTOS).filter(a -> a.getAttributeKey().equals("addressKey")).findFirst().orElse(null);
        Assert.assertEquals(transformationAttributeDTO.getAttributeName(),"Address Key");
        Assert.assertEquals(transformationAttributeDTO.getType(),"STRING");
        Assert.assertNull(transformationAttributeDTO.getParentSchemaKey());
        Assert.assertNull(transformationAttributeDTO.getParentEntityKey());
        Assert.assertNull(transformationAttributeDTO.getParentAttributeKey());
        Assert.assertTrue(transformationAttributeDTO.isIsRequired());
        transformationAttributeDTO = Arrays.stream(transformationAttributeDTOS).filter(a -> a.getAttributeKey().equals("countryKey")).findFirst().orElse(null);
        Assert.assertEquals(transformationAttributeDTO.getAttributeName(),"Country Key");
        Assert.assertEquals(transformationAttributeDTO.getType(),"STRING");
        Assert.assertEquals(transformationAttributeDTO.getParentSchemaKey(),"aLDMCustomer");
        Assert.assertEquals(transformationAttributeDTO.getParentEntityKey(), "Country");
        Assert.assertEquals(transformationAttributeDTO.getParentAttributeKey(),"countryKey");
        Assert.assertFalse(transformationAttributeDTO.isIsRequired());
        transformationAttributeDTO = Arrays.stream(transformationAttributeDTOS).filter(a -> a.getAttributeKey().equals("addressLine1")).findFirst().orElse(null);
        Assert.assertEquals(transformationAttributeDTO.getAttributeName(),"Address Line 1");
        Assert.assertEquals(transformationAttributeDTO.getType(),"STRING");
        Assert.assertNull(transformationAttributeDTO.getParentSchemaKey());
        Assert.assertNull(transformationAttributeDTO.getParentEntityKey());
        Assert.assertNull(transformationAttributeDTO.getParentAttributeKey());
        Assert.assertFalse(transformationAttributeDTO.isIsRequired());
    }

    @Test
    void GET_TransformationAvailableAttributeList_Return200() {
        String schemaKey = "aLDMCustomer";
        String entityKey = "Customer";
        final Response response = assertGetResponseGet(configurationServiceUrl + configServiceAdditionalUrl + "/transformation-attributes/" + schemaKey + "/" + entityKey, HttpStatus.SC_OK);
        TransformationAttributeDTO[] transformationAttributeDTOS = response.as(TransformationAttributeDTO[].class);
        TransformationAttributeDTO transformationAttributeDTO = Arrays.stream(transformationAttributeDTOS).filter(a -> a.getAttributeKey().equals("customerKey")).findFirst().orElse(null);
        Assert.assertEquals(transformationAttributeDTO.getAttributeName(),"Customer Key");
        Assert.assertEquals(transformationAttributeDTO.getType(),"STRING");
        Assert.assertNull(transformationAttributeDTO.getParentSchemaKey());
        Assert.assertNull(transformationAttributeDTO.getParentEntityKey());
        Assert.assertNull(transformationAttributeDTO.getParentAttributeKey());
        Assert.assertTrue(transformationAttributeDTO.isIsRequired());
        transformationAttributeDTO = Arrays.stream(transformationAttributeDTOS).filter(a -> a.getAttributeKey().equals("countryKey")).findFirst().orElse(null);
        Assert.assertEquals(transformationAttributeDTO.getAttributeName(),"Country Key");
        Assert.assertEquals(transformationAttributeDTO.getType(),"STRING");
        Assert.assertEquals(transformationAttributeDTO.getParentSchemaKey(),"aLDMCustomer");
        Assert.assertEquals(transformationAttributeDTO.getParentEntityKey(), "Country");
        Assert.assertEquals(transformationAttributeDTO.getParentAttributeKey(),"countryKey");
        Assert.assertFalse(transformationAttributeDTO.isIsRequired());
        transformationAttributeDTO = Arrays.stream(transformationAttributeDTOS).filter(a -> a.getAttributeKey().equals("customerLegalName")).findFirst().orElse(null);
        Assert.assertEquals(transformationAttributeDTO.getAttributeName(),"Customer Legal Name");
        Assert.assertEquals(transformationAttributeDTO.getType(),"STRING");
        Assert.assertNull(transformationAttributeDTO.getParentSchemaKey());
        Assert.assertNull(transformationAttributeDTO.getParentEntityKey());
        Assert.assertNull(transformationAttributeDTO.getParentAttributeKey());
        Assert.assertFalse(transformationAttributeDTO.isIsRequired());
    }
}
