package com.amdocs.aia.il.common.stores;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class KeyColumn implements Serializable {
    private static final long serialVersionUID = 9190668147162163247L;

    public static final String ROWKEY_SEPARATOR = ":::";

    private final Serializable[] ids;

    public KeyColumn(RepeatedMessage repeatedMessage, List<ColumnConfiguration> idFields) {
        this.ids = new Serializable[idFields.size()];
        int index = 0;
        for (final ColumnConfiguration idField : idFields) {
            this.ids[index++] = repeatedMessage.getValue(idField.getColumnName());
        }
    }

    /**
     * For testing purposes
     *
     * @param ids
     */
    public KeyColumn(final Serializable[] ids) {
        this.ids = ids;
    }

    public Object[] getIds() {
        return ids;
    }

    public static Object[] getIds(RepeatedMessage repeatedMessage, List<ColumnConfiguration> idFields) {
        return new KeyColumn(repeatedMessage, idFields).getIds();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final KeyColumn keyColumn = (KeyColumn) o;
        String[] strIds=  convertIdsToString(ids);
        String[] strKeyIds= convertIdsToString(keyColumn.ids);
        return Arrays.equals(strIds, strKeyIds);
    }

    private static String[] convertIdsToString(Serializable[] s) {
        final String[] strIds = new String[s.length];
        int i = 0;
        for (Serializable id : s) {
            if(id == null) {
                strIds[i++] = null;
            }else {
                strIds[i++] = id.toString();
            }
        }
        return strIds;
    }

    public static boolean isEmpty(KeyColumn keyColumn){
        for(Object k: keyColumn.getIds()){
            if(k!=null && !"".equals(k)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(convertIdsToString(ids));
    }

    /**
     * Compare current Row ID values to other
     *
     * @param keyColumn - row ID to compare
     * @return true if equals, else false
     */
    public boolean compareTo(KeyColumn keyColumn) {
        return Arrays.equals(this.ids, keyColumn.getIds());
    }

    /**
     * Act as regular contains just compare all values as strings
     *
     * @param list
     * @param keyColumnToCheck
     * @return true if keyColumnToCheck contains in list compared as string value, else false
     */
    public static boolean containsStr(Collection<KeyColumn> list, KeyColumn keyColumnToCheck) {
        //keys of the object to check
        Object[] keyColumnToCheckIds = keyColumnToCheck.getIds();
        for (KeyColumn keyColumn : list) {
            //keys of the object in list
            Object[] ids = keyColumn.getIds();
            //check first that length is equal
            if (ids.length == keyColumnToCheckIds.length) {
                boolean isEqual = true;
                //check that values equal
                for (int i = 0; i < ids.length; i++) {
                    if (!ids[i].toString().equals(keyColumnToCheckIds[i].toString())) {
                        isEqual = false;
                        break;
                    }
                }
                if (isEqual) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return StringUtils.join(ids, ROWKEY_SEPARATOR);
    }

    /**
     * @return new {@link KeyColumn} with the exact values converted to strings
     */
    public KeyColumn createNewStrIds() {
        final String[] strIds = new String[this.ids.length];
        int i = 0;
        for (final Serializable id : this.ids) {
            strIds[i++] = id.toString();
        }
        return new KeyColumn(strIds);
    }
}