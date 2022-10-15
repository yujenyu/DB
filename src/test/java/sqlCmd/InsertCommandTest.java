package sqlCmd;

import core.Condition;
import core.DBContext;
import core.DBEngine;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqlCmd.type.SqlCmdType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InsertCommandTest {
    InsertCommand insertCommand;
    DBContext ctx;
    String testDB;
    String testTable;
    List<String> createDBTokens;
    List<String> createTableTokens;
    List<String> insertIntoTableTokens;

    @BeforeEach
    public void setup() {
        this.insertCommand = new InsertCommand();
        this.ctx = new DBContext();
        this.testDB = "testDB";
        this.testTable = "testTable";
        this.createDBTokens = Arrays.asList("CREATE", "DATABASE", testDB, ";");
        this.createTableTokens = Arrays.asList("CREATE", "TABLE", testTable, "(", "name", ",", "age", ",", "handsome", ")", ";");
        this.insertIntoTableTokens = Arrays.asList("INSERT", "INTO", testTable, "VALUES",
                "(", "'", "Steven", "'", ",", "65", ",", "true", ")", ";");
    }

    @AfterEach
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(DBEngine.getDbRootDir() + File.separator + testDB));

    }

    @Test
    public void insertIntoTableShouldSuccess() throws Exception {
        CreateCommand createCommand = new CreateCommand();

        // create required db first
        ctx.setQueryTokens(createDBTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        // use db
        ctx.setCurrentDB(testDB);

        // create required table
        ctx.setQueryTokens(createTableTokens);
        ctx.setCmdType(SqlCmdType.CREATE);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        // set command type
        ctx.setCmdType(SqlCmdType.INSERT);

        ctx.setQueryTokens(insertIntoTableTokens);
        this.insertCommand.parseInput(ctx);
        this.insertCommand.executeCommand(ctx);

        String expected = new StringBuilder()
                .append("id\tname\tage\thandsome\t")
                .append("\n")
                .append("1\tSteven\t65\ttrue\t")
                .append("\n")
                .toString();

        DBEngine engine = new DBEngine();
        Assertions.assertEquals(expected, engine.selectTable(ctx, testTable, null, null));
    }
}