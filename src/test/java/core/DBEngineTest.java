package core;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DBEngineTest {


    String dbRootDir;
    String testDB;
    DBEngine engine;

    @BeforeEach
    public void setup() {
        dbRootDir = "testDBRoot";
        testDB = "testDB";
        engine = new DBEngine();

    }

    @AfterEach
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(dbRootDir));
    }

}