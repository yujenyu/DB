package sqlCmd.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlCmdTypeTest {
    @Test
    public void shouldSuccess() {
        Assertions.assertEquals(SqlCmdType.valueOf("SELECT"), SqlCmdType.SELECT);
        Assertions.assertEquals(SqlCmdType.valueOf("CREATE"), SqlCmdType.CREATE);
        Assertions.assertEquals(SqlCmdType.valueOf("USE"), SqlCmdType.USE);
        Assertions.assertEquals(SqlCmdType.valueOf("DROP"), SqlCmdType.DROP);
        Assertions.assertEquals(SqlCmdType.valueOf("ALTER"), SqlCmdType.ALTER);
        Assertions.assertEquals(SqlCmdType.valueOf("UPDATE"), SqlCmdType.UPDATE);
    }

}