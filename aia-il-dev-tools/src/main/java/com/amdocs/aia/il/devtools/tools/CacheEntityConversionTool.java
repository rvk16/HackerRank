package com.amdocs.aia.il.devtools.tools;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.extensions.typesystems.*;
import com.amdocs.aia.common.model.logical.LogicalAttribute;
import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.il.common.model.*;
import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.common.model.configuration.transformation.Transformation;
import com.amdocs.aia.il.common.model.publisher.PublisherCacheEntityStore;
import com.amdocs.aia.il.common.model.publisher.PublisherCacheSchemaStore;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE;

public class CacheEntityConversionTool extends AbstractConfigZipConversionTool {
    private final static Logger LOGGER = LoggerFactory.getLogger(CacheEntityConversionTool.class);


    private Map<String, PublisherCacheEntityStore> publisherCacheEntitiesByLogicalKey;
    private Map<String, Transformation> transformationByTargetEntityStoreKey;
    private List<LogicalEntity> logicalEntities;
    private Map<String, EntityStore> entityStoreByLogicalKey;



    public CacheEntityConversionTool(String sourceZip, String targetZip) {
        super(sourceZip, targetZip);
    }

    @Override
    protected void doConversions() {
        loadLogicalEntities();
        loadPublisherCacheEntityStore();
        loadEntityStore();
        loadTransformation();
        createCacheEntitiesAndDeleteFromShared();
        deletePublisherCacheSchemaStore();
        deletePublisherCacheEntityStore();
    }

    @Override
    protected boolean shouldPackMetadata() {
        return false;
    }

    private void loadLogicalEntities() {
        logicalEntities =  getElementsProvider()
                .getElements(ModelConstants.SHARED_PRODUCT_KEY, LogicalEntity.ELEMENT_TYPE_CODE, LogicalEntity.class);

    }

    private void loadPublisherCacheEntityStore() {
        publisherCacheEntitiesByLogicalKey = getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, PublisherCacheEntityStore.class.getSimpleName(), PublisherCacheEntityStore.class)
                .stream()
                .collect(Collectors.toMap(PublisherCacheEntityStore::getLogicalEntityKey, Function.identity()));
    }
    private void loadEntityStore() {
        entityStoreByLogicalKey = getElementsProvider()
                .getElements(ModelConstants.SHARED_PRODUCT_KEY, EntityStore.ELEMENT_TYPE_CODE, EntityStore.class)
                .stream()
                .collect(Collectors.toMap(EntityStore::getLogicalEntityKey, Function.identity()));
    }

    private void loadTransformation() {
        transformationByTargetEntityStoreKey = getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, Transformation.class.getSimpleName(), Transformation.class)
                .stream()
                .collect(Collectors.toMap(Transformation::getTargetEntityStoreKey, Function.identity()));
    }


    private void createCacheEntitiesAndDeleteFromShared() {
        logicalEntities.stream().filter(logicalEntity -> publisherCacheEntitiesByLogicalKey.containsKey(logicalEntity.getEntityKey()))
                .forEach( logicalEntity -> {
                    createCacheEntity(logicalEntity);
                    updateTransformation(transformationByTargetEntityStoreKey.get(logicalEntity.getEntityKey()));
                    deleteElement(logicalEntity);
                    deleteElement(entityStoreByLogicalKey.get(logicalEntity.getEntityKey()));
                });
    }



    private void deletePublisherCacheSchemaStore() {
        getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, PublisherCacheSchemaStore.class.getSimpleName(), PublisherCacheSchemaStore.class)
                .stream()
                .forEach(this::deleteElement);
    }


    private void deletePublisherCacheEntityStore() {
        getElementsProvider()
                .getElements(ConfigurationConstants.PRODUCT_KEY, PublisherCacheEntityStore.class.getSimpleName(), PublisherCacheEntityStore.class)
                .stream()
                .forEach(this::deleteElement);
    }




    private void createCacheEntity(LogicalEntity logicalEntity) {
        CacheEntity cacheEntity = new CacheEntity();
        cacheEntity.setEntityKey(logicalEntity.getEntityKey());
        cacheEntity.setProjectKey(logicalEntity.getProjectKey());
        cacheEntity.setOriginProcess(logicalEntity.getOriginProcess());
        cacheEntity.setDescription(logicalEntity.getDescription());
        cacheEntity.setElementType(CacheEntity.ELEMENT_TYPE);
        cacheEntity.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        cacheEntity.setAttributes(logicalEntity.getAttributes().stream().map( logicalAttribute -> createAttribute(logicalAttribute)).collect(Collectors.toList()));
        cacheEntity.setId(EntityConfigurationUtils.getElementId(cacheEntity));
        saveElement(cacheEntity);
    }


    private void updateTransformation(Transformation transformation) {
        transformation.setTargetSchemaName(CACHE);
        saveElement(transformation);
    }


    private CacheAttribute createAttribute(LogicalAttribute logicalAttribute) {
        CacheAttribute cacheAttribute = new CacheAttribute();
        cacheAttribute.setAttributeKey(logicalAttribute.getAttributeKey());
        cacheAttribute.setName(logicalAttribute.getName());
        cacheAttribute.setDatatype(LogicalTypeSystem.format(logicalAttribute.getDatatype()));
        cacheAttribute.setKeyPosition(logicalAttribute.getKeyPosition());
        cacheAttribute.setAttributeKey(logicalAttribute.getAttributeKey());
        cacheAttribute.setDescription(logicalAttribute.getDescription());

        return cacheAttribute;
    }



    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            LOGGER.error("Usage: java {} <inputRoot> <outputRoot> <relativePathsTextFile>", CacheEntityConversionTool.class.getName());
            System.exit(1);
        }
        String inputRoot = args[0];
        String outputRoot = args[1];

        final List<String> lines = Files.readAllLines(Paths.get(args[2]));
        for(String relativePath : lines) {
            if (relativePath.isEmpty() || !relativePath.endsWith(".zip")) {
                LOGGER.info("Read empty line or invalid zip file name - quitting process");
                break;
            }
            File sourceZip = new File(inputRoot, relativePath);
            File targetZip = new File(outputRoot, relativePath);

            if (!sourceZip.exists()) {
                if (targetZip.exists()) {
                    LOGGER.info("File {} does not exist - copying target file {} to {}", sourceZip, targetZip, sourceZip);
                    FileUtils.copyFile(targetZip, sourceZip);
                } else {
                    throw new IllegalArgumentException("Source file " + sourceZip + " does not exist and cannot be copied from the target as well since target zip " + targetZip + " does not exist as well");
                }
            }
            LOGGER.info("Converting {}", relativePath);
            new CacheEntityConversionTool(sourceZip.getAbsolutePath(), targetZip.getAbsolutePath()).convertSingleZip();
        }
    }
}
