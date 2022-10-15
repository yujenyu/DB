package core;

import core.exception.InvalidSqlException;
import sqlCmd.Config;
import sqlCmd.type.StatusType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Row implements Serializable {
    private static final long serialVersionUID = 2204826590490672252L;

    private final LinkedHashMap<String, String> columnToValue;
    private String rowInString;
    public Row() {
        columnToValue = new LinkedHashMap<>();
    }

    protected void setRowData(String columnName, String columnValue) {
        columnToValue.put(columnName, columnValue);
    }

    public void setRow(Map<String, String> data) {
        for(Map.Entry<String,String> entry: data.entrySet()) {
            setRowData(entry.getKey(), entry.getValue());
        }
    }

    protected String getColVal(String key) throws InvalidSqlException {
        if(!columnToValue.containsKey(key)) {
            throw new InvalidSqlException(Config.getOutput(StatusType.ERROR) + ": Invalid WHERE Condition no such column name: " + key);
        }
        return columnToValue.get(key);
    }

    protected void addNewColumn(String columnName) {
        columnToValue.put(columnName, "");
    }

    protected String getColumnToValue() {
        rowInString = "";

        StringBuilder sb = new StringBuilder();
        for(String key : columnToValue.keySet()) {
            sb.append(columnToValue.get(key));
            sb.append("\t");
        }
        return sb.toString();
    }

    public void removeColumnValue(String columnName) {
        columnToValue.remove(columnName);
    }

    protected void updateRowValue(String columnName, String newValue){
        columnToValue.replace(columnName, newValue);
        // updateRowToPrint();
    }

    protected Set<String> getKey() {
        return columnToValue.keySet();
    }

}
