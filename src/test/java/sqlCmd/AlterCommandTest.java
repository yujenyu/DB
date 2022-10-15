package sqlCmd;

import core.DBContext;
import core.DBEngine;
import core.Row;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlterCommandTest {
    AlterCommand alterCommand;
    DBContext ctx;
    String testDB;
    String testTable;
    List<String> createDBTokens;
    List<String> createTableTokens;
    List<String> createTableWithAttributeListTokens;
    List<String> alterTableAddTokens;
    List<String> alterTableDropTokens;
    private LinkedList<Row> rows;

    @BeforeEach
    public void setup() {
        this.alterCommand = new AlterCommand();
        this.ctx = new DBContext();
        this.testDB = "testDB";
        this.testTable = "testTable";
        this.createDBTokens = Arrays.asList("CREATE", "DATABASE", testDB, ";");
        this.createTableTokens = Arrays.asList("CREATE", "TABLE", testTable, ";");
        this.createTableWithAttributeListTokens = Arrays.asList("CREATE", "TABLE", testTable, "(", "name", ",", "mark", ",", "pass", ")", ";");
        this.alterTableAddTokens = Arrays.asList("ALTER", "TABLE", testTable, "ADD", "age", ";");
        this.alterTableDropTokens = Arrays.asList("ALTER", "TABLE", testTable, "DROP", "age", ";");

        ctx.setCurrentDB(testDB);
    }
    @AfterEach
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(DBEngine.getDbRootDir() + File.separator + testDB));
    }

    @Test
    public void testAlterTableAddShouldSuccess() throws Exception {
        DBEngine e = new DBEngine();

        // create required db first
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

        ctx.setQueryTokens(alterTableAddTokens);
        alterCommand.parseInput(ctx);
        alterCommand.executeCommand(ctx);

        // Check new column and row should be created
        // Assertions.assertTrue(......);

//        getColumnNames();
//        getAllRows();
    }

    @Test
    public void testAlterTableDropShouldSuccess() throws Exception {
        DBEngine e = new DBEngine();

        // create required db first
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

        // add new column
        ctx.setQueryTokens(alterTableAddTokens);
        alterCommand.parseInput(ctx);
        alterCommand.executeCommand(ctx);

        // drop column
        ctx.setQueryTokens(alterTableDropTokens);
        alterCommand.parseInput(ctx);
        alterCommand.executeCommand(ctx);

        // Check assigned column and row should be deleted
        // Assertions.assertTrue(......);
    }
}