package sqlCmd;

import core.DBContext;
import core.DBEngine;
import core.exception.DataBaseExistsException;
import core.exception.TableExistsException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqlCmd.type.SqlCmdType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateCommandTest {

    String dbRootDir;
    CreateCommand createCommand;
    DBContext ctx;
    String testDB;
    String testTable;
    List<String> createDBTokens;
    List<String> createTableTokens;
    List<String> createTableWithAttributeListTokens;

    @BeforeEach
    public void setup() {
        this.createCommand = new CreateCommand();
        this.ctx = new DBContext();
        this.testDB = "testDB";
        this.testTable = "testTable";
        this.createDBTokens = Arrays.asList("CREATE", "DATABASE", testDB, ";");
        this.createDBTokens = Arrays.asList("CREATE", "DATABASE", testDB, ";");
        this.createTableTokens = Arrays.asList("CREATE", "TABLE", testTable, ";");
        this.createTableWithAttributeListTokens = Arrays.asList("CREATE", "TABLE", testTable, "(", "name", ",", "mark", ",", "pass", ")", ";");
        this.dbRootDir = DBEngine.getDbRootDir();

        ctx.setCurrentDB(testDB);
    }
    @AfterEach
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(dbRootDir + File.separator + testDB));

    }
    @Test
    public void testCreateDataBaseShouldSuccess() throws Exception {
        ctx.setQueryTokens(createDBTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);
        Assert.assertTrue(new File(dbRootDir + File.separator + testDB).exists());
    }

    @Test
    public void testCreateDataBaseCmdInLowerShouldSuccess() throws Exception {
        List<String> tokensInLower = createDBTokens.stream().map(t -> t.toLowerCase()).collect(Collectors.toList());

        ctx.setQueryTokens(tokensInLower);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);
        Assert.assertTrue(new File(dbRootDir + File.separator + testDB).exists());
    }

    @Test
    public void testCreateDataBaseConflictWithExistsDBShouldFail() throws Exception {
        Assertions.assertThrows(DataBaseExistsException.class, () -> {
            // create conflicted db first
            ctx.setQueryTokens(createDBTokens);
            createCommand.parseInput(ctx);
            createCommand.executeCommand(ctx);


            ctx.setQueryTokens(createDBTokens);
            createCommand.parseInput(ctx);
            createCommand.executeCommand(ctx);
        });
    }

    @Test
    public void testCreateTableConflictWithExistsDBShouldFail() throws Exception {
        Assertions.assertThrows(TableExistsException.class, () -> {
            // create required db first
            ctx.setQueryTokens(createDBTokens);
            createCommand.parseInput(ctx);
            createCommand.executeCommand(ctx);

            // create required table
            ctx.setQueryTokens(createTableTokens);
            createCommand.parseInput(ctx);
            createCommand.executeCommand(ctx);

            ctx.setQueryTokens(createTableTokens);
            createCommand.parseInput(ctx);
            createCommand.executeCommand(ctx);
        });
    }

    @Test
    public void testCreateTableShouldSuccess() throws Exception {
        DBEngine e = new DBEngine();
        // create required db first
        ctx.setQueryTokens(createDBTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        ctx.setQueryTokens(createTableTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);
        Assert.assertTrue(e.getFileOfTable(ctx, testTable).exists());
    }

    @Test
    public void testCreateTableWithAttributeListShouldSuccess() throws Exception {
        DBEngine e = new DBEngine();
        // create required db first
        ctx.setQueryTokens(createDBTokens);
        ctx.setCmdType(SqlCmdType.CREATE);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        ctx.setQueryTokens(createTableWithAttributeListTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        Assert.assertTrue(e.getFileOfTable(ctx, testTable).exists());
    }

}