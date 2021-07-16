package com.amdocs.aia.il.configuration.dto;

import java.util.Objects;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.CommonModelDTO;
import com.amdocs.aia.il.configuration.dto.ContextEntityDTO;
import com.amdocs.aia.il.configuration.dto.PropertyDataDTO;
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
 * Context representation
 */
@ApiModel(description = "Context representation")
@Validated


public class ContextDTO extends CommonModelDTO  {
  @JsonProperty("contextEntities")
  @Valid
  private List<ContextEntityDTO> contextEntities = null;

  @JsonProperty("contextKey")
  private String contextKey = null;

  @JsonProperty("elementType")
  private String elementType = null;

  @JsonProperty("elementVersion")
  private Integer elementVersion = null;

  @JsonProperty("id")
  private String id = null;

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

  @JsonProperty("sourceElementId")
  private String sourceElementId = null;

  @JsonProperty("tags")
  @Valid
  private List<String> tags = null;

  public ContextDTO contextEntities(List<ContextEntityDTO> contextEntities) {
    this.contextEntities = contextEntities;
    return this;
  }

  public ContextDTO addContextEntitiesItem(ContextEntityDTO contextEntitiesItem) {
    if (this.contextEntities == null) {
      this.contextEntities = new ArrayList<>();
    }
    this.contextEntities.add(contextEntitiesItem);
    return this;
  }

  /**
   * Get contextEntities
   * @return contextEntities
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<ContextEntityDTO> getContextEntities() {
    return contextEntities;
  }

  public void setContextEntities(List<ContextEntityDTO> contextEntities) {
    this.contextEntities = contextEntities;
  }

  public ContextDTO contextKey(String contextKey) {
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

  public ContextDTO elementType(String elementType) {
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

  public ContextDTO elementVersion(Integer elementVersion) {
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

  public ContextDTO id(String id) {
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

  public ContextDTO lastUpdateTime(Long lastUpdateTime) {
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

  public ContextDTO origin(String origin) {
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

  public ContextDTO ownerProjectKey(String ownerProjectKey) {
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

  public ContextDTO productKey(String productKey) {
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

  public ContextDTO properties(Map<String, PropertyDataDTO> properties) {
    this.properties = properties;
    return this;
  }

  public ContextDTO putPropertiesItem(String key, PropertyDataDTO propertiesItem) {
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

  public ContextDTO propertyValues(Object propertyValues) {
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

  public ContextDTO referenceIds(List<String> referenceIds) {
    this.referenceIds = referenceIds;
    return this;
  }

  public ContextDTO addReferenceIdsItem(String referenceIdsItem) {
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

  public ContextDTO sourceElementId(String sourceElementId) {
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

  public ContextDTO tags(List<String> tags) {
    this.tags = tags;
    return this;
  }

  public ContextDTO addTagsItem(String tagsItem) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContextDTO context = (ContextDTO) o;
    return Objects.equals(this.contextEntities, context.contextEntities) &&
        Objects.equals(this.contextKey, context.contextKey) &&
        Objects.equals(this.elementType, context.elementType) &&
        Objects.equals(this.elementVersion, context.elementVersion) &&
        Objects.equals(this.id, context.id) &&
        Objects.equals(this.lastUpdateTime, context.lastUpdateTime) &&
        Objects.equals(this.origin, context.origin) &&
        Objects.equals(this.ownerProjectKey, context.ownerProjectKey) &&
        Objects.equals(this.productKey, context.productKey) &&
        Objects.equals(this.properties, context.properties) &&
        Objects.equals(this.propertyValues, context.propertyValues) &&
        Objects.equals(this.referenceIds, context.referenceIds) &&
        Objects.equals(this.sourceElementId, context.sourceElementId) &&
        Objects.equals(this.tags, context.tags) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contextEntities, contextKey, elementType, elementVersion, id, lastUpdateTime, origin, ownerProjectKey, productKey, properties, propertyValues, referenceIds, sourceElementId, tags, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContextDTO {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    contextEntities: ").append(toIndentedString(contextEntities)).append("\n");
    sb.append("    contextKey: ").append(toIndentedString(contextKey)).append("\n");
    sb.append("    elementType: ").append(toIndentedString(elementType)).append("\n");
    sb.append("    elementVersion: ").append(toIndentedString(elementVersion)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    lastUpdateTime: ").append(toIndentedString(lastUpdateTime)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
    sb.append("    ownerProjectKey: ").append(toIndentedString(ownerProjectKey)).append("\n");
    sb.append("    productKey: ").append(toIndentedString(productKey)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
    sb.append("    propertyValues: ").append(toIndentedString(propertyValues)).append("\n");
    sb.append("    referenceIds: ").append(toIndentedString(referenceIds)).append("\n");
    sb.append("    sourceElementId: ").append(toIndentedString(sourceElementId)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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

