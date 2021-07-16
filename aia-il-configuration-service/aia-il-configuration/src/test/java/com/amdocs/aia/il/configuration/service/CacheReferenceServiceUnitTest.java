package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.repo.*;
import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.configuration.dependencies.CacheEntityDependencyAnalyzer;
import com.amdocs.aia.il.configuration.dto.CacheReferenceAttributeDTO;
import com.amdocs.aia.il.configuration.dto.CacheReferenceEntityDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.mapper.cache.CacheAttributeMapper;
import com.amdocs.aia.il.configuration.mapper.cache.CacheEntityMapper;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import com.amdocs.aia.il.configuration.repository.cache.CacheReferenceRepository;
import com.amdocs.aia.il.configuration.service.cache.CacheReferenceServiceImpl;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CacheReferenceServiceUnitTest {
    private static final String PROJECT_KEY = "aia";

    @InjectMocks
    private CacheReferenceServiceImpl cacheReferenceServiceImpl;
    @Mock
    private CacheReferenceRepository cacheReferenceRepository;
    @Mock
    private CacheEntityMapper cacheEntityMapper;
    @Mock
    private MessageHelper messageHelper;
    @MockBean
    private CacheAttributeMapper cacheAttributeMapper;
    @Mock
    private AiaRepositoryOperations aiaRepositoryOperations;
    @Mock
    UserRepositoryStatusDTO mockUserRepositoryStatusDTO;
    @Mock
    private ChangeRequestDTO mockChangeRequestDTO;

    @Mock
    private CacheEntityDependencyAnalyzer cacheEntityDependencyAnalyzer;

    @Mock
    private AbstractModelService<CacheEntity, com.amdocs.aia.repo.client.AiaProjectElementRepository<CacheEntity>> abstractModelService;

    @BeforeEach
    void before() {
        cacheAttributeMapper = new CacheAttributeMapper();
        cacheEntityMapper = new CacheEntityMapper(cacheAttributeMapper);
    }
    @Test
    void whenGetCacheEntity_ShouldReturnDTO() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);
        when(cacheReferenceRepository.getByKey(PROJECT_KEY, cacheEntity.getEntityKey())).thenReturn(cacheEntity);
        final CacheReferenceEntityDTO cacheReferenceEntityDTO = cacheReferenceServiceImpl.get(PROJECT_KEY, cacheEntity.getEntityKey());
        assertEquals(cacheEntityKey , cacheReferenceEntityDTO.getCacheReferenceEntityKey());
    }

    @Test
    void whenGetCacheEntity_ShouldThrowException() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);
        doReturn(getCacheEntityApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createObjectDoesNotExistException(Mockito.anyString(), Mockito.anyString(),Mockito.anyString());
        final ApiException ex = assertThrows(ApiException.class, () -> cacheReferenceServiceImpl.get(PROJECT_KEY, cacheEntity.getEntityKey()));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenSaveCacheEntity_ShouldReturnDTO() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);

        when(mockUserRepositoryStatusDTO.getActiveChangeRequest()).thenReturn(mockChangeRequestDTO);
        when(aiaRepositoryOperations.getUserStatus()).thenReturn(mockUserRepositoryStatusDTO);
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(any());
        when(cacheReferenceRepository.save(cacheEntity)).thenReturn(cacheEntity);
        when(cacheReferenceRepository.validateBeforeSave(Mockito.any())).thenReturn(new ValidationResponseDTO(ValidationStatus.OK, 0, false, Collections.emptyList()));
        doNothing().when(abstractModelService).validateBeforeSave(cacheEntity);

        final CacheReferenceEntityDTO cacheReferenceEntityDTO = cacheReferenceServiceImpl.save(PROJECT_KEY, createMockForCacheReferenceEntityDTO(cacheEntityKey) );
        assertEquals(cacheEntity.getEntityKey() , cacheReferenceEntityDTO.getCacheReferenceEntityKey());
    }

    @Test
    void whenSaveCacheEntity_ShouldThrowException() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);
        when(cacheReferenceRepository.getByKey(PROJECT_KEY, cacheEntity.getEntityKey())).thenReturn(cacheEntity);
        doReturn(getCacheEntityApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST))
                .when(messageHelper).createObjectAlreadyExistException(Mockito.anyString(), Mockito.anyString());
        final ApiException ex = assertThrows(ApiException.class, () -> cacheReferenceServiceImpl.save(PROJECT_KEY, createMockForCacheReferenceEntityDTO(cacheEntityKey)));
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void whenSaveCacheEntity_DuplicateAttributeKeys_ShouldThrowException() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);
        CacheReferenceEntityDTO cacheReferenceEntityDTO = createMockForCacheReferenceEntityDTO(cacheEntityKey);
        CacheReferenceAttributeDTO cacheReferenceAttributeDTO1 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO1.setAttributeKey("attributeKey1");
        cacheReferenceAttributeDTO1.setAttributeName("attribute Key1");
        cacheReferenceAttributeDTO1.setKeyPosition(1);
        cacheReferenceAttributeDTO1.setType("type");
        cacheReferenceEntityDTO.getCacheReferenceAttributes().add(cacheReferenceAttributeDTO1);

        when(mockUserRepositoryStatusDTO.getActiveChangeRequest()).thenReturn(mockChangeRequestDTO);
        when(aiaRepositoryOperations.getUserStatus()).thenReturn(mockUserRepositoryStatusDTO);
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(any());
        when(cacheReferenceRepository.getByKey(any(), cacheReferenceEntityDTO.getCacheReferenceEntityKey())).thenReturn(null);

        final ApiException ex = assertThrows(ApiException.class, () -> cacheReferenceServiceImpl.save(PROJECT_KEY, cacheReferenceEntityDTO));
        assertEquals(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, ex.getStatusCode());
    }

    @Test
    void whenUpdateCacheEntity_ShouldReturnDTO() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);
        cacheEntity.setDescription("test");
        when(cacheReferenceRepository.getByKey(PROJECT_KEY, cacheEntity.getEntityKey())).thenReturn(cacheEntity);
        final CacheReferenceEntityDTO cacheReferenceEntityDTO = cacheReferenceServiceImpl.get(PROJECT_KEY, cacheEntity.getEntityKey());
        when(cacheReferenceRepository.getByKey(eq(cacheEntity.getProjectKey()), eq(cacheEntity.getEntityKey()))).thenReturn(cacheEntity);
        when(cacheReferenceRepository.validateBeforeSave(any())).thenReturn(new ValidationResponseDTO(ValidationStatus.OK, 0, false, Collections.emptyList()));
        when(mockUserRepositoryStatusDTO.getActiveChangeRequest()).thenReturn(mockChangeRequestDTO);
        when(aiaRepositoryOperations.getUserStatus()).thenReturn(mockUserRepositoryStatusDTO);
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(any());
        when(cacheReferenceRepository.save(cacheEntity)).thenReturn(cacheEntity);
        cacheReferenceServiceImpl.update(cacheEntity.getProjectKey(), cacheEntity.getEntityKey(), cacheReferenceEntityDTO);
        assertEquals("aia", cacheEntity.getProjectKey());
        assertNotNull(cacheEntity);
    }

    @Test
    void whenUpdateCacheEntityNotFound_ShouldThrowException() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheReferenceEntityDTO cacheReferenceEntityDTO = createMockForCacheReferenceEntityDTO(cacheEntityKey);
        doReturn(getCacheEntityApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createElementNotFoundException(Mockito.anyString(), Mockito.anyString());
        final ApiException ex = assertThrows(ApiException.class, () -> cacheReferenceServiceImpl.update(PROJECT_KEY, cacheEntityKey, cacheReferenceEntityDTO ));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void whenGetListCacheEntity_ShouldReturnDTOList() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);
        List<CacheEntity> cacheEntityList = new ArrayList<>(Collections.singletonList(cacheEntity));
        when(cacheReferenceRepository.findByProjectKey(PROJECT_KEY)).thenReturn(cacheEntityList);
        final List<CacheReferenceEntityDTO> cacheReferenceEntityDTOList = cacheReferenceServiceImpl.list(PROJECT_KEY);
        assertEquals(cacheEntityList.get(0).getEntityKey() , cacheReferenceEntityDTOList.get(0).getCacheReferenceEntityKey());
        assertEquals(cacheEntityList.get(0).getAttributes().size() , cacheReferenceEntityDTOList.get(0).getCacheReferenceAttributes().size());
        assertEquals(cacheEntityList.get(0).getDescription() , cacheReferenceEntityDTOList.get(0).getDescription());
    }

    @Test
    void whenDeleteCacheEntity_ShouldReturnStatusOK() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);
        when(cacheReferenceRepository.getByKey(PROJECT_KEY, cacheEntity.getEntityKey())).thenReturn(cacheEntity);
        doNothing().when(aiaRepositoryOperations).delete(cacheEntity.getEntityKey());
        doNothing().when(abstractModelService).validateBeforeDelete(cacheEntity);
        when(cacheReferenceRepository.validateBeforeDelete(Mockito.any())).thenReturn(new ValidationResponseDTO(ValidationStatus.OK, 0, false, Collections.emptyList()));

        cacheReferenceServiceImpl.delete(cacheEntity.getProjectKey(), cacheEntity.getEntityKey());
        assertEquals("aia", cacheEntity.getProjectKey());
        verify(aiaRepositoryOperations, times(1)).delete(cacheEntity.getId());
    }

    @Test
    void whenDeleteCacheEntityNotFound_ShouldThrowException() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        doReturn(getCacheEntityApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST))
                .when(messageHelper).createElementNotFoundException(Mockito.anyString(), Mockito.anyString());
        final ApiException ex = assertThrows(ApiException.class, () -> cacheReferenceServiceImpl.delete(PROJECT_KEY, cacheEntityKey));
        assertEquals(AiaApiException.AiaApiHttpCodes.NOT_FOUND, ex.getStatusCode());
    }



    @Test
    void whenBulkSaveCacheEntities_ShouldReturnSaveElementsResponse() {
        String cacheEntityKey = "cacheEntity";
        cacheReferenceServiceImpl.setMapper(new CacheEntityMapper(cacheAttributeMapper));
        final CacheEntity cacheEntity = createMockForCacheEntityModel(cacheEntityKey);

        when(cacheReferenceRepository.findByProjectKey(PROJECT_KEY)).thenReturn(Collections.emptyList());
        SaveElementsResponse sveElementsResponse = new SaveElementsResponse();
        sveElementsResponse.setSavedElementsCount(1);

        when(mockUserRepositoryStatusDTO.getActiveChangeRequest()).thenReturn(mockChangeRequestDTO);
        when(aiaRepositoryOperations.getUserStatus()).thenReturn(mockUserRepositoryStatusDTO);
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(any());
        when(cacheReferenceRepository.saveElements( Collections.singletonList(cacheEntity))).thenReturn(sveElementsResponse);
        doNothing().when(abstractModelService).validateBeforeSave(cacheEntity);

        final SaveElementsResponseDTO saveElementsResponse = cacheReferenceServiceImpl.bulkSave(PROJECT_KEY, Collections.singletonList(createMockForCacheReferenceEntityDTO(cacheEntityKey)));
        assertEquals(1 , saveElementsResponse.getSavedElementsCount());
    }


    private static CacheEntity createMockForCacheEntityModel(final String cacheEntityKey) {
        final CacheEntity cacheEntity =new CacheEntity();
        cacheEntity.setProjectKey(PROJECT_KEY);
        cacheEntity.setEntityKey(cacheEntityKey);
        cacheEntity.setDescription("Description");
        cacheEntity.setAttributes(getCacheAttributeList());

        return cacheEntity;
    }

    private static List<CacheAttribute> getCacheAttributeList(){
        CacheAttribute cacheAttribute1 = new CacheAttribute();
        cacheAttribute1.setAttributeKey("attributeKey1");
        cacheAttribute1.setName("attribute Key1");
        cacheAttribute1.setKeyPosition(1);
        cacheAttribute1.setDatatype("type");

        CacheAttribute cacheAttribute2 = new CacheAttribute();
        cacheAttribute2.setAttributeKey("attributeKey2");
        cacheAttribute2.setName("attribute Key2");
        cacheAttribute2.setKeyPosition(0);
        cacheAttribute2.setDatatype("type");

        List<CacheAttribute> cacheAttributes = new ArrayList<>();
        cacheAttributes.add(cacheAttribute1);
        cacheAttributes.add(cacheAttribute2);
        return cacheAttributes;
    }

    private static CacheReferenceEntityDTO createMockForCacheReferenceEntityDTO(String cacheReferenceEntityKey) {
        CacheReferenceEntityDTO mockCacheReferenceEntityDTO = new CacheReferenceEntityDTO();
        mockCacheReferenceEntityDTO
                .cacheReferenceEntityKey(cacheReferenceEntityKey)
                .description("description")
                .cacheReferenceAttributes(getCacheReferenceAttributes());

        return mockCacheReferenceEntityDTO;
    }

    private static List<CacheReferenceAttributeDTO> getCacheReferenceAttributes(){
        CacheReferenceAttributeDTO cacheReferenceAttributeDTO1 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO1.setAttributeKey("attributeKey1");
        cacheReferenceAttributeDTO1.setAttributeName("attribute Key1");
        cacheReferenceAttributeDTO1.setKeyPosition(1);
        cacheReferenceAttributeDTO1.setType("type");

        CacheReferenceAttributeDTO cacheReferenceAttributeDTO2 = new CacheReferenceAttributeDTO();
        cacheReferenceAttributeDTO2.setAttributeKey("attributeKey2");
        cacheReferenceAttributeDTO2.setAttributeName("attribute Key2");
        cacheReferenceAttributeDTO2.setKeyPosition(0);
        cacheReferenceAttributeDTO2.setType("type");

        List<CacheReferenceAttributeDTO> cacheReferenceAttributeDTOS = new ArrayList<>();
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO1);
        cacheReferenceAttributeDTOS.add(cacheReferenceAttributeDTO2);

        return cacheReferenceAttributeDTOS;
    }

    private static ApiException getCacheEntityApiException(AiaApiException.AiaApiHttpCodes status, AIAAPIMessageTemplate message) {
        return new ApiException(status, message, "display_name", "element_key");
    }
}
