import core.CmdToResult;
import core.DBContext;
import core.DBEngine;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqlCmd.Config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBControllerTest {
    private static String testRootDir = "testDBRootDir";
    Map<String, String> typeToSqlString;
    DBContext ctx;
    String testDB;
    String testTable;

    @BeforeEach
    public void setup() {
        this.testDB = "testDB";
        this.testTable = "testTable";
        this.ctx = new DBContext();
        this.typeToSqlString= new HashMap<>() {{
            put("create_database", String.format("CREATE DATABASE %s ;",  testDB));
            put("drop_database", String.format("DROP DATABASE %s ;", testDB));
        }};
    }

    @AfterEach
    public void teardown() throws IOException {
        // System.out.println("DEBUG: " + DBEngine.getDbRootDir());;
        FileUtils.deleteDirectory(new File(DBEngine.getDbRootDir()));

    }

    @Test
    public void testSqlCreate() throws Exception {
        DBController controller = new DBController(typeToSqlString.get("create_database"), new DBContext());
        String expectedResult = "[OK]";


        controller.executeQuery();
        assertEquals(controller.getCtx().getResult(), expectedResult);
    }

    @Test
    public void testSqlDrop() throws Exception {
        DBController createController = new DBController(typeToSqlString.get("create_database"), new DBContext());
        createController.executeQuery();


        DBController dropController = new DBController(typeToSqlString.get("drop_database"), new DBContext());
        String expectedResult = "[OK]";


        dropController.executeQuery();
        assertEquals(dropController.getCtx().getResult(), expectedResult);
    }

    @Test
    public void testBasicTranscript() throws Exception {
        DBEngine.setDBRootDir(testRootDir);
        final List<CmdToResult> cmdToExpectedResult = new LinkedList<>() {{
            add(new CmdToResult("CREATE DATABASE markbook;", Config.OK()));
            add(new CmdToResult("USE markbook;" ,Config.OK()));
            add(new CmdToResult("CREATE TABLE marks (name, mark, pass);", Config.OK()));
            add(new CmdToResult("INSERT INTO marks VALUES ('Steve', 65, true);", Config.OK()));
            add(new CmdToResult("INSERT INTO marks VALUES ('Dave', 55, true);", Config.OK()));
            add(new CmdToResult("INSERT INTO marks VALUES ('Bob', 35, false);", Config.OK()));
            add(new CmdToResult("INSERT INTO marks VALUES ('Clive', 20, false);", Config.OK()));
            add(new CmdToResult("SELECT * FROM marks;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("2\tDave\t55\ttrue\t\n")
                            .append("3\tBob\t35\tfalse\t\n")
                            .append("4\tClive\t20\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("SELECT * FROM marks WHERE name != 'Dave';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("3\tBob\t35\tfalse\t\n")
                            .append("4\tClive\t20\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("SELECT * FROM marks WHERE pass == true;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("2\tDave\t55\ttrue\t\n")
                            .toString()));
            add(new CmdToResult("SELECT * FROM marks WHERE name == 'Clive';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("4\tClive\t20\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("UPDATE marks SET mark = 38 WHERE name == 'Clive';", Config.OK()));
            add(new CmdToResult("SELECT * FROM marks WHERE name == 'Clive';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("4\tClive\t38\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("DELETE FROM marks WHERE name == 'Dave';", Config.OK()));
            add(new CmdToResult("SELECT * FROM marks;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("3\tBob\t35\tfalse\t\n")
                            .append("4\tClive\t38\tfalse\t\n")
                            .toString()
            ));
            add(new CmdToResult("DELETE FROM marks WHERE mark < 40;", Config.OK()));
            add(new CmdToResult("SELECT * FROM marks;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .toString()
            ));
        }};

        for(CmdToResult test : cmdToExpectedResult) {
            System.out.println("sql: " + test.getCmd());

            DBController controller = new DBController(test.getCmd(), ctx);
            controller.executeQuery();

            System.out.println("result: " + test.getExpectedResult());
            // Utils.listDbRootDir();
            Assertions.assertEquals(ctx.getResult(), test.getExpectedResult());
        }
    }

    @Test
    public void testLowercaseBasicTranscript() throws Exception {
        DBEngine.setDBRootDir(testRootDir);
        final List<CmdToResult> cmdToExpectedResult = new LinkedList<>() {{
            add(new CmdToResult("create database markbook;", Config.OK()));
            add(new CmdToResult("use markbook;", Config.OK()));
            add(new CmdToResult("create table marks (name, mark, pass);", Config.OK()));
            add(new CmdToResult("insert into marks values ('Steve', 65, true);", Config.OK()));
            add(new CmdToResult("insert into marks values ('Dave', 55, true);", Config.OK()));
            add(new CmdToResult("insert into marks values ('Bob', 35, false);", Config.OK()));
            add(new CmdToResult("insert into marks values ('Clive', 20, false);", Config.OK()));
            add(new CmdToResult("select * from marks;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("2\tDave\t55\ttrue\t\n")
                            .append("3\tBob\t35\tfalse\t\n")
                            .append("4\tClive\t20\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("select * from marks where name != 'Dave';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("3\tBob\t35\tfalse\t\n")
                            .append("4\tClive\t20\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("select * from marks where pass == true;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("2\tDave\t55\ttrue\t\n")
                            .toString()));
            add(new CmdToResult("select * from marks where name == 'Clive';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("4\tClive\t20\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("update marks set mark = 38 where name == 'Clive';", Config.OK()));
            add(new CmdToResult("select * from marks where name == 'Clive';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("4\tClive\t38\tfalse\t\n")
                            .toString()));
            add(new CmdToResult("delete from marks where name == 'Dave';", Config.OK()));
            add(new CmdToResult("select * from marks;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .append("3\tBob\t35\tfalse\t\n")
                            .append("4\tClive\t38\tfalse\t\n")
                            .toString()
            ));
            add(new CmdToResult("delete from marks where mark < 40;", Config.OK()));
            add(new CmdToResult("select * from marks;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tmark\tpass\t\n")
                            .append("1\tSteve\t65\ttrue\t\n")
                            .toString()
            ));
        }};

        for(CmdToResult test : cmdToExpectedResult) {
            System.out.println("sql: " + test.getCmd());

            DBController controller = new DBController(test.getCmd(), ctx);
            controller.executeQuery();

            System.out.println("result: " + test.getExpectedResult());
            // Utils.listDbRootDir();
            Assertions.assertEquals(ctx.getResult(), test.getExpectedResult());
        }
    }

    @Test
    public void testSubstantialTranscript() throws Exception {
        DBEngine.setDBRootDir(testRootDir);
        final List<CmdToResult> cmdToExpectedResult = new LinkedList<>() {{
            add(new CmdToResult("USE imdb;", "[ERROR]: Unknown database"));
            add(new CmdToResult("DROP TABLE actors;", "[ERROR]: Unknown database"));
            add(new CmdToResult("DROP TABLE movies;", "[ERROR]: Unknown database"));
            add(new CmdToResult("DROP TABLE roles;", "[ERROR]: Unknown database"));
            add(new CmdToResult("DROP DATABASE imdb;", "[ERROR]: Unknown database"));
            add(new CmdToResult("CREATE DATABASE imdb;", Config.OK()));
            add(new CmdToResult("USE imdb;", Config.OK()));
            add(new CmdToResult("CREATE TABLE actors (name, nationality, awards);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('Hugh Grant', 'British', 3);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('Toni Collette', 'Australian', 12);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('James Caan', 'American', 8);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('Emma Thompson', 'British', 10);", Config.OK()));
            add(new CmdToResult("CREATE TABLE movies (name, genre);", Config.OK()));
            add(new CmdToResult("INSERT INTO movies VALUES ('Mickey Blue Eyes', 'Comedy');", Config.OK()));
            add(new CmdToResult("INSERT INTO movies VALUES ('About a Boy', 'Comedy');", Config.OK()));
            add(new CmdToResult("INSERT INTO movies VALUES ('Sense and Sensibility', 'Period Drama');", Config.OK()));
            add(new CmdToResult("SELECT id FROM movies WHERE name == 'Mickey Blue Eyes';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("1\t\n")
                            .toString()));
            add(new CmdToResult("SELECT id FROM movies WHERE name == 'About a Boy';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("2\t\n")
                            .toString()));
            add(new CmdToResult("SELECT id FROM movies WHERE name == 'Sense and Sensibility';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("3\t\n")
                            .toString()));
            add(new CmdToResult("SELECT id FROM actors WHERE name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("1\t\n")
                            .toString()));
            add(new CmdToResult("SELECT id FROM actors WHERE name == 'Toni Collette';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("2\t\n")
                            .toString()));
            add(new CmdToResult("SELECT id FROM actors WHERE name == 'James Caan';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("3\t\n")
                            .toString()));
            add(new CmdToResult("SELECT id FROM actors WHERE name == 'Emma Thompson';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("4\t\n")
                            .toString()));
            add(new CmdToResult("CREATE TABLE roles (name, movie_id, actor_id);", Config.OK()));
            add(new CmdToResult("INSERT INTO roles VALUES ('Edward', 3, 1);", Config.OK()));
            add(new CmdToResult("INSERT INTO roles VALUES ('Frank', 1, 3);", Config.OK()));
            add(new CmdToResult("INSERT INTO roles VALUES ('Fiona', 2, 2);", Config.OK()));
            add(new CmdToResult("INSERT INTO roles VALUES ('Elinor', 3, 4);", Config.OK()));
            add(new CmdToResult("SELECT * FROM actors WHERE awards < 5;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\n")
                            .toString()));
            add(new CmdToResult("ALTER TABLE actors ADD age;", Config.OK()));
            add(new CmdToResult("SELECT * FROM actors;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\tage\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\t\n")
                            .append("2\tToni Collette\tAustralian\t12\t\t\n")
                            .append("3\tJames Caan\tAmerican\t8\t\t\n")
                            .append("4\tEmma Thompson\tBritish\t10\t\t\n")
                            .toString()));
            add(new CmdToResult("UPDATE actors SET age = 45 WHERE name == 'Hugh Grant';", Config.OK()));
            add(new CmdToResult("SELECT * FROM actors WHERE name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\tage\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t45\t\n")
                            .toString()));
            add(new CmdToResult("SELECT nationality FROM actors WHERE name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("nationality\t\n")
                            .append("British\t\n")
                            .toString()));
            add(new CmdToResult("ALTER TABLE actors DROP age;", Config.OK()));
            add(new CmdToResult("SELECT * FROM actors WHERE name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\n")
                            .toString()));
//            add(new CmdToResult("SELECT * FROM actors WHERE (awards > 5) AND (nationality == 'British');", Config.OK() + "\n" +
//                    new StringBuilder()
//                            .append("id\tname\tnationality\tawards\t\n")
//                            .append("4\tEmma Thompson\tBritish\t10\t\n")
//                            .toString()));
//            add(new CmdToResult("SELECT * FROM actors WHERE (awards > 5) AND ((nationality == 'British') OR (nationality == 'Australian'));", Config.OK() + "\n" +
//                    new StringBuilder()
//                            .append("id\tname\tnationality\tawards\t\n")
//                            .append("2\tToni Collette\tAustralian\t12\t\n")
//                            .append("4\tEmma Thompson\tBritish\t10\t\n")
//                            .toString()));
            add(new CmdToResult("SELECT * FROM actors WHERE name LIKE 'an';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\n")
                            .append("3\tJames Caan\tAmerican\t8\t\n")
                            .toString()));
            add(new CmdToResult("SELECT * FROM actors WHERE awards >= 10;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("2\tToni Collette\tAustralian\t12\t\n")
                            .append("4\tEmma Thompson\tBritish\t10\t\n")
                            .toString()));
            add(new CmdToResult("DELETE FROM actors WHERE name == 'Hugh Grant';", Config.OK()));
            add(new CmdToResult("DELETE FROM actors WHERE name == 'James Caan';", Config.OK()));
            add(new CmdToResult("DELETE FROM actors WHERE name == 'Emma Thompson';", Config.OK()));

            // Havent finished JoinCommand
//            add(new CmdToResult("JOIN actors AND roles ON id AND actor_id;", Config.OK() +
//                    new StringBuilder()
//                            .append("id\tactors.name\tactors.nationality\tactors.awards\troles.name\troles.movie_id\troles.actor_id\t\n")
//                            .append("2\tToni Collette\tAustralian\t12\tFiona\t2\t2\t\n")
//                            .toString()));
//            add(new CmdToResult("JOIN movies AND roles ON id AND movie_id;", Config.OK() +
//                    new StringBuilder()
//                            .append("id\tmovies.name\tmovies.genre\troles.name\troles.name\troles.movie_id\troles.actor_id\t\n")
//                            .append("1\tMickey Blue Eyes\tComedy\tFrank\t1\t3\t\n")
//                            .append("2\tAbout a Boy\tComedy\tFiona\t2\t2\t\n")
//                            .append("3\tSense and Sensibility\tPeriod Drama\tEdward\t3\t1\t\n")
//                            .append("4\tSense and Sensibility\tPeriod Drama\tElinor\t3\t4\t\n")
//                            .toString()));

            add(new CmdToResult("DROP TABLE actors;", Config.OK()));
            add(new CmdToResult("SELECT * FROM actors;", "[ERROR]: Table does not exist"));
            add(new CmdToResult("DROP DATABASE imdb;", Config.OK()));
            add(new CmdToResult("USE imdb;", "[ERROR]: Unknown database"));
        }};

        for(CmdToResult test : cmdToExpectedResult) {
            System.out.println("sql: " + test.getCmd());

            try {
                DBController controller = new DBController(test.getCmd(), ctx);
                controller.executeQuery();
            } catch (Exception e) {
                String exceptionMsg = e.toString();
                System.out.println("result: " + exceptionMsg);
                Assertions.assertEquals(exceptionMsg, test.getExpectedResult());
                continue;
            }

            System.out.println("result: " + test.getExpectedResult());
            // Utils.listDbRootDir();
            Assertions.assertEquals(ctx.getResult(), test.getExpectedResult());
        }
    }

    @Test
    public void testLowercaseSubstantialTranscript() throws Exception {
        DBEngine.setDBRootDir(testRootDir);
        final List<CmdToResult> cmdToExpectedResult = new LinkedList<>() {{
            add(new CmdToResult("use imdb;", "[ERROR]: Unknown database"));
            add(new CmdToResult("drop table actors;", "[ERROR]: Unknown database"));
            add(new CmdToResult("drop table movies;", "[ERROR]: Unknown database"));
            add(new CmdToResult("drop table roles;", "[ERROR]: Unknown database"));
            add(new CmdToResult("drop database imdb;", "[ERROR]: Unknown database"));
            add(new CmdToResult("create database imdb;", Config.OK()));
            add(new CmdToResult("use imdb;", Config.OK()));
            add(new CmdToResult("create table actors (name, nationality, awards);", Config.OK()));
            add(new CmdToResult("insert into actors values ('Hugh Grant', 'British', 3);", Config.OK()));
            add(new CmdToResult("insert into actors values ('Toni Collette', 'Australian', 12);", Config.OK()));
            add(new CmdToResult("insert into actors values ('James Caan', 'American', 8);", Config.OK()));
            add(new CmdToResult("insert into actors values ('Emma Thompson', 'British', 10);", Config.OK()));
            add(new CmdToResult("create table movies (name, genre);", Config.OK()));
            add(new CmdToResult("insert into movies values ('Mickey Blue Eyes', 'Comedy');", Config.OK()));
            add(new CmdToResult("insert into movies values ('About a Boy', 'Comedy');", Config.OK()));
            add(new CmdToResult("insert into movies values ('Sense and Sensibility', 'Period Drama');", Config.OK()));
            add(new CmdToResult("select id from movies where name == 'Mickey Blue Eyes';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("1\t\n")
                            .toString()));
            add(new CmdToResult("select id from movies where name == 'About a Boy';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("2\t\n")
                            .toString()));
            add(new CmdToResult("select id from movies where name == 'Sense and Sensibility';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("3\t\n")
                            .toString()));
            add(new CmdToResult("select id from actors where name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("1\t\n")
                            .toString()));
            add(new CmdToResult("select id from actors where name == 'Toni Collette';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("2\t\n")
                            .toString()));
            add(new CmdToResult("select id from actors where name == 'James Caan';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("3\t\n")
                            .toString()));
            add(new CmdToResult("select id from actors where name == 'Emma Thompson';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\t\n")
                            .append("4\t\n")
                            .toString()));
            add(new CmdToResult("create table roles (name, movie_id, actor_id);", Config.OK()));
            add(new CmdToResult("insert into roles values ('Edward', 3, 1);", Config.OK()));
            add(new CmdToResult("insert into roles values ('Frank', 1, 3);", Config.OK()));
            add(new CmdToResult("insert into roles values ('Fiona', 2, 2);", Config.OK()));
            add(new CmdToResult("insert into roles values('Elinor', 3, 4);", Config.OK()));
            add(new CmdToResult("select * from actors where awards < 5;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\n")
                            .toString()));
            add(new CmdToResult("alter table actors add age;", Config.OK()));
            add(new CmdToResult("select * from actors;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\tage\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\t\n")
                            .append("2\tToni Collette\tAustralian\t12\t\t\n")
                            .append("3\tJames Caan\tAmerican\t8\t\t\n")
                            .append("4\tEmma Thompson\tBritish\t10\t\t\n")
                            .toString()));
            add(new CmdToResult("update actors set age = 45 where name == 'Hugh Grant';", Config.OK()));
            add(new CmdToResult("select * from actors where name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\tage\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t45\t\n")
                            .toString()));
            add(new CmdToResult("select nationality from actors where name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("nationality\t\n")
                            .append("British\t\n")
                            .toString()));
            add(new CmdToResult("alter table actors drop age;", Config.OK()));
            add(new CmdToResult("select * from actors where name == 'Hugh Grant';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\n")
                            .toString()));
