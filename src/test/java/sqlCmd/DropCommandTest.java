package sqlCmd;

import core.DBContext;
import core.DBEngine;
import core.exception.DataBaseNotExistsException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class DropCommandTest {

    DropCommand dropCommand;
    DBContext ctx;
    String testDB;
    String testTable;
    List<String> createDBTokens;
    List<String> createTableTokens;
    List<String> dropDBTokens;
    List<String> dropTableTokens;

    @BeforeEach
    public void setup() {
        this.dropCommand = new DropCommand();
        this.ctx = new DBContext();
        this.ctx.setCurrentDB(testDB);
        this.testDB = "testDB";
        this.testTable = "testTable";
        this.createDBTokens = Arrays.asList("CREATE", "DATABASE", testDB, ";");
        this.createTableTokens = Arrays.asList("CREATE", "TABLE", testTable, ";");
        this.dropDBTokens = Arrays.asList("DROP", "DATABASE", testDB, ";");
        this.dropTableTokens = Arrays.asList("DROP", "TABLE", testTable, ";");
    }

    @AfterEach
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(DBEngine.getDbRootDir() + File.separator + testDB));
    }

    @Test
    public void testDropDataBaseShouldSuccess() throws Exception {
        // create required db first
        CreateCommand createCommand = new CreateCommand();
        ctx.setQueryTokens(createDBTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        ctx.setQueryTokens(dropDBTokens);
        dropCommand.parseInput(ctx);
        dropCommand.executeCommand(ctx);
        Assert.assertFalse(new File("database" + File.separator + testDB).exists());
    }

    @Test
    public void testDropTableShouldSuccess() throws Exception {
        DBEngine e = new DBEngine();

        // create required db
        CreateCommand createCommand = new CreateCommand();
        ctx.setQueryTokens(createDBTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        ctx.setCurrentDB(testDB);

        // create required table
        ctx.setQueryTokens(createTableTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        Assertions.assertTrue(e.getFileOfTable(ctx, testTable).exists());

        ctx.setQueryTokens(dropTableTokens);
        dropCommand.parseInput(ctx);
        dropCommand.executeCommand(ctx);
        Assertions.assertFalse(e.getFileOfTable(ctx, testTable).exists());
    }

    @Test
    public void testDropDatabaseNotExistsShouldFail() throws Exception {
        Assertions.assertThrows(DataBaseNotExistsException.class, () -> {
            ctx.setQueryTokens(dropDBTokens);
            dropCommand.parseInput(ctx);
            dropCommand.executeCommand(ctx);
        });
    }

}