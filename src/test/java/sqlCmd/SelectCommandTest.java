package sqlCmd;

import core.DBContext;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

class SelectCommandTest {

    SelectCommand selectCommand;
    DBContext ctx;
    List<String> createDBTokens;
    List<String> createTableTokens;
    List<String> createTableWithAttributeListTokens;
    String testDB;
    String testTable;

    @BeforeEach
    public void setup() {
        this.selectCommand = new SelectCommand();
        this.ctx = new DBContext();
        this.testDB = "testDB";
        this.testTable = "testTable";
        this.createDBTokens = Arrays.asList("CREATE", "DATABASE", testDB, ";");
        this.createTableTokens = Arrays.asList("CREATE", "TABLE", testTable, ";");
        this.createTableWithAttributeListTokens = Arrays.asList("CREATE", "TABLE", testTable, "(", "name", ",", "email", ",", "address", ")", ";");

        ctx.setCurrentDB(testDB);
    }

}