//            add(new CmdToResult("select * from actors where (awards > 5) and (nationality == 'British');", Config.OK() +
//                    new StringBuilder()
//                            .append("id\tname\tnationality\tawards\t\n")
//                            .append("4\tEmma Thompson\tBritish\t10\t\n")
//                            .toString()));
//            add(new CmdToResult("select * from actors where (awards > 5) and ((nationality == 'British') or (nationality == 'Australian'));", Config.OK() +
//                    new StringBuilder()
//                            .append("id\tname\tnationality\tawards\t\n")
//                            .append("2\tToni Collette\tAustralian\t12\t\n")
//                            .append("4\tEmma Thompson\tBritish\t10\t\n")
//                            .toString()));
            add(new CmdToResult("select * from actors where name LIKE 'an';", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("1\tHugh Grant\tBritish\t3\t\n")
                            .append("3\tJames Caan\tAmerican\t8\t\n")
                            .toString()));
            add(new CmdToResult("select * from actors where awards >= 10;", Config.OK() + "\n" +
                    new StringBuilder()
                            .append("id\tname\tnationality\tawards\t\n")
                            .append("2\tToni Collette\tAustralian\t12\t\n")
                            .append("4\tEmma Thompson\tBritish\t10\t\n")
                            .toString()));
            add(new CmdToResult("delete from actors where name == 'Hugh Grant';", Config.OK()));
            add(new CmdToResult("delete from actors where name == 'James Caan';", Config.OK()));
            add(new CmdToResult("delete from actors where name == 'Emma Thompson';", Config.OK()));

            // Havent finished JoinCommand
//            add(new CmdToResult("join actors and roles on id and actor_id;", Config.OK() +
//                    new StringBuilder()
//                            .append("id\tactors.name\tactors.nationality\tactors.awards\troles.name\troles.movie_id\troles.actor_id\t\n")
//                            .append("2\tToni Collette\tAustralian\t12\tFiona\t2\t2\t\n")
//                            .toString()));
//            add(new CmdToResult("join movies and roles on id and movie_id;", Config.OK() +
//                    new StringBuilder()
//                            .append("id\tmovies.name\tmovies.genre\troles.name\troles.name\troles.movie_id\troles.actor_id\t\n")
//                            .append("1\tMickey Blue Eyes\tComedy\tFrank\t1\t3\t\n")
//                            .append("2\tAbout a Boy\tComedy\tFiona\t2\t2\t\n")
//                            .append("3\tSense and Sensibility\tPeriod Drama\tEdward\t3\t1\t\n")
//                            .append("4\tSense and Sensibility\tPeriod Drama\tElinor\t3\t4\t\n")
//                            .toString()));

            add(new CmdToResult("drop table actors;", Config.OK()));
            add(new CmdToResult("select * from actors;", "[ERROR]: Table does not exist"));
            add(new CmdToResult("drop database imdb;", Config.OK()));
            add(new CmdToResult("use imdb;", "[ERROR]: Unknown database"));
        }};

        for(CmdToResult test : cmdToExpectedResult) {
            System.out.println("sql: " + test.getCmd());

            try {
                DBController controller = new DBController(test.getCmd(), ctx);
                controller.executeQuery();
            } catch (Exception e) {
                String exceptionMsg = e.toString();
                System.out.println("result: " + exceptionMsg);
                Assertions.assertEquals(exceptionMsg, test.getExpectedResult());
                continue;
            }

            System.out.println("result: " + test.getExpectedResult());
            // Utils.listDbRootDir();
            Assertions.assertEquals(ctx.getResult(), test.getExpectedResult());
        }
    }

    @Test
    public void testRobustTranscript() throws Exception {
        DBEngine.setDBRootDir(testRootDir);
        final List<CmdToResult> cmdToExpectedResult = new LinkedList<>() {{
            // Create database and tables
            add(new CmdToResult("CREATE DATABASE imdb;", Config.OK()));
            add(new CmdToResult("USE imdb;", Config.OK()));
            add(new CmdToResult("CREATE TABLE actors (name, nationality, awards);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('Hugh Grant', 'British', 3);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('Toni Collette', 'Australian', 12);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('James Caan', 'American', 8);", Config.OK()));
            add(new CmdToResult("INSERT INTO actors VALUES ('Emma Thompson', 'British', 10);", Config.OK()));
            add(new CmdToResult("CREATE TABLE movies (name, genre);", Config.OK()));
            add(new CmdToResult("INSERT INTO movies VALUES ('Mickey Blue Eyes', 'Comedy');", Config.OK()));
            add(new CmdToResult("INSERT INTO movies VALUES ('About a Boy', 'Comedy');", Config.OK()));
            add(new CmdToResult("INSERT INTO movies VALUES ('Sense and Sensibility', 'Period Drama');", Config.OK()));
            add(new CmdToResult("create table roles (name, movie_id, actor_id);", Config.OK()));
            add(new CmdToResult("insert into roles values ('Edward', 3, 1);", Config.OK()));
            add(new CmdToResult("insert into roles values ('Frank', 1, 3);", Config.OK()));
            add(new CmdToResult("insert into roles values ('Fiona', 2, 2);", Config.OK()));
            add(new CmdToResult("insert into roles values('Elinor', 3, 4);", Config.OK()));

            add(new CmdToResult("SELECT * FROM actors", "[ERROR]: Semi colon missing at end of line"));
            add(new CmdToResult("SELECT * FROM crew;", "[ERROR]: Table does not exist"));
            add(new CmdToResult("SELECT spouse FROM actors;", "[ERROR]: Attribute does not exist"));
            add(new CmdToResult("SELECT * FROM actors);", "[ERROR]: Invalid query"));
            add(new CmdToResult("SELECT * FROM actors WHERE name == 'Hugh Grant;", "[ERROR]: Invalid query"));
            add(new CmdToResult("SELECT * FROM actors WHERE name > 10;", "[ERROR]: Attribute cannot be converted to number"));
            add(new CmdToResult("SELECT name age FROM actors;", "[ERROR]: Invalid query"));
            add(new CmdToResult("SELECT * FROM actors awards > 10;", "[ERROR]: Invalid query"));
            add(new CmdToResult("SELECT * FROM actors WHERE name LIKE 10;", "[ERROR]: String expected"));
            // add(new CmdToResult("      SELECT * FROM actors WHERE awards > 10;", Config.OK()));
            add(new CmdToResult("USE ebay;", "[ERROR]: Unknown database"));
        }};

        for(CmdToResult test : cmdToExpectedResult) {
            System.out.println("sql: " + test.getCmd());

            try {
                DBController controller = new DBController(test.getCmd(), ctx);
                controller.executeQuery();
            } catch (Exception e) {
                String exceptionMsg = e.toString();
               System.out.println("result: " + exceptionMsg);
               Assertions.assertEquals(exceptionMsg, test.getExpectedResult());
               continue;
            }

            System.out.println("result: " + test.getExpectedResult());
            // Utils.listDbRootDir();
            Assertions.assertEquals(ctx.getResult(), test.getExpectedResult());
        }
    }
}