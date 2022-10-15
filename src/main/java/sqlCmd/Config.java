package sqlCmd;

import sqlCmd.type.StatusType;

import java.util.EnumMap;

public class Config {

    private static final EnumMap<StatusType, String> outputMsg = new EnumMap<StatusType, String>(StatusType.class)
    {{
        put(StatusType.OK, "[OK]");
        put(StatusType.ERROR, "[ERROR]");
    }};

    public static String getOutput(StatusType status) {
        return outputMsg.get(status);
    }

    public static String OK() {
        return getOutput(StatusType.OK);
    }

    public static String ERROR() {
        return getOutput(StatusType.ERROR);
    }
}
