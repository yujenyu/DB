package sqlCmd;

import core.DBEngine;
import core.DBContext;
import core.SqlParser;
import core.exception.InvalidSqlException;

import java.util.List;

// <Alter>  ::=  ALTER TABLE <TableName> <AlterationType> <AttributeName>
// <AlterationType> ::=  ADD | DROP
public class AlterCommand implements DBCommand{
    private List<String> tokens;
    private String tableName;
    private String alterationType;
    private String attributeName;

    private void init(DBContext ctx) { tokens = ctx.getSqlTokens(); }

    @Override
    public void parseInput(DBContext ctx) throws Exception {
        init(ctx);

        if(!SqlParser.isValidAlterTableStatement(ctx.getSqlTokens())) {
            throw new InvalidSqlException("[ERROR]: Invalid query");
        }

        tableName = tokens.get(2);
        SqlParser.checkName(tableName);
        alterationType = tokens.get(3);
        attributeName = tokens.get(4);
        SqlParser.checkName(attributeName);
    }

    @Override
    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();
        engine.alterTable(ctx, tableName, attributeName, alterationType);
        ctx.setResult(Config.OK());
    }
}
