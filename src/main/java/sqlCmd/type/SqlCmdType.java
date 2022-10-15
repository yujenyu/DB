package sqlCmd.type;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SqlCmdType implements Serializable {
    USE("use"),
    CREATE("create"),
    DROP("drop"),
    ALTER("alter"),
    INSERT("insert"),
    SELECT("select"),
    UPDATE("update"),
    DELETE("delete"),
    JOIN("join");

    private String cmdType;

    public static SqlCmdType getEnum(String cmdType) {
        for(SqlCmdType t : values()) {
            if(t.toString().equalsIgnoreCase(cmdType)) {
                return t;
            }
        }
        throw new IllegalArgumentException();
    }

    SqlCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public static Set<String> sqlCmdSet = List.of(SqlCmdType.values()).stream()
            .map(e -> e.toString().toLowerCase())
            .collect(Collectors.toSet());
}
