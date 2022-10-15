package core;

import core.exception.InvalidSqlException;
import sqlCmd.Config;
import sqlCmd.type.StatusType;
import sqlCmd.type.ValType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Condition {

    protected static Map<String, Set<ValType>> validValType = new HashMap<String, Set<ValType>>() {{
        put("<", Stream.of(ValType.INT, ValType.FLOAT).collect(Collectors.toSet()));
        put("<=", Stream.of(ValType.INT, ValType.FLOAT).collect(Collectors.toSet()));
        put(">", Stream.of(ValType.INT, ValType.FLOAT).collect(Collectors.toSet()));
        put(">=", Stream.of(ValType.INT, ValType.FLOAT).collect(Collectors.toSet()));
        put("==", Stream.of(ValType.INT, ValType.FLOAT, ValType.STRING, ValType.BOOL).collect(Collectors.toSet()));
        put("!=", Stream.of(ValType.INT, ValType.FLOAT, ValType.STRING, ValType.BOOL).collect(Collectors.toSet()));
        put("LIKE", Stream.of(ValType.STRING).collect(Collectors.toSet()));
        put("like", Stream.of(ValType.STRING).collect(Collectors.toSet()));
    }};

    String col;
    String val;
    String op;
    ValType valType;

    Condition(String col, String op, String val) {
        this.col = col;
        this.op = op;
        this.val = SqlParser.unquoted(val);
        this.valType = SqlParser.getValType(val);
    }

    protected String getCol() {
        return this.col;
    }

    protected String getOp() {
        return this.op;
    }

    protected String getVal() {
        return this.val;
    }

    protected ValType getValType() {
        return this.getValType();
    }

    public boolean isLessThan(Row row) throws InvalidSqlException {
        return parseFloat(row.getColVal(col)) <  parseFloat(this.val);
    }

    public boolean isLessThanEqual(Row row) throws InvalidSqlException {
        return parseFloat(row.getColVal(col)) <=  parseFloat(this.val);
    }
    public boolean isGreatThan(Row row) throws InvalidSqlException {
        return parseFloat(row.getColVal(col)) >  parseFloat(this.val);
    }

    public boolean isGreatThanEqual(Row row) throws InvalidSqlException {
        return parseFloat(row.getColVal(col)) >=  parseFloat(this.val);
    }

    private  float parseFloat(String aString) throws InvalidSqlException {
        try {
            return Float.parseFloat(aString);
        } catch (NumberFormatException e) {
            throw new InvalidSqlException("[ERROR]: Attribute cannot be converted to number");
        }
    }
    public boolean isEqual(Row row) throws InvalidSqlException {
        switch (valType) {
            case STRING:
            case BOOL:
                return row.getColVal(col).equals(this.val);
            case FLOAT:
            case INT:
                return parseFloat(row.getColVal(col)) == parseFloat(this.val);
            default:
                throw new InvalidSqlException(Config.getOutput(StatusType.ERROR) + ": " + op + "is only support " + validValType.get(op) + " types.");
        }
    }

    public boolean isLike(Row row) throws InvalidSqlException {
        return row.getColVal(col).contains(this.val);
    }

    boolean evaluate(Row row) throws InvalidSqlException {
        if (!validValType.get(op).contains(valType)) {
            throw new InvalidSqlException("[ERROR]: String expected");
        }

        switch (op) {
            case "<":
                return isLessThan(row);
            case "<=":
                return isLessThanEqual(row);
            case ">":
                return isGreatThan(row);
            case ">=":
                return isGreatThanEqual(row);
            case "==":
                return isEqual(row);
            case "!=":
                return !isEqual(row);
            case "LIKE":
            case "like":
                return isLike(row);
            default:
                throw new InvalidSqlException(Config.getOutput(StatusType.ERROR) + " : Invalid WHERE Condition operator" + op);
        }
    }
    public String toString() {
        return this.col + " " + this.op + " " + this.val;
    }
}
