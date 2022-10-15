package core;

import core.exception.InvalidSqlException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import sqlCmd.type.SqlCmdType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlParserTest {

    SqlParser sqlParser;
    DBContext ctx;

    @BeforeEach
    public void setup() {
        sqlParser = new SqlParser();
        ctx = new DBContext();
    }

    @Test
    public void testTokenizeShouldPass() throws InvalidSqlException {
        String input = "select * from marks where name == 'jordan';";
        List<String> expected = Arrays.asList("select", "*", "from", "marks", "where", "name", "==", "'jordan'", ";");
        assertEquals(SqlParser.tokenize(input), expected);
    }

    @Test
    public void testToAttributeListOfCreateTableShouldEqual() throws InvalidSqlException {
        List<String> attributeListTokens = Arrays.asList("(", "attr", ",", "attr", ",", "attrxxx", ")", ";");
        List<String> expectedAttributeList = Arrays.asList("attr", "attr", "attrxxx");

        ctx.setCmdType(SqlCmdType.CREATE);
        assertEquals(sqlParser.toAttributeList(ctx, attributeListTokens), expectedAttributeList);
    }

    @Test
    public void testToAttributeListOfSelectTableShouldEqual() throws InvalidSqlException {
        List<String> attributeListTokens = Arrays.asList("attr", ",", "attr", ",", "attrxxx", ";");
        List<String> expectedAttributeList = Arrays.asList("attr", "attr", "attrxxx");

        ctx.setCmdType(SqlCmdType.SELECT);
        assertEquals(sqlParser.toAttributeList(ctx, attributeListTokens), expectedAttributeList);

    }

    @Test
    public void testToAttributeListShouldThrowException() throws InvalidSqlException {
        Assertions.assertThrows(InvalidSqlException.class, () -> {
            List<String> attributeListTokens = Arrays.asList("(", "attr", ",", "attr", ",", "attrxxx", ")");
            ctx.setCmdType(SqlCmdType.CREATE);
            sqlParser.toAttributeList(ctx, attributeListTokens);
        });
    }
}