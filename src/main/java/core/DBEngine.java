package core;

import core.exception.ColumnSizeNotMatchException;
import core.exception.DataBaseExistsException;
import core.exception.DataBaseNotExistsException;
import core.exception.InvalidSqlException;
import core.exception.ParseException;
import core.exception.TableExistsException;
import core.exception.TableNotExistsException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sqlCmd.Config;
import sqlCmd.type.StatusType;

public class DBEngine {
    protected static String dbRootDir = "db";
    protected static final String SEP = File.separator;
    protected static final String extension = ".tab";

    public static String getDbRootDir () {
        return dbRootDir;
    }

    public static void setDBRootDir(String dir) {
        dbRootDir = dir;
    }

    public void createTable(DBContext ctx, String tbName, List<String> attributeList) throws Exception {
        if (!currentDBExists(ctx)) throw new DataBaseNotExistsException("[ERROR]: Unknown database");
        if (tbExists(ctx, tbName)) throw new TableExistsException("[ERROR]: Table already exists");

        Table table = new Table(tbName + extension);

        if (attributeList != null) {
            table.addColumns(attributeList);
        }

        writeTableToFile(ctx, table);
    }

    public void createDatabase(String dbName) throws Exception {
        createDbDirIfNotExists();
        File db = new File(dbRootDir + SEP + dbName);
        if (db.exists()) throw new DataBaseExistsException("[ERROR]: Database already exists");
        db.mkdir();
    }

    private void createDbDirIfNotExists() {
        File dbRootDir = new File(DBEngine.dbRootDir);

        if(! dbRootDir.exists()) {
            dbRootDir.mkdir();
        }
    }

    public void dropTable(DBContext ctx, String tbName) throws Exception {
        if (!currentDBExists(ctx))   throw new DataBaseNotExistsException("[ERROR]: Unknown database");
        if (!tbExists(ctx, tbName)) throw new TableNotExistsException("[ERROR]: Table does not exist");
        File table = new File(dbRootDir + SEP + ctx.getCurrentDB() + SEP + tbName + extension);
        table.delete();
    }

    public void dropDatabase(String dbName) throws Exception {
        File db = new File(dbRootDir + SEP + dbName);
        if (!db.exists()) throw new DataBaseNotExistsException("[ERROR]: Unknown database");
        FileUtils.deleteDirectory(db);
    }

    public void useDatabase(DBContext ctx, String dbName) throws Exception {
        if (!dbExists(dbName)) throw new DataBaseNotExistsException("[ERROR]: Unknown database");
        ctx.setCurrentDB(dbName);
    }

    public void alterTable(DBContext ctx, String tbName, String attributeName, String alterationType)
            throws InvalidSqlException, IOException, ParseException {

        File tableFile = getFileOfTable(ctx, tbName);
        Table table = readTableFromFile(tableFile);
        switch (alterationType) {
            case "ADD":
            case "add":
                table.addSingleColumn(attributeName);
                break;
            case "DROP":
            case "drop":
                table.removeColumn(attributeName);
                break;
            default:
                throw new InvalidSqlException(Config.getOutput(StatusType.ERROR) + ": Invalid query");
        }
        writeTableToFile(ctx, table);
    }

    public void insertIntoTable(DBContext ctx, String tbName, List<String> values)
            throws InvalidSqlException, IOException, ParseException, ColumnSizeNotMatchException {

        File tableFile = getFileOfTable(ctx, tbName);
        Table table = readTableFromFile(tableFile);
        // System.out.println("values: " + values);
        table.addRow(values);
        writeTableToFile(ctx, table);
    }

    public void updateRow(DBContext ctx, String tbName, List<NameValue> nameValueList,  Condition condition)
            throws InvalidSqlException, IOException, ParseException {

        File tableFile = getFileOfTable(ctx, tbName);
        Table table = readTableFromFile(tableFile);
        table.updateRows(nameValueList, condition);
        writeTableToFile(ctx, table);
    }

    public void deleteRow(DBContext ctx, String tbName, Condition condition)
            throws InvalidSqlException, IOException, ParseException {

        File tableFile = getFileOfTable(ctx, tbName);
        Table table = readTableFromFile(tableFile);
        table.removeRow(tbName, condition);
        writeTableToFile(ctx, table);
    }

    public File getFileOfTable(DBContext ctx, String tbName) throws InvalidSqlException {
        if (!currentDBExists(ctx)) throw new InvalidSqlException("[ERROR]: No database be assigned");

        //if (!tbExists(ctx, tbName)) throw new InvalidSqlException("No such table.");
        return new File(dbRootDir + SEP + ctx.getCurrentDB() + SEP + tbName + extension);
    }

    public String selectTable(DBContext ctx, String tbName, List<String> attributeList, Condition condition)
            throws InvalidSqlException, IOException, ParseException, TableNotExistsException {

        File tableFile = getFileOfTable(ctx, tbName);

        if (!tableFile.exists()) throw new TableNotExistsException("[ERROR]: Table does not exist");
        Table table = readTableFromFile(tableFile);
        // System.out.println("DEBUG: \n" + table.getAllRows());

        if(attributeList == null) {
            attributeList = table.getColumnNames();
        }
        return table.toString(condition, attributeList);
    }

    private boolean currentDBExists(DBContext ctx) {
        return new File(dbRootDir + SEP + ctx.getCurrentDB()).exists();
    }

    private boolean dbExists(String dbName) {
        return new File(dbRootDir + SEP + dbName).exists();
    }

    private boolean tbExists(DBContext ctx, String tbName) {
        return currentDBExists(ctx) &&
               new File(dbRootDir + SEP + ctx.getCurrentDB() + File.separator + tbName + ".tab").exists();
    }

    private Table readTableFromFile(File tableFile) throws IOException, ParseException {
        FileInputStream readFromFile = new FileInputStream(tableFile);
        ObjectInputStream readObjIn = new ObjectInputStream(readFromFile);
        try {
            Table tb = (Table)readObjIn.readObject();
            readObjIn.close();
            readFromFile.close();
            return tb;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ParseException("[ERROR]: Couldn't deserialize core.Table file");
        }
    }

    private void writeTableToFile(DBContext ctx, Table table) {
        try {
            FileOutputStream fOut = new FileOutputStream(dbRootDir + SEP + ctx.getCurrentDB() + SEP + table.getFilename());
            ObjectOutputStream objOut = new ObjectOutputStream(fOut);
            objOut.writeObject(table);
            objOut.close();
            fOut.close();
        } catch (IOException e) {
            throw new RuntimeException("[ERROR]: Couldn't serialize core.Table to file");
            // e.printStackTrace();
        }
    }
}
