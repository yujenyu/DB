package sqlCmd;

import core.DBEngine;
import core.DBContext;
import core.SqlParser;
import core.exception.InvalidSqlException;
import sqlCmd.type.SqlKeyWord;
import sqlCmd.type.StatusType;

import java.util.List;

// <Create>          ::=  <CreateDatabase> | <CreateTable>
// <CreateDatabase>  ::=  CREATE DATABASE <DatabaseName>
// <CreateTable>     ::=  CREATE TABLE <TableName> | CREATE TABLE <TableName> ( <AttributeList> )
// <AttributeList>   ::=  <AttributeName> | <AttributeName> , <AttributeList>
public class CreateCommand implements DBCommand {

    private SqlKeyWord createType;
    private String createName;
    private List<String> attributeList;

    @Override
    public void parseInput(DBContext ctx) throws Exception {
        List<String> sqlTokens = ctx.getSqlTokens();

        createType = SqlKeyWord.getEnum(sqlTokens.get(1));
        createName = sqlTokens.get(2);

        // CREATE TABLE tbName;
        // CREATE TABLE tbName (, attr, xxx, bbb ) ;
        // must be create table (...)
        if ("(".equals(sqlTokens.get(3))) {
            attributeList = SqlParser.toAttributeList(
                    ctx,
                    sqlTokens.subList(3, sqlTokens.size())
            );
        }
    }

    @Override
    public void executeCommand(DBContext ctx) throws Exception {
        DBEngine engine = new DBEngine();

        switch (createType) {
            case DATABASE:
                engine.createDatabase(createName);
                break;
            case TABLE:
                engine.createTable(ctx, createName, attributeList);
                break;
            default:
                throw new InvalidSqlException(Config.getOutput(StatusType.ERROR) + " : invalid create type.");
        }
        ctx.setResult(Config.getOutput(StatusType.OK));
    }
}
