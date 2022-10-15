package sqlCmd;

import core.Condition;
import core.DBEngine;
import core.DBContext;
import core.SqlParser;
import core.exception.InvalidSqlException;
import sqlCmd.type.SqlKeyWord;

import java.util.List;
import java.util.Map;

// <Select>         ::=  SELECT <WildAttribList> FROM <TableName> |
//                       SELECT <WildAttribList> FROM <TableName> WHERE <Condition>
// <WildAttribList> ::=  <AttributeList> | *
public class SelectCommand implements DBCommand {
    Map<SqlKeyWord, Integer> keywordIdx;
    private List<String> attributeList;
    private List<String> tokens;
    private String tableName;
    private SqlParser parser;
    private int idxOfFROM, idxOfWHERE;
    private Condition condition;

    private void init(DBContext ctx) {
        parser = new SqlParser();
        tokens = ctx.getSqlTokens();
        keywordIdx = SqlParser.getKeywordIdx(tokens);
        idxOfFROM = keywordIdx.get(SqlKeyWord.FROM);
        idxOfWHERE = keywordIdx.get(SqlKeyWord.WHERE);
        condition = null;
        attributeList = null;
    }

    // [ SELECT * FROM xxx WHERE xxx]
    // [ SELECT a , b , c FROM WHERE xxx]
    @Override
    public void parseInput(DBContext ctx) throws Exception {
        init(ctx);

        // FROM is requisite for SELECT clause
        if (idxOfFROM == -1) throw new InvalidSqlException("[ERROR]: Invalid query");

        tableName = tokens.get(idxOfFROM + 1);
        SqlParser.checkName(tableName);


        String tokensAfterFromBeforeWhere = String.join("" , tokens.subList(idxOfFROM+1, (idxOfWHERE==-1)?tokens.size()-1 : idxOfWHERE));
        SqlParser.checkNameShowInvalidQuery(tokensAfterFromBeforeWhere);

        if(!"*".equals(tokens.get(1))) {
            attributeList = parser.toAttributeList( ctx, tokens.subList(1, idxOfFROM));
        }

        if(idxOfWHERE != -1) {
            condition = SqlParser.toCondition(ctx, tokens.subList(idxOfWHERE+1, tokens.size()));
        }
    }
    @Override
    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();
        String tableContent = engine.selectTable(ctx, tableName, attributeList, condition);
        ctx.setResult(Config.OK() + "\n" + tableContent);
    }
}
