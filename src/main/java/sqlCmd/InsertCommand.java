package sqlCmd;

import core.DBContext;
import core.DBEngine;
import core.SqlParser;
import core.exception.InvalidSqlException;

import java.util.List;

// <Insert>     ::=  INSERT INTO <TableName> VALUES ( <ValueList> )
// <ValueList>  ::=  <Value>  |  <Value> , <ValueList>
// <Value>      ::=  '<StringLiteral>'  |  <BooleanLiteral>  |  <FloatLiteral>  |  <IntegerLiteral>
public class InsertCommand implements DBCommand {
    private List<String> tokens;
    private String tableName;
    private List<String> attributeList;

    private void init(DBContext ctx) {
        tokens = ctx.getSqlTokens();
    }

    // [INSERT INTO marks VALUES ('Steve', 65, true);]
    @Override
    public void parseInput(DBContext ctx) throws Exception {
        init(ctx);

        if(!SqlParser.isValidInsertIntoStatement(ctx.getSqlTokens())) {
            throw new InvalidSqlException("[ERROR]: Invalid query");
        }

        tableName = tokens.get(2);
        SqlParser.checkName(tableName);

        int startIdx = tokens.indexOf("(");
        int endIdx = tokens.size();
        attributeList = SqlParser.toAttributeList(ctx,tokens.subList(startIdx, endIdx));
    }

    @Override
    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();
        engine.insertIntoTable(ctx, tableName, attributeList);
        ctx.setResult(Config.OK());
    }

}
