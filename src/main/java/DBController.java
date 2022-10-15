import core.DBContext;
import core.SqlParser;
import core.exception.InvalidSqlException;
import sqlCmd.*;
import sqlCmd.type.SqlCmdType;
import sqlCmd.type.StatusType;

import java.util.HashMap;
import java.util.List;

public class DBController {

    private static final HashMap<SqlCmdType, DBCommand> commandTypes = new HashMap<>() {{
        put(SqlCmdType.USE, new UseCommand());
        put(SqlCmdType.CREATE, new CreateCommand());
        put(SqlCmdType.DROP, new DropCommand());
        put(SqlCmdType.ALTER, new AlterCommand());
        put(SqlCmdType.INSERT, new InsertCommand());
        put(SqlCmdType.SELECT, new SelectCommand());
        put(SqlCmdType.UPDATE, new UpdateCommand());
        put(SqlCmdType.DELETE, new DeleteCommand());
        // put(SqlCmdType.JOIN, new JoinCommand());
    }};

    private String input;
    private final List<String> tokens;
    private DBContext ctx;

    DBController(String sqlString, DBContext ctx) throws InvalidSqlException {
        this.input = sqlString;
        this.ctx = ctx;
        this.tokens = SqlParser.tokenize(this.input);

        // DBContext setup
        this.ctx.setQueryTokens(this.tokens);
        this.ctx.setCmdType(SqlCmdType.getEnum(this.tokens.get(0)));
    }

    protected String executeQuery() throws Exception {
        DBCommand cmd = commandTypes.get(ctx.getCmdType());
        cmd.parseInput(ctx);
        cmd.executeCommand(ctx);

        return ctx.getResult();
    }

    public DBContext getCtx() {
        return this.ctx;
    }

    public List<String> getTokens() {
        return this.tokens;
    }
}
