package sqlCmd;

import core.DBContext;
import core.DBEngine;
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

class UseCommandTest {
    UseCommand useCommand;
    DBContext ctx;
    String testDB;
    List<String> createDBTokens;
    List<String> useDBTokens;

    @BeforeEach
    public void setup() {
        this.useCommand = new UseCommand();
        this.ctx = new DBContext();
        this.testDB = "testDB";
        this.createDBTokens = Arrays.asList("CREATE", "DATABASE", testDB, ";");
        this.useDBTokens = Arrays.asList("USE", testDB, ";");

        ctx.setCurrentDB(testDB);
    }
    @AfterEach
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(DBEngine.getDbRootDir()  + File.separator + testDB));
    }

    @Test
    public void testUseDataBaseShouldSuccess() throws Exception {
        // create required db first
        CreateCommand createCommand = new CreateCommand();
        ctx.setQueryTokens(createDBTokens);
        createCommand.parseInput(ctx);
        createCommand.executeCommand(ctx);

        ctx.setQueryTokens(useDBTokens);
        useCommand.parseInput(ctx);
        useCommand.executeCommand(ctx);

        Assert.assertTrue(ctx.getCurrentDB().equals(testDB));
    }

}

