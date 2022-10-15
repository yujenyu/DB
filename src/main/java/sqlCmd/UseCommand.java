package sqlCmd;

import core.DBContext;
import core.DBEngine;
import core.SqlParser;
import sqlCmd.type.StatusType;

// <Use>  ::=  USE <DatabaseName>
public class UseCommand implements DBCommand{
    private String dbName;

    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();
        SqlParser.checkName(dbName);
        engine.useDatabase(ctx, dbName);
        ctx.setResult(Config.getOutput(StatusType.OK));
    }

    public void parseInput(DBContext ctx) throws Exception {
        dbName = ctx.getSqlTokens().get(1);
    }
}
