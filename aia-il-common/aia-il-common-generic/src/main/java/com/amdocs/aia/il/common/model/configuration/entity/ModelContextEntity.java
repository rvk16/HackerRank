package com.amdocs.aia.il.common.model.configuration.entity;

/*
 * @author SWARNIMJ
 */

import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.model.configuration.tables.TableInfoConfiguration;
import com.amdocs.aia.il.common.model.configuration.transformation.*;
import com.amdocs.aia.il.common.reference.table.AbstractReferenceTableInfo;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ModelContextEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelContextEntity.class);
    private final Map<String, DataProcessingContext> dataProcessingContexts = new HashMap<>();
    private final String namespace;
    private Map<String, PublishEntity> publishEntity;
    private Context context;
    boolean isNonTransient;
    boolean isBulkFlow;
    private Map<String,String> schemaNameToDataChannelMap = new HashMap<>();

    public ModelContextEntity(String namespace) {
        this.namespace = namespace;
    }

    public void put(Context context, Map<String, Transformation> transformations, Map<String, PublishEntity> publishEntity, Map<String, RandomAccessTable> tableNameToRandomAccessTableMap,
                    Map<String, ConfigurationRow> tableNameToTableConfigurationRowMap, Map<String, TargetEntity> targetEntities, Map<String, AbstractReferenceTableInfo> referenceTables,
                    Map<String, Map<String, DataProcessingContext>> dataProcessingContextsPerDataChannel, boolean isNonTransient, Map<String, String> schemaNameToDataChannelMap, boolean isBulkFlow) {
        this.context = context;
        this.publishEntity = publishEntity;
        this.isNonTransient = isNonTransient;
        this.isBulkFlow = isBulkFlow;
        this.schemaNameToDataChannelMap = schemaNameToDataChannelMap;
        if (this.context.getContextKey().equalsIgnoreCase("Address")) {
            LOGGER.info("STOP");
        }

        // Creating dataProcessing contexts and also registering relations
        for (ContextEntity contextEntity : context.getContextEntities()) {
            if (!ContextEntityRelationType.REF.equals(contextEntity.getRelationType())) {
                registerContextRelations(contextEntity, tableNameToRandomAccessTableMap, tableNameToTableConfigurationRowMap, dataProcessingContextsPerDataChannel);
            }
        }
        // Register referenceTables for REF contexts
        for (ContextEntity contextEntity : context.getContextEntities()) {
            if (ContextEntityRelationType.REF.equals(contextEntity.getRelationType())) {
                this.registerReferenceTables(contextEntity, referenceTables);
            }
        }

        // Registering the Publishing Query and Publishing Groovy
        transformations.values().stream()
                .filter(each -> TransformationSourceType.CONTEXT.equals(each.getSourceType()) && context.getKey().equals(each.getContextKey()))
                .forEach(transformation -> this.registerPublishingQueryAndGroovy(transformation, targetEntities));
    }


    private void registerReferenceTables(ContextEntity contextEntity, Map<String, AbstractReferenceTableInfo> refTableInfoes) {
        DataProcessingContext dataProcessingContext = dataProcessingContexts.get(context.getContextKey());
        AbstractReferenceTableInfo refTableInfo = refTableInfoes.get(contextEntity.getEntityStoreKey());
        dataProcessingContext.addReferenceTableInfo(refTableInfo);
    }

    private void registerContextRelations(ContextEntity publisherModelContextEntity, Map<String, RandomAccessTable> tableNameToRandomAccessTableMap, Map<String, ConfigurationRow> tableNameToTableConfigurationRowMap, Map<String, Map<String, DataProcessingContext>> dataProcessingContextsPerDataChannel) {
        String dataChannelName = publisherModelContextEntity.getSchemaStoreKey();
        DataProcessingContextRelation relation = convertToDataProcessingContextRelation(publisherModelContextEntity, tableNameToTableConfigurationRowMap);
        DataProcessingContext dataProcessingContext = dataProcessingContexts.get(relation.getDataProcessingContextName());
        if (dataProcessingContext == null) {
            dataProcessingContext = new DataProcessingContext(relation.getDataProcessingContextName());
            dataProcessingContexts.put(relation.getDataProcessingContextName(), dataProcessingContext);
        }
        dataProcessingContext.addRelation(relation);

        computeDataProcessingContextsPerDataChannel(dataChannelName, dataProcessingContext, dataProcessingContextsPerDataChannel);

        ConfigurationRow tableDetailFromMap = tableNameToTableConfigurationRowMap.get(relation.getTableInfo());
        ConfigurationRow parentTableDetailFromMap;
        switch (relation.getType()) {
            case ROOT:
                break;
            case ONE_TO_ONE:
                // add the relation is case of bulk flow to for de-duplication logic in bulk transformer.
                if(isBulkFlow){
                    tableNameToRandomAccessTableMap.get(relation.getTableInfo()).registerFkIndex(tableDetailFromMap.getIdFields(), relation.getTableInfo(), relation.getDataProcessingContextName(), relation.getParentRelation().getTableInfo(), tableDetailFromMap.getIdFields(), ParentChildRelationType.PARENT.toString(),true,false,relation.getTableInfo(),false);
                    tableNameToRandomAccessTableMap.get(relation.getTableInfo()).registerFkIndex(tableDetailFromMap.getIdFields(), relation.getParentRelation().getTableInfo(), relation.getDataProcessingContextName(), relation.getTableInfo(), tableDetailFromMap.getIdFields(), ParentChildRelationType.CHILD.toString(),false,false,"",false);
                }
                break;
            case ONE_TO_MANY:
                tableNameToRandomAccessTableMap.get(relation.getTableInfo()).registerFkIndex(tableDetailFromMap.getIdFields(), relation.getTableInfo(), relation.getDataProcessingContextName(), relation.getParentRelation().getTableInfo(), relation.getKeyColumns(), ParentChildRelationType.PARENT.toString(),false,false,"",false);
                tableNameToRandomAccessTableMap.get(relation.getTableInfo()).registerFkIndex(relation.getKeyColumns(), relation.getParentRelation().getTableInfo(), relation.getDataProcessingContextName(), relation.getTableInfo(), tableDetailFromMap.getIdFields(), ParentChildRelationType.CHILD.toString(),true,false,relation.getParentRelation().getTableInfo(),false);
                break;
            case MANY_TO_ONE:
                parentTableDetailFromMap = tableNameToTableConfigurationRowMap.get(relation.getParentRelation().getTableInfo());
                if (relation.getPropagationMode().equals(RelationPropagationMode.PROPAGATE)) {
                    tableNameToRandomAccessTableMap.get(relation.getParentRelation().getTableInfo()).registerFkIndex(parentTableDetailFromMap.getIdFields(), relation.getParentRelation().getTableInfo(), relation.getDataProcessingContextName(), relation.getTableInfo(), relation.getParentKeyColumns(), ParentChildRelationType.CHILD.toString(),false,true,"",false);
                    tableNameToRandomAccessTableMap.get(relation.getParentRelation().getTableInfo()).registerFkIndex(relation.getParentKeyColumns(), relation.getTableInfo(), relation.getDataProcessingContextName(), relation.getParentRelation().getTableInfo(), parentTableDetailFromMap.getIdFields(), ParentChildRelationType.PARENT.toString(),true,true,relation.getTableInfo(),false);
                }
                break;
            case MANY_TO_MANY:
                parentTableDetailFromMap = tableNameToTableConfigurationRowMap.get(relation.getParentRelation().getTableInfo());
                String strRelTable = relation.getParentRelation().getTableInfo() + '_' + relation.getParentKeyColumns().getName();
                String strTableName = relation.getTableInfo() + '_' + relation.getKeyColumns().getName();

                String strParentRelTable = relation.getTableInfo() + '_' + relation.getKeyColumns().getName();
                String strParentTableName = relation.getParentRelation().getTableInfo() + '_' + relation.getParentKeyColumns().getName();

                tableNameToRandomAccessTableMap.get(relation.getTableInfo()).registerFkIndex(relation.getKeyColumns(), strTableName, relation.getDataProcessingContextName(), relation.getTableInfo(), tableDetailFromMap.getIdFields(), ParentChildRelationType.CHILD.toString(),true,publisherModelContextEntity.isDoPropagation(),strRelTable,true);
                tableNameToRandomAccessTableMap.get(relation.getParentRelation().getTableInfo()).registerFkIndex(parentTableDetailFromMap.getIdFields(), relation.getParentRelation().getTableInfo(), relation.getDataProcessingContextName(), strParentRelTable, relation.getParentKeyColumns(), ParentChildRelationType.CHILD.toString(),false,publisherModelContextEntity.isDoPropagation(),"",true);
                if (relation.getPropagationMode().equals(RelationPropagationMode.PROPAGATE)) {
                    tableNameToRandomAccessTableMap.get(relation.getParentRelation().getTableInfo()).registerFkIndex(relation.getParentKeyColumns(), strParentTableName, relation.getDataProcessingContextName(), relation.getParentRelation().getTableInfo(), parentTableDetailFromMap.getIdFields(), ParentChildRelationType.PARENT.toString(),true,publisherModelContextEntity.isDoPropagation(),strParentRelTable,true);
                    tableNameToRandomAccessTableMap.get(relation.getTableInfo()).registerFkIndex(tableDetailFromMap.getIdFields(), relation.getTableInfo(), relation.getDataProcessingContextName(), strRelTable, relation.getKeyColumns(), ParentChildRelationType.PARENT.toString(),false,publisherModelContextEntity.isDoPropagation(),"",true);
                }
                break;
            default:
                break;
        }
    }

    private void computeDataProcessingContextsPerDataChannel(String dataChannelName, DataProcessingContext dataProcessingContext, Map<String, Map<String, DataProcessingContext>> dataProcessingContextsPerDataChannel) {
        Map<String, DataProcessingContext> dataProcessingContextPerContext = dataProcessingContextsPerDataChannel.get(dataChannelName);
        if (dataProcessingContextPerContext == null) {
            dataProcessingContextPerContext = new HashMap<>();
            dataProcessingContextPerContext.put(context.getContextKey(), dataProcessingContext);
        } else {
            if (!dataProcessingContextPerContext.containsKey(context.getContextKey())) {
                dataProcessingContextPerContext.put(context.getContextKey(), dataProcessingContext);
            }
        }
        dataProcessingContextsPerDataChannel.put(dataChannelName, dataProcessingContextPerContext);
    }

    public Map<String, DataProcessingContext> getDataProcessingContexts() {
        return dataProcessingContexts;
    }

    /**
     * Convert an EntityRelationConfig to EntityRelation
     *
     * @param publisherContextEntity
     * @return EntityRelation
     */
    public DataProcessingContextRelation convertToDataProcessingContextRelation(ContextEntity publisherContextEntity, Map<String, ConfigurationRow> tableNameToTableConfigurationRowMap) {
        DataProcessingContextRelation contextRelation = null;
        try {
            Optional<ContextEntity> leadEntity = context.getContextEntities().stream()
                    .filter(publisherContextEntity1 -> ContextEntityRelationType.LEAD.equals(publisherContextEntity1.getRelationType())).findFirst();
            ContextEntity leading = null;

            if (leadEntity.isPresent()) {
                leading = leadEntity.get();
            } else {
                throw new RuntimeException("Leading table info is not present for context : " + context.getContextKey());
            }

            switch (publisherContextEntity.getRelationType()) {
                case LEAD:
                    contextRelation = getLEADContextRelation(publisherContextEntity, tableNameToTableConfigurationRowMap);
                    break;
                case OTO:
                    ContextEntity otoParent = getParentRelation(publisherContextEntity);
                    String otoName = getRelationName(leading, publisherContextEntity);
                    contextRelation = getOTOContextRelation(otoName, otoParent, tableNameToTableConfigurationRowMap, publisherContextEntity);
                    break;
                case OTM:
                    ContextEntity otmParent = getParentRelation(publisherContextEntity);
                    String otmName = getRelationName(leading, publisherContextEntity) + '_' + StringUtils.join(StringUtils.split(publisherContextEntity.getForeignKeys()), "_");
                    contextRelation = getOTMContextRelation(otmName, otmParent, tableNameToTableConfigurationRowMap, publisherContextEntity);
                    break;
                case MTO:
                    ContextEntity mtoParent = getParentRelation(publisherContextEntity);
                    String mtoName = getRelationName(leading, publisherContextEntity) + '_' + StringUtils.join(StringUtils.split(publisherContextEntity.getForeignKeys()), "_");
                    contextRelation = getMTOContextRelation(mtoName, mtoParent, tableNameToTableConfigurationRowMap, publisherContextEntity);
                    break;
                case MTM:
                    ContextEntity mtmParent = getParentRelation(publisherContextEntity);
                    String mtmName = getRelationName(leading, publisherContextEntity) + '_' + StringUtils.join(StringUtils.split(publisherContextEntity.getForeignKeys()), "_");
                    contextRelation = getMTMContextRelation(mtmName, mtmParent, tableNameToTableConfigurationRowMap, publisherContextEntity);
                    break;
                case REF:
                default:
                    break;

            }
        } catch (Exception ex) {
            throw ex;
        }
        return contextRelation;
    }

    private DataProcessingContextRelation getMTMContextRelation(String mtmName, ContextEntity mtmParent, Map<String, ConfigurationRow> tableNameToTableConfigurationRowMap, ContextEntity publisherContextEntity) {
        DataProcessingContextRelation contextRelation = new DataProcessingContextRelation(mtmName,
                context.getContextKey(),
                convertToDataProcessingContextRelation(mtmParent, tableNameToTableConfigurationRowMap),
                publisherContextEntity.getEntityStoreKey(),
                tableNameToTableConfigurationRowMap.get(publisherContextEntity.getEntityStoreKey()),
                RelationType.MANY_TO_MANY,
                getRelationPropagationMode(publisherContextEntity),
                getForeignKeysList(publisherContextEntity, false),
                getForeignKeysList(publisherContextEntity, true),
                null,
                null,
                getRelationMode(publisherContextEntity.getNoReferentAction()));

        return contextRelation;
    }

    private DataProcessingContextRelation getLEADContextRelation(ContextEntity publisherContextEntity, Map<String, ConfigurationRow> tableNameToTableConfigurationRowMap) {
        DataProcessingContextRelation contextRelation = new DataProcessingContextRelation(context.getContextKey() + '_' + publisherContextEntity.getEntityStoreKey(),
                context.getContextKey(),
                null,
                publisherContextEntity.getEntityStoreKey(),
                tableNameToTableConfigurationRowMap.get(publisherContextEntity.getEntityStoreKey()),
                RelationType.ROOT,
                RelationPropagationMode.NONE,
                null,
                null,
                null,
                null,
                RelationMandatoryMode.MANDATORY);
        return contextRelation;
    }

    private DataProcessingContextRelation getOTOContextRelation(String otoName, ContextEntity otoParent, Map<String,
            ConfigurationRow> tableNameToTableConfigurationRowMap, ContextEntity publisherContextEntity){
        DataProcessingContextRelation contextRelation = new DataProcessingContextRelation(otoName,
                context.getContextKey(),
                convertToDataProcessingContextRelation(otoParent, tableNameToTableConfigurationRowMap),
                publisherContextEntity.getEntityStoreKey(),
                tableNameToTableConfigurationRowMap.get(publisherContextEntity.getEntityStoreKey()),
                RelationType.ONE_TO_ONE,
                getRelationPropagationMode(publisherContextEntity),
                null,
                null,
                null,
                null,
                getRelationMode(publisherContextEntity.getNoReferentAction()));

        return contextRelation;
    }

    private DataProcessingContextRelation getOTMContextRelation(String otmName, ContextEntity otmParent, Map<String,
            ConfigurationRow> tableNameToTableConfigurationRowMap, ContextEntity publisherContextEntity){
        DataProcessingContextRelation contextRelation = new DataProcessingContextRelation(otmName,
                context.getContextKey(),
                convertToDataProcessingContextRelation(otmParent, tableNameToTableConfigurationRowMap),
                publisherContextEntity.getEntityStoreKey(),
                tableNameToTableConfigurationRowMap.get(publisherContextEntity.getEntityStoreKey()),
                RelationType.ONE_TO_MANY,
                getRelationPropagationMode(publisherContextEntity),
                getForeignKeysList(publisherContextEntity, false),
                null,
                null,
                null,
                getRelationMode(publisherContextEntity.getNoReferentAction()));

        return contextRelation;
    }

    private  DataProcessingContextRelation getMTOContextRelation(String mtoName, ContextEntity mtoParent, Map<String, ConfigurationRow> tableNameToTableConfigurationRowMap, ContextEntity publisherContextEntity){
        DataProcessingContextRelation contextRelation = new DataProcessingContextRelation(mtoName,
                context.getContextKey(),
                convertToDataProcessingContextRelation(mtoParent, tableNameToTableConfigurationRowMap),
                publisherContextEntity.getEntityStoreKey(),
                tableNameToTableConfigurationRowMap.get(publisherContextEntity.getEntityStoreKey()),
                RelationType.MANY_TO_ONE,
                getRelationPropagationMode(publisherContextEntity),
                null,
                getForeignKeysList(publisherContextEntity, true),
                null,
                null,
                getRelationMode(publisherContextEntity.getNoReferentAction()));

        return contextRelation;
    }
    public String getRelationName(ContextEntity leading, ContextEntity publisherContextEntity) {
        String relationName = "";
        if (leading != null) {
            relationName = leading.getEntityStoreKey() + '_' + publisherContextEntity.getEntityStoreKey();
        }
        return relationName;
    }

    public RelationPropagationMode getRelationPropagationMode(ContextEntity publisherContextEntity) {
        return publisherContextEntity.isDoPropagation() ? RelationPropagationMode.PROPAGATE : RelationPropagationMode.NONE;
    }

    public ContextEntity getParentRelation(ContextEntity publisherContextEntity) {
        ContextEntity parentRelation = null;
        Optional<ContextEntity> value = context.getContextEntities().stream().
                filter(publisherContextEntity1 -> publisherContextEntity1.getEntityStoreKey().equals(getTableName(publisherContextEntity.getParentContextEntityKey()))).findFirst();

        if (value.isPresent()) {
            parentRelation = value.get();
        }
        return parentRelation;
    }

    public List<ColumnConfiguration> getForeignKeysList(ContextEntity publisherContextEntity, boolean isParent) {
        List<String> foreignKeysStrings = Arrays.stream(StringUtils.split(publisherContextEntity.getForeignKeys(), ",")).map(String::trim).distinct().collect(Collectors.toList());
        return getColumnConfigurations(publisherContextEntity, isParent, foreignKeysStrings);
    }

    private List<ColumnConfiguration> getColumnConfigurations(ContextEntity publisherContextEntity, boolean isParent, List<String> foreignKeysStrings) {
        List<String> foreignKeys;
        if (ContextEntityRelationType.MTM.equals(publisherContextEntity.getRelationType())) {
            foreignKeys = getForeignKeysMTM(publisherContextEntity, foreignKeysStrings, isParent);
        } else {
            foreignKeys = foreignKeysStrings;
        }
        if (isParent) {
            ContextEntity parentEntity = getParentRelation(publisherContextEntity);
            return getColumnConfigurations(foreignKeys, parentEntity);
        } else {
            return getColumnConfigurations(foreignKeys, publisherContextEntity);
        }
    }

    private List<ColumnConfiguration> getColumnConfigurations(List<String> foreignKeys, ContextEntity parentEntity) {
        if (parentEntity != null) {
            TableInfoConfiguration parentConfiguration = getTableConfiguration(parentEntity.getSchemaStoreKey(), parentEntity.getEntityStoreKey());
            if (parentConfiguration != null) {
                List<ColumnConfiguration> foreignKeyColumns = new ArrayList<>();
                for(String foreignKey : foreignKeys){
                    for(ColumnConfiguration columnConfiguration : parentConfiguration.getColumns()){
                        if(foreignKey.equalsIgnoreCase(columnConfiguration.getColumnName())){
                            foreignKeyColumns.add(columnConfiguration);
                            break;
                        }
                    }
                }
                return foreignKeyColumns;
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> getForeignKeysMTM(ContextEntity publisherContextEntity, List<String> foreignKeysStrings, boolean isParent) {
        Map<String, String> aliasValueMaps = context.getContextEntities().stream().collect(Collectors.toMap(ContextEntity::getEntityStoreKey, ContextEntity::getSourceAlias,
                (alias1, alias2) -> {
                    return alias1;
                }));
        List<String> foreignKeys = new ArrayList<>();
        for (String key : foreignKeysStrings) {
            if (StringUtils.startsWithIgnoreCase(key, publisherContextEntity.getSourceAlias()) && !isParent) {
                foreignKeys.add(StringUtils.removeStartIgnoreCase(key, publisherContextEntity.getSourceAlias() + "."));
            } else if (!StringUtils.startsWithIgnoreCase(key, publisherContextEntity.getSourceAlias()) && isParent) {
                foreignKeys.add(StringUtils.removeStartIgnoreCase(key, aliasValueMaps.get(getTableName(publisherContextEntity.getParentContextEntityKey())) + "."));
            }
        }

        return foreignKeys;
    }

    public String getTableName(String contextEntityKey) {
        Map<String, String> aliasMaps = context.getContextEntities().stream().collect(Collectors.toMap(ContextEntity::getSourceAlias, ContextEntity::getEntityStoreKey,
                (alias1, alias2) -> {
                    return alias1;
                }));
        for (Map.Entry<String, String> entry : aliasMaps.entrySet()) {
            if (contextEntityKey.equals(entry.getKey() + "-" + entry.getValue())) {
                return entry.getValue();
            }
        }
        return contextEntityKey;
    }

    public TableInfoConfiguration getTableConfiguration(String schemaStoreKey, String tableName) {
        TableInfoConfiguration tableInfoConfiguration = null;
        Optional<ConfigurationRow> value = publishEntity.get(getFullName(namespace, schemaNameToDataChannelMap.get(schemaStoreKey)))
                .getTableConfigurations().get(PublisherUtils.getPublisherStoreForContextTable(schemaStoreKey)).getRows().stream()
                .filter(configurationRow -> configurationRow.getTableName().equals(tableName)).findFirst();
        if (value.isPresent()) {
            tableInfoConfiguration = value.get().getTableConfiguration();
        }
        return tableInfoConfiguration;
    }


    //----------------------------------------Transformations ---------------------------------

    private void registerPublishingQueryAndGroovy(Transformation transformation, Map<String, TargetEntity> targetEntites) {
        this.registerPublishingQuery(transformation, targetEntites);
        if (TransformationImplementationType.CUSTOM_GROOVY.equals(transformation.getImplementationType())) {
            registerPublishingGroovy(transformation, targetEntites);
        }
    }

    private void registerPublishingGroovy(Transformation transformation, Map<String, TargetEntity> targetEntities) {
        DataProcessingContext dataProcessingContext = dataProcessingContexts.get(getDataProcessingContextName(transformation));
        dataProcessingContext.addPublishingGroovy(new PublishingGroovy(transformation.getTargetEntityStoreKey(),
                transformation.getContextKey(), transformation.getCustomGroovyScript(),
                transformation.getCustomGroovyScriptForDeletionKeys(),
                targetEntities));
    }

    private void registerPublishingQuery(Transformation transformation, Map<String, TargetEntity> targetEntities) {
        DataProcessingContext dataProcessingContext = dataProcessingContexts.get(getDataProcessingContextName(transformation));
        dataProcessingContext.addPublishingQuery(new PublishingQuery(transformation.getTargetEntityStoreKey(),
                transformation.getContextKey(),
                transformation.getCustomScript(),
                transformation.getCustomScriptForDeletionKeys(),
                targetEntities.get(transformation.getTargetSchemaName() + '_' + transformation.getTargetEntityStoreKey())));
    }

    public enum ParentChildRelationType {
        PARENT("PARENT"),
        CHILD("CHILD");

        private final String type;

        public String getType() {
            return type;
        }

        ParentChildRelationType(String type) {
            this.type = type;
        }
    }

    private String getFullName(String namespace, String schemaStoreKey) {
        if (isNonTransient) {
            return PublisherUtils.getSchemaNameWithNamespace(namespace, schemaStoreKey) + "_transformer";
        } else {
            return PublisherUtils.getSchemaNameWithNamespace(namespace, schemaStoreKey);
        }
    }

    private String getDataProcessingContextName(Transformation transformation) {
        return transformation.getContextKey().replace("Context", "");
    }

    private RelationMandatoryMode getRelationMode(NoReferentAction referentAction) {
        return RelationMandatoryMode.valueOf(referentAction.name());
    }

}
