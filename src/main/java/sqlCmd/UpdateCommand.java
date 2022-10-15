package sqlCmd;

import core.Condition;
import core.DBContext;
import core.DBEngine;
import core.NameValue;
import core.SqlParser;
import core.exception.InvalidSqlException;
import sqlCmd.type.SqlKeyWord;

import java.util.List;
import java.util.Map;

// <Update>         ::=  UPDATE <TableName> SET <NameValueList> WHERE <Condition>
// <NameValueList>  ::=  <NameValuePair> | <NameValuePair> , <NameValueList>
// <NameValuePair>  ::=  <AttributeName> = <Value>
public class UpdateCommand implements DBCommand{
    private List<String> tokens;
    private String tableName;
    private String columnName;
    private Condition condition;
    private List<NameValue> nameValueList;
    Map<SqlKeyWord, Integer> keywordIdx;
    private int idxOfSET, idxOfWHERE;

    private void init(DBContext ctx) {
        tokens = ctx.getSqlTokens();
        keywordIdx = SqlParser.getKeywordIdx(tokens);
        idxOfSET = keywordIdx.get(SqlKeyWord.SET);
        idxOfWHERE = keywordIdx.get(SqlKeyWord.WHERE);
        condition = null;
    }

    @Override
    public void parseInput(DBContext ctx) throws Exception {
        init(ctx);

        // SET and WHERE is requisite for UPDATE clause
        if (idxOfSET == -1 || idxOfWHERE == -1) throw new InvalidSqlException("[ERROR]: Invalid query");

        tableName = ctx.getSqlTokens().get(1);
        SqlParser.checkName(tableName);

        // not sure yet
        columnName = ctx.getSqlTokens().get(3);
        SqlParser.checkName(columnName);

        nameValueList = SqlParser.toNameValueList(ctx, tokens.subList(idxOfSET +1, idxOfWHERE));

        condition = SqlParser.toCondition(ctx, tokens.subList(idxOfWHERE + 1, tokens.size()));
    }

    @Override
    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();
        engine.updateRow(ctx, tableName, nameValueList, condition);
        ctx.setResult(Config.OK());
    }
}
