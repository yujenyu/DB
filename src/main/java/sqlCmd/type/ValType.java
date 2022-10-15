package sqlCmd.type;

import java.io.Serializable;

public enum ValType implements Serializable {
    STRING("'[^']*'"),
    BOOL("true|false"),
    FLOAT("[-+]?[0-9]*\\.[0-9]+"),
    INT("[-+]?[0-9]+");

    private final String pattern;

    ValType(String pattern) {
        this.pattern = pattern;
    }

    public final String pattern() {
        return pattern;
    }
}
