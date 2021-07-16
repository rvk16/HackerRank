package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.dto.LeadKeyDTO;
import com.amdocs.aia.il.configuration.dto.PropertyDataDTO;
import com.amdocs.aia.il.configuration.dto.TransformationContextEntityDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Transformation representation
 */
@ApiModel(description = "Transformation representation")
@Validated


public class TransformationDTO extends CommonModelDTO  {
  @JsonProperty("contextKey")
  private String contextKey = null;

  @JsonProperty("customScript")
  private String customScript = null;

  @JsonProperty("customScriptForDeletionKeys")
  private String customScriptForDeletionKeys = null;

  @JsonProperty("customGroovyScript")
  private String customGroovyScript = null;

  @JsonProperty("customGroovyScriptForDeletionKeys")
  private String customGroovyScriptForDeletionKeys = null;

  @JsonProperty("elementType")
  private String elementType = null;

  @JsonProperty("elementVersion")
  private Integer elementVersion = null;

  @JsonProperty("id")
  private String id = null;

  /**
   * Gets or Sets implementationType
   */
  public enum ImplementationTypeEnum {
    SQL("CUSTOM_SQL"),
    
    GROOVY("CUSTOM_GROOVY");

    private String value;

    ImplementationTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ImplementationTypeEnum fromValue(String text) {
      for (ImplementationTypeEnum b : ImplementationTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("implementationType")
  private ImplementationTypeEnum implementationType = null;

  @JsonProperty("lastUpdateTime")
  private Long lastUpdateTime = null;

  @JsonProperty("origin")
  private String origin = null;

  @JsonProperty("ownerProjectKey")
  private String ownerProjectKey = null;

  @JsonProperty("productKey")
  private String productKey = null;

  @JsonProperty("properties")
  @Valid
  private Map<String, PropertyDataDTO> properties = null;

  @JsonProperty("propertyValues")
  private Object propertyValues = null;

  @JsonProperty("referenceIds")
  @Valid
  private List<String> referenceIds = null;

  @JsonProperty("referenceSourceEntities")
  @Valid
  private List<TransformationContextEntityDTO> referenceSourceEntities = null;

  @JsonProperty("sourceElementId")
  private String sourceElementId = null;

  /**
   * Gets or Sets sourceType
   */
  public enum SourceTypeEnum {
    CONTEXT("CONTEXT"),
    
    REFERENCE("REFERENCE");

    private String value;

    SourceTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SourceTypeEnum fromValue(String text) {
      for (SourceTypeEnum b : SourceTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("sourceType")
  private SourceTypeEnum sourceType = null;

  @JsonProperty("tags")
  @Valid
  private List<String> tags = null;

  @JsonProperty("targetEntityStoreKey")
  private String targetEntityStoreKey = null;

  @JsonProperty("targetSchemaStoreKey")
  private String targetSchemaStoreKey = null;

  @JsonProperty("targetSchemaName")
  private String targetSchemaName = null;

  @JsonProperty("isPublished")
  private Boolean isPublished = null;

  @JsonProperty("referenceAttributes")
  @Valid
  private List<String> referenceAttributes = null;

  @JsonProperty("leadkeys")
  @Valid
  private List<LeadKeyDTO> leadkeys = null;

  public TransformationDTO contextKey(String contextKey) {
    this.contextKey = contextKey;
    return this;
  }

  /**
   * Get contextKey
   * @return contextKey
  **/
  @ApiModelProperty(value = "")


  public String getContextKey() {
    return contextKey;
  }

  public void setContextKey(String contextKey) {
    this.contextKey = contextKey;
  }

  public TransformationDTO customScript(String customScript) {
    this.customScript = customScript;
    return this;
  }

  /**
   * Get customScript
   * @return customScript
  **/
  @ApiModelProperty(value = "")


  public String getCustomScript() {
    return customScript;
  }

  public void setCustomScript(String customScript) {
    this.customScript = customScript;
  }

  public TransformationDTO customScriptForDeletionKeys(String customScriptForDeletionKeys) {
    this.customScriptForDeletionKeys = customScriptForDeletionKeys;
    return this;
  }

  /**
   * Get customScriptForDeletionKeys
   * @return customScriptForDeletionKeys
  **/
  @ApiModelProperty(value = "")


  public String getCustomScriptForDeletionKeys() {
    return customScriptForDeletionKeys;
  }

  public void setCustomScriptForDeletionKeys(String customScriptForDeletionKeys) {
    this.customScriptForDeletionKeys = customScriptForDeletionKeys;
  }

  public TransformationDTO customGroovyScript(String customGroovyScript) {
    this.customGroovyScript = customGroovyScript;
    return this;
  }

  /**
   * Get customGroovyScript
   * @return customGroovyScript
  **/
  @ApiModelProperty(value = "")


  public String getCustomGroovyScript() {
    return customGroovyScript;
  }

  public void setCustomGroovyScript(String customGroovyScript) {
    this.customGroovyScript = customGroovyScript;
  }

  public TransformationDTO customGroovyScriptForDeletionKeys(String customGroovyScriptForDeletionKeys) {
    this.customGroovyScriptForDeletionKeys = customGroovyScriptForDeletionKeys;
    return this;
  }

  /**
   * Get customGroovyScriptForDeletionKeys
   * @return customGroovyScriptForDeletionKeys
  **/
  @ApiModelProperty(value = "")


  public String getCustomGroovyScriptForDeletionKeys() {
    return customGroovyScriptForDeletionKeys;
  }

  public void setCustomGroovyScriptForDeletionKeys(String customGroovyScriptForDeletionKeys) {
    this.customGroovyScriptForDeletionKeys = customGroovyScriptForDeletionKeys;
  }

  public TransformationDTO elementType(String elementType) {
    this.elementType = elementType;
    return this;
  }

  /**
   * Get elementType
   * @return elementType
  **/
  @ApiModelProperty(value = "")


  public String getElementType() {
    return elementType;
  }

  public void setElementType(String elementType) {
    this.elementType = elementType;
  }

  public TransformationDTO elementVersion(Integer elementVersion) {
    this.elementVersion = elementVersion;
    return this;
  }

  /**
   * Get elementVersion
   * @return elementVersion
  **/
  @ApiModelProperty(value = "")


  public Integer getElementVersion() {
    return elementVersion;
  }

  public void setElementVersion(Integer elementVersion) {
    this.elementVersion = elementVersion;
  }

  public TransformationDTO id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TransformationDTO implementationType(ImplementationTypeEnum implementationType) {
    this.implementationType = implementationType;
    return this;
  }

  /**
   * Get implementationType
   * @return implementationType
  **/
  @ApiModelProperty(value = "")


  public ImplementationTypeEnum getImplementationType() {
    return implementationType;
  }

  public void setImplementationType(ImplementationTypeEnum implementationType) {
    this.implementationType = implementationType;
  }

  public TransformationDTO lastUpdateTime(Long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
    return this;
  }

  /**
   * Get lastUpdateTime
   * @return lastUpdateTime
  **/
  @ApiModelProperty(value = "")


  public Long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public TransformationDTO origin(String origin) {
    this.origin = origin;
    return this;
  }

  /**
   * Get origin
   * @return origin
  **/
  @ApiModelProperty(value = "")


  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public TransformationDTO ownerProjectKey(String ownerProjectKey) {
    this.ownerProjectKey = ownerProjectKey;
    return this;
  }

  /**
   * Get ownerProjectKey
   * @return ownerProjectKey
  **/
  @ApiModelProperty(value = "")


  public String getOwnerProjectKey() {
    return ownerProjectKey;
  }

  public void setOwnerProjectKey(String ownerProjectKey) {
    this.ownerProjectKey = ownerProjectKey;
  }

  public TransformationDTO productKey(String productKey) {
    this.productKey = productKey;
    return this;
  }

  /**
   * Get productKey
   * @return productKey
  **/
  @ApiModelProperty(value = "")


  public String getProductKey() {
    return productKey;
  }

  public void setProductKey(String productKey) {
    this.productKey = productKey;
  }

  public TransformationDTO properties(Map<String, PropertyDataDTO> properties) {
    this.properties = properties;
    return this;
  }

  public TransformationDTO putPropertiesItem(String key, PropertyDataDTO propertiesItem) {
    if (this.properties == null) {
      this.properties = new HashMap<>();
    }
    this.properties.put(key, propertiesItem);
    return this;
  }

  /**
   * Get properties
   * @return properties
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Map<String, PropertyDataDTO> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, PropertyDataDTO> properties) {
    this.properties = properties;
  }

  public TransformationDTO propertyValues(Object propertyValues) {
    this.propertyValues = propertyValues;
    return this;
  }

  /**
   * Get propertyValues
   * @return propertyValues
  **/
  @ApiModelProperty(value = "")


  public Object getPropertyValues() {
    return propertyValues;
  }

  public void setPropertyValues(Object propertyValues) {
    this.propertyValues = propertyValues;
  }

  public TransformationDTO referenceIds(List<String> referenceIds) {
    this.referenceIds = referenceIds;
    return this;
  }

  public TransformationDTO addReferenceIdsItem(String referenceIdsItem) {
    if (this.referenceIds == null) {
      this.referenceIds = new ArrayList<>();
    }
    this.referenceIds.add(referenceIdsItem);
    return this;
  }

  /**
   * Get referenceIds
   * @return referenceIds
  **/
  @ApiModelProperty(value = "")


  public List<String> getReferenceIds() {
    return referenceIds;
  }

  public void setReferenceIds(List<String> referenceIds) {
    this.referenceIds = referenceIds;
  }

  public TransformationDTO referenceSourceEntities(List<TransformationContextEntityDTO> referenceSourceEntities) {
    this.referenceSourceEntities = referenceSourceEntities;
    return this;
  }

  public TransformationDTO addReferenceSourceEntitiesItem(TransformationContextEntityDTO referenceSourceEntitiesItem) {
    if (this.referenceSourceEntities == null) {
      this.referenceSourceEntities = new ArrayList<>();
    }
    this.referenceSourceEntities.add(referenceSourceEntitiesItem);
    return this;
  }

  /**
   * Get referenceSourceEntities
   * @return referenceSourceEntities
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<TransformationContextEntityDTO> getReferenceSourceEntities() {
    return referenceSourceEntities;
  }

  public void setReferenceSourceEntities(List<TransformationContextEntityDTO> referenceSourceEntities) {
    this.referenceSourceEntities = referenceSourceEntities;
  }

  public TransformationDTO sourceElementId(String sourceElementId) {
    this.sourceElementId = sourceElementId;
    return this;
  }

  /**
   * Get sourceElementId
   * @return sourceElementId
  **/
  @ApiModelProperty(value = "")


  public String getSourceElementId() {
    return sourceElementId;
  }

  public void setSourceElementId(String sourceElementId) {
    this.sourceElementId = sourceElementId;
  }

  public TransformationDTO sourceType(SourceTypeEnum sourceType) {
    this.sourceType = sourceType;
    return this;
  }

  /**
   * Get sourceType
   * @return sourceType
  **/
  @ApiModelProperty(value = "")


  public SourceTypeEnum getSourceType() {
    return sourceType;
  }

  public void setSourceType(SourceTypeEnum sourceType) {
    this.sourceType = sourceType;
  }

  public TransformationDTO tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public TransformationDTO addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Get tags
   * @return tags
  **/
  @ApiModelProperty(value = "")


  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public TransformationDTO targetEntityStoreKey(String targetEntityStoreKey) {
    this.targetEntityStoreKey = targetEntityStoreKey;
    return this;
  }

  /**
   * Get targetEntityStoreKey
   * @return targetEntityStoreKey
  **/
  @ApiModelProperty(value = "")


  public String getTargetEntityStoreKey() {
    return targetEntityStoreKey;
  }

  public void setTargetEntityStoreKey(String targetEntityStoreKey) {
    this.targetEntityStoreKey = targetEntityStoreKey;
  }

  public TransformationDTO targetSchemaStoreKey(String targetSchemaStoreKey) {
    this.targetSchemaStoreKey = targetSchemaStoreKey;
    return this;
  }

  /**
   * Get targetSchemaStoreKey
   * @return targetSchemaStoreKey
  **/
  @ApiModelProperty(value = "")


  public String getTargetSchemaStoreKey() {
    return targetSchemaStoreKey;
  }

  public void setTargetSchemaStoreKey(String targetSchemaStoreKey) {
    this.targetSchemaStoreKey = targetSchemaStoreKey;
  }

  public TransformationDTO targetSchemaName(String targetSchemaName) {
    this.targetSchemaName = targetSchemaName;
    return this;
  }

  /**
   * Get targetSchemaName
   * @return targetSchemaName
  **/
  @ApiModelProperty(value = "")


  public String getTargetSchemaName() {
    return targetSchemaName;
  }

  public void setTargetSchemaName(String targetSchemaName) {
    this.targetSchemaName = targetSchemaName;
  }

  public TransformationDTO isPublished(Boolean isPublished) {
    this.isPublished = isPublished;
    return this;
  }

  /**
   * Get isPublished
   * @return isPublished
  **/
  @ApiModelProperty(value = "")


  public Boolean isIsPublished() {
    return isPublished;
  }

  public void setIsPublished(Boolean isPublished) {
    this.isPublished = isPublished;
  }

  public TransformationDTO referenceAttributes(List<String> referenceAttributes) {
    this.referenceAttributes = referenceAttributes;
    return this;
  }

  public TransformationDTO addReferenceAttributesItem(String referenceAttributesItem) {
    if (this.referenceAttributes == null) {
      this.referenceAttributes = new ArrayList<>();
    }
    this.referenceAttributes.add(referenceAttributesItem);
    return this;
  }

  /**
   * Get referenceAttributes
   * @return referenceAttributes
  **/
  @ApiModelProperty(value = "")


  public List<String> getReferenceAttributes() {
    return referenceAttributes;
  }

  public void setReferenceAttributes(List<String> referenceAttributes) {
    this.referenceAttributes = referenceAttributes;
  }

  public TransformationDTO leadkeys(List<LeadKeyDTO> leadkeys) {
    this.leadkeys = leadkeys;
    return this;
  }

  public TransformationDTO addLeadkeysItem(LeadKeyDTO leadkeysItem) {
    if (this.leadkeys == null) {
      this.leadkeys = new ArrayList<>();
    }
    this.leadkeys.add(leadkeysItem);
    return this;
  }

  /**
   * Get leadkeys
   * @return leadkeys
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<LeadKeyDTO> getLeadkeys() {
    return leadkeys;
  }

  public void setLeadkeys(List<LeadKeyDTO> leadkeys) {
    this.leadkeys = leadkeys;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransformationDTO transformation = (TransformationDTO) o;
    return Objects.equals(this.contextKey, transformation.contextKey) &&
        Objects.equals(this.customScript, transformation.customScript) &&
        Objects.equals(this.customScriptForDeletionKeys, transformation.customScriptForDeletionKeys) &&
        Objects.equals(this.customGroovyScript, transformation.customGroovyScript) &&
        Objects.equals(this.customGroovyScriptForDeletionKeys, transformation.customGroovyScriptForDeletionKeys) &&
        Objects.equals(this.elementType, transformation.elementType) &&
        Objects.equals(this.elementVersion, transformation.elementVersion) &&
        Objects.equals(this.id, transformation.id) &&
        Objects.equals(this.implementationType, transformation.implementationType) &&
        Objects.equals(this.lastUpdateTime, transformation.lastUpdateTime) &&
        Objects.equals(this.origin, transformation.origin) &&
        Objects.equals(this.ownerProjectKey, transformation.ownerProjectKey) &&
        Objects.equals(this.productKey, transformation.productKey) &&
        Objects.equals(this.properties, transformation.properties) &&
        Objects.equals(this.propertyValues, transformation.propertyValues) &&
        Objects.equals(this.referenceIds, transformation.referenceIds) &&
        Objects.equals(this.referenceSourceEntities, transformation.referenceSourceEntities) &&
        Objects.equals(this.sourceElementId, transformation.sourceElementId) &&
        Objects.equals(this.sourceType, transformation.sourceType) &&
        Objects.equals(this.tags, transformation.tags) &&
        Objects.equals(this.targetEntityStoreKey, transformation.targetEntityStoreKey) &&
        Objects.equals(this.targetSchemaStoreKey, transformation.targetSchemaStoreKey) &&
        Objects.equals(this.targetSchemaName, transformation.targetSchemaName) &&
        Objects.equals(this.isPublished, transformation.isPublished) &&
        Objects.equals(this.referenceAttributes, transformation.referenceAttributes) &&
        Objects.equals(this.leadkeys, transformation.leadkeys) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contextKey, customScript, customScriptForDeletionKeys, customGroovyScript, customGroovyScriptForDeletionKeys, elementType, elementVersion, id, implementationType, lastUpdateTime, origin, ownerProjectKey, productKey, properties, propertyValues, referenceIds, referenceSourceEntities, sourceElementId, sourceType, tags, targetEntityStoreKey, targetSchemaStoreKey, targetSchemaName, isPublished, referenceAttributes, leadkeys, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransformationDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    contextKey: ").append(toIndentedString(contextKey)).append("\n");
    sb.append("    customScript: ").append(toIndentedString(customScript)).append("\n");
    sb.append("    customScriptForDeletionKeys: ").append(toIndentedString(customScriptForDeletionKeys)).append("\n");
    sb.append("    customGroovyScript: ").append(toIndentedString(customGroovyScript)).append("\n");
    sb.append("    customGroovyScriptForDeletionKeys: ").append(toIndentedString(customGroovyScriptForDeletionKeys)).append("\n");
    sb.append("    elementType: ").append(toIndentedString(elementType)).append("\n");
    sb.append("    elementVersion: ").append(toIndentedString(elementVersion)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    implementationType: ").append(toIndentedString(implementationType)).append("\n");
    sb.append("    lastUpdateTime: ").append(toIndentedString(lastUpdateTime)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
    sb.append("    ownerProjectKey: ").append(toIndentedString(ownerProjectKey)).append("\n");
    sb.append("    productKey: ").append(toIndentedString(productKey)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
    sb.append("    propertyValues: ").append(toIndentedString(propertyValues)).append("\n");
    sb.append("    referenceIds: ").append(toIndentedString(referenceIds)).append("\n");
    sb.append("    referenceSourceEntities: ").append(toIndentedString(referenceSourceEntities)).append("\n");
    sb.append("    sourceElementId: ").append(toIndentedString(sourceElementId)).append("\n");
    sb.append("    sourceType: ").append(toIndentedString(sourceType)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    targetEntityStoreKey: ").append(toIndentedString(targetEntityStoreKey)).append("\n");
    sb.append("    targetSchemaStoreKey: ").append(toIndentedString(targetSchemaStoreKey)).append("\n");
    sb.append("    targetSchemaName: ").append(toIndentedString(targetSchemaName)).append("\n");
    sb.append("    isPublished: ").append(toIndentedString(isPublished)).append("\n");
    sb.append("    referenceAttributes: ").append(toIndentedString(referenceAttributes)).append("\n");
    sb.append("    leadkeys: ").append(toIndentedString(leadkeys)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

