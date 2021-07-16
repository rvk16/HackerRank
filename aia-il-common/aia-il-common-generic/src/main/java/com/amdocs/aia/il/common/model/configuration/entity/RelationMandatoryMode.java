package com.amdocs.aia.il.common.model.configuration.entity;

/**
 * Defines if a relation is:
 * mandatory - will use retry mechanism
 * optional - publish regular
 * mandatory_publish - use retry table and publish
 * Created by ORENKAF on 12/13/2016.
 */
public enum RelationMandatoryMode {
    OPTIONAL(1),
    MANDATORY_PUBLISH(2),
    MANDATORY(3);

    //represents the priority of the relation,
    //i.e. in case in the same level there are both MANDATORY & MANDATORY_PUBLISH --> act as MANDATORY
    private int id;

    RelationMandatoryMode (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * @param relationMandatoryMode1
     * @param relationMandatoryMode2
     * @return the {@link RelationMandatoryMode} which has higher priority
     */
    public static RelationMandatoryMode getRelationMandatoryModeWithHigherPriority(
            RelationMandatoryMode relationMandatoryMode1, RelationMandatoryMode relationMandatoryMode2) {
        return relationMandatoryMode1.getId() >= relationMandatoryMode2.getId()?relationMandatoryMode1:relationMandatoryMode2;
    }
}
