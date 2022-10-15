package sqlCmd;

import core.Condition;
import core.DBContext;
import core.DBEngine;
import core.SqlParser;
import core.exception.InvalidSqlException;
import sqlCmd.type.SqlKeyWord;

import java.util.List;
import java.util.Map;

// <Delete>  ::=  DELETE FROM <TableName> WHERE <Condition>
public class DeleteCommand implements DBCommand{
    private List<String> tokens;
    private String tableName;
    Condition condition;
    Map<SqlKeyWord, Integer> keywordIdx;
    private int idxOfFROM, idxOfWHERE;

    private void init(DBContext ctx) {
        tokens = ctx.getSqlTokens();
        keywordIdx = SqlParser.getKeywordIdx(tokens);
        idxOfFROM = keywordIdx.get(SqlKeyWord.FROM);
        idxOfWHERE = keywordIdx.get(SqlKeyWord.WHERE);
    }

    @Override
    public void parseInput(DBContext ctx) throws Exception {
        init(ctx);

        // FROM and WHERE is requisite for DELETE clause
        if (idxOfFROM == -1 || idxOfWHERE == -1) throw new InvalidSqlException("[ERROR]: Invalid query");

        tableName = tokens.get(idxOfFROM + 1);
        SqlParser.checkName(tableName);

        condition = SqlParser.toCondition(ctx, tokens.subList(idxOfWHERE + 1, tokens.size()));
    }

    @Override
    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();
        engine.deleteRow(ctx, tableName, condition);
        ctx.setResult(Config.OK());
    }
}
