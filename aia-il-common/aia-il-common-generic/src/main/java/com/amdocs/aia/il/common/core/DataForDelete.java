package com.amdocs.aia.il.common.core;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.stores.KeyColumn;
import com.amdocs.aia.il.common.stores.KeyColumnDescriptor;
import com.amdocs.aia.il.common.utils.PublisherUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataForDelete implements Serializable {

    private static final long serialVersionUID = 7302745465034292136L;

    private KeyColumn keyColumn;
    private long updateTime;
    private Map<String, KeyColumn> fkNameKeyColumnMap = new HashMap<> ();
    private String tableName;

    public DataForDelete(KeyColumn keyColumn, long updateTime, Map<String, KeyColumn> fkNameKeyColumnMap, String tableName) {
        this.keyColumn = keyColumn;
        this.updateTime = updateTime;
        this.fkNameKeyColumnMap = fkNameKeyColumnMap;
        this.tableName = tableName;
    }

    public KeyColumn getKeyColumn () { return keyColumn; }

    public void setKeyColumn (KeyColumn keyColumn) { this.keyColumn=keyColumn; }

    public long getUpdateTime () { return updateTime; }

    public void setUpdateTime (long updateTime) { this.updateTime=updateTime; }

    public Map<String, KeyColumn> getFkNameKeyColumnMap () { return fkNameKeyColumnMap; }

    public void setFkNameKeyColumnMap (Map<String, KeyColumn> fkNameKeyColumnMap) { this.fkNameKeyColumnMap=fkNameKeyColumnMap; }

    public String getTableName () { return tableName; }

    public void setTableName (String tableName) { this.tableName=tableName; }

    public static DataForDelete getDataToDelete (String tableInfo, List<ColumnConfiguration> keyColumns, RepeatedMessage data, List<KeyColumnDescriptor> indexedFks) {
        Map<String, KeyColumn> fkNameKeyColumnMap = new HashMap<>();
        for (KeyColumnDescriptor fk : indexedFks) {
            if(!PublisherUtils.hasAllFields (data, fk.getKeys())) {
                continue;
            }
            fkNameKeyColumnMap.put(fk.getName(), new KeyColumn(data, fk.getKeys ()));
        }
        Long updateTime = (Long) data.getValue ("SYS_CREATION_DATE");
        if(updateTime == null) {
            updateTime = (Long) data.getValue("transactionTime");
        }
        DataForDelete dfd =  new DataForDelete (new KeyColumn (data, keyColumns), updateTime, fkNameKeyColumnMap, tableInfo);
        return dfd;
    }

    @Override
    public String toString() {
        return "DataForDelete{" +
                "keyColumn=" + keyColumn +
                ", updateTime=" + updateTime +
                ", fkNameKeyColumnMap=" + fkNameKeyColumnMap +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
