package core;

import sqlCmd.type.SqlCmdType;

import java.util.ArrayList;
import java.util.List;

public class DBContext {
    private List<String> queryTokens;
    private String currentDB;
    private String result;
    private SqlCmdType cmdType;

    public DBContext() {
        queryTokens = new ArrayList<>();
    }

    public void setQueryTokens(List<String> queryTokens) {
        this.queryTokens = queryTokens;
    }

    public void setCmdType(SqlCmdType cmdType) {
        this.cmdType = cmdType;
    }

    public SqlCmdType getCmdType() {
        return this.cmdType;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String output) {
        this.result = output;
    }

    public List<String> getSqlTokens() {
        return this.queryTokens;
    }

    public String getCurrentDB() {
        return this.currentDB;
    }

    public void setCurrentDB(String dbName) {
        this.currentDB = dbName;
    }
}
