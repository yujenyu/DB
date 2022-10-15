package core;

import sqlCmd.type.ValType;

public class NameValue {
    String col;
    String val;
    ValType valType;

    NameValue(String col, String val) {
        this.col = col;
        this.val = SqlParser.unquoted(val);
        this.valType = SqlParser.getValType(val);
    }

    protected String getCol() {
        return this.col;
    }


    protected String getVal() {
        return this.val;
    }

    protected ValType getValType() {
        return this.getValType();
    }

    public String toString() {
       return new StringBuilder()
               .append(this.col).append("=").append(this.val)
               .append("[").append(this.valType).append("]")
               .toString();
    }
}

