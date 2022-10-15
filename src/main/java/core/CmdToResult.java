package core;

public class CmdToResult {
    String cmd;
    String expectedResult;

    public CmdToResult(String cmd, String expectedResult) {
        this.cmd = cmd;
        this.expectedResult = expectedResult;
    }

    public String getCmd() {
        return this.cmd;
    }

    public String getExpectedResult() {
        return this.expectedResult;
    }
}
