package core;

import core.exception.ColumnSizeNotMatchException;
import core.exception.InvalidSqlException;
import sqlCmd.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Table implements Serializable {
    private static final long serialVersionUID = 2733089634426428303L;

    private LinkedList<Row> rows;
    private LinkedList<String> columnNames;
    private int rowId;
    private String filename;

    public Table(String filename) {
        this.filename = filename;
        this.rows = new LinkedList<>();
        this.columnNames = new LinkedList<>();
        this.rowId = 0;
        this.addColumn("id");
    }

    protected void addColumn(String columnName) {
        this.columnNames.add(columnName);
    }

    protected void addSingleColumn(String columnName) {
        this.columnNames.add(columnName);
        extendRows(columnName);
    }

    protected void addColumns(List<String> columnNames){
        this.columnNames.addAll(columnNames);
    }

    protected void extendRows(String columnName) {
        this.rows.forEach(row -> row.addNewColumn(columnName));
    }

    public void removeColumn(String columnName) {
        removeColumnValue(columnName);
        this.columnNames.remove(columnName);
    }

    public void removeColumnValue(String columnName) {
        for(Row row : rows){
            row.removeColumnValue(columnName);
        }
    }

    protected void removeRow(String columnName, Condition condition) throws InvalidSqlException {

        List<Row> rowsToBeDeleted = getFilterRows(condition);
        this.rows.removeAll(rowsToBeDeleted);
    }

    public void setData(LinkedList<Row> rows, List<String> columnName) {
        this.rows = rows;
    }

    public void addRow(List<String> values) throws ColumnSizeNotMatchException {
        if (values.size() != columnNames.size() - 1) {
            throw new ColumnSizeNotMatchException("value list's size not match.");
        }

        Row row = new Row();
        // id column
        row.setRow(Map.of("id", Integer.toString(++rowId)));
        // other columns
        for(int i=0; i<columnNames.size()-1; i++) {
            row.setRow(Map.of(columnNames.get(i+1), values.get(i)));
        }
        rows.add(row);
    }

    public String getAllRows() {
        StringBuilder sb = new StringBuilder();
        rows.forEach(row -> sb.append(row.getColumnToValue()).append("\n"));
        return sb.toString();
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public String getAllColumns(){
        StringBuilder sb = new StringBuilder();
        for(String column : columnNames){
            sb.append(column);
            sb.append("  ");
        }
        sb.append("\n");
        return sb.toString();
    }

    public String toString(Condition condition) throws InvalidSqlException {
        if(condition == null) {
            return new StringBuilder()
                    .append(columnsToString(this.columnNames)).append("\n")
                    .append(getAllRows())
                    .toString();
        }

        return new StringBuilder()
                .append(columnsToString(this.columnNames)).append("\n")
                .append(getRows(condition, null))
                .toString();
    }

    public String toString(Condition condition, List<String> attributeList) throws InvalidSqlException {
        return new StringBuilder()
                .append(columnsToString( getFilterCols(attributeList) )).append("\n")
                .append(getRows(condition, attributeList))
                .toString();
    }

    public String getRows(Condition condition, List<String> attributeList) throws InvalidSqlException {
        StringBuilder sb = new StringBuilder();

        List<Row> matchedRows = getFilterRows(condition);
        List<Row> matchedRowsCols = getFilterCols(matchedRows, attributeList);

        matchedRowsCols.forEach(row -> sb.append(row.getColumnToValue()).append("\n"));
        return sb.toString();
    }

    protected List<Row> getFilterCols(List<Row> rows, List<String> attributeList) throws InvalidSqlException {
        if(attributeList == null) {
            return rows;
        }

        Set<String> targetRowName = new HashSet<>(attributeList);
        List<Row> resRows = new ArrayList<>();

            for(Row srcRow : rows) {
                Row dstRow = new Row();

                for(String key: srcRow.getKey()) {
                    if (targetRowName.contains(key)) {
                        // System.out.println("DEBUG : " + key + "," + srcRow.getColVal(key));
                        dstRow.setRowData(key, srcRow.getColVal(key));
                    }
                }
                resRows.add(dstRow);
        }
        return resRows;
    }

    protected List<Row> getFilterRows(Condition condition) throws InvalidSqlException {
        if(condition == null) {
            return rows;
        }

        List<Row> resultRows = new ArrayList<>();
        for(Row row : rows) {
            if(condition.evaluate(row))  {
                resultRows.add(row);
            }
        }

        return resultRows;
//        return   rows.stream()
//                .filter(r -> {
//                    try {
//                        return condition.evaluate(r);
//                    } catch (InvalidSqlException e) {
//                    }
//                    return false;
//                }).collect(Collectors.toList());
    }

    protected List<String> getFilterCols(List<String> attributeList) throws InvalidSqlException {
        if(! columnNames.containsAll(attributeList)) {
            throw new InvalidSqlException("[ERROR]: Attribute does not exist");
        }

        return attributeList.stream()
                .filter(attributeList::contains)
                .collect(Collectors.toList());
    }

    protected String columnsToString(List<String> columns) {
        StringBuilder sb = new StringBuilder();
        columns.forEach(column -> sb.append(column) .append("\t"));
        return sb.toString();
    }

    public void updateRows(List<NameValue> nameValueList, Condition condition) throws InvalidSqlException {
        List<String> colNamesToBeUpdate = nameValueList.stream()
                .map(NameValue::getCol)
                .collect(Collectors.toList());

        if(!getColumnNames().containsAll(colNamesToBeUpdate)) {
            throw new InvalidSqlException(Config.ERROR() + ":Invalid UPDATE statement, not all update column existed in this table");
        }

        List<Row> matchedRows = getFilterRows(condition);
        for(Row row: matchedRows) {
            for(NameValue nameValue : nameValueList) {
                row.setRowData(nameValue.getCol(), nameValue.getVal());
            }
        }
    }

    public String getFilename() {
        return this.filename;
    }
}
