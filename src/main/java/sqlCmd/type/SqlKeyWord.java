package sqlCmd.type;

import java.io.Serializable;

public enum SqlKeyWord implements Serializable {
    FROM("FROM"),
    WHERE("WHERE"),
    SET("SET"),
    DATABASE("DATABASE"),
    TABLE("TABLE");

    private final String pattern;

    SqlKeyWord(String pattern) {
        this.pattern = pattern;
    }

    public static SqlKeyWord getEnum(String keyWord) {
        for(SqlKeyWord k : values()) {
            if(k.toString().equalsIgnoreCase(keyWord)) {
                return k;
            }
        }
        throw new IllegalArgumentException();
    }

    public final String pattern() {
        return pattern.toLowerCase();
    }
}
