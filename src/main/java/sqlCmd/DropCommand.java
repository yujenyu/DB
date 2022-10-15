package sqlCmd;

import core.DBContext;
import core.DBEngine;
import core.exception.InvalidSqlException;
import sqlCmd.type.SqlKeyWord;
import sqlCmd.type.StatusType;


// <Drop>       ::=  DROP <Structure> <StructureName>
// <Structure>  ::=  DATABASE | TABLE
public class DropCommand implements DBCommand{
    private SqlKeyWord dropType;
    private String dropName;

    @Override
    public void parseInput(DBContext ctx) throws Exception {
        dropType = SqlKeyWord.getEnum(ctx.getSqlTokens().get(1));
        dropName = ctx.getSqlTokens().get(2);
    }

    @Override
    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();
        switch (dropType) {
            case DATABASE:
                engine.dropDatabase(dropName);
                break;
            case TABLE:
                engine.dropTable(ctx, dropName);
                break;
            default:
                throw new InvalidSqlException(Config.getOutput(StatusType.ERROR) + " : invalid create type.");
        }
        ctx.setResult(Config.getOutput(StatusType.OK));
    }

    public SqlKeyWord getDropType() {
        return this.dropType;
    }

    public String getDropName() {
        return this.dropName;
    }
}
