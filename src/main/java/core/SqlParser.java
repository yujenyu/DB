package core;

import core.exception.InvalidSqlException;
import sqlCmd.Config;
import sqlCmd.type.SqlCmdType;
import sqlCmd.type.SqlKeyWord;
import sqlCmd.type.ValType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlParser {
    private static final String validAttributeListOfCreateTablePat = "\\(\\s*[a-zA-Z_]+(\\s*,\\s*[a-zA-Z_]+\\s*)*\\)\\s*;$";
    private static final String validAttributeListOfSelectTablePat = "\\s*[a-zA-Z_]+(\\s*,\\s*[a-zA-Z_]+\\s*)*\\s*$";
    private static final String validInsertPat = "^(INSERT|insert)\\s+(INTO|into)\\s+[a-zA-Z_]*\\s+(VALUES|values)\\s+\\(.*\\)\\s*;";
    private static final String validAlterPat = "^(ALTER|alter)\\s+(TABLE|table)\\s+[a-zA-Z_]*\\s+(ADD|add|DROP|drop)\\s+[a-zA-Z_]+\\s*;$";
    private static final String validJoinPat = "^(JOIN|join)\\s+[a-zA-Z_]*\\s+(AND|and)\\s+[a-zA-Z_]*\\s+(ON|on)\\s+[a-zA-Z_]*\\s+(AND|and)\\s+[a-zA-Z_]+\\s*;$";
    private static final String validPairEndSemiColon = "^\\(.*\\)\\s+;$";
    private static final String validQuotedToken = "^'([a-zA-Z_]+)'$";
    private static final String validToken = "^[a-zA-Z_]*$";
    private static final String validNumberToken = "^([0-9]+)$";

    public SqlParser() {}

    public static List<String> tokenize(String input) throws InvalidSqlException {
        // basic validation
        checkEndQuery(input);

        List<String> parts = Arrays.asList(input.split("(?=[ ,;()'])|(?<=[ ,;()'])"));

        // Remove empty, blank and trim space
        List<String> tokens = parts.stream()
                .filter(t -> !t.isEmpty() && !t.isBlank())
                .map(String::trim)
                .collect(Collectors.toList());

        // select, id, from, movies, where, name, ==, ', jordan, huang, '
        Stack<String> toCombine = new Stack<>();
        boolean quoted = false;

        for(int i = 0; i<tokens.size(); i++) {
            if(! "'".equals(tokens.get(i))) {
                toCombine.add(tokens.get(i));
            }
            else {
                StringBuilder sb = new StringBuilder();

                while(i < tokens.size()-2 && !"'".equals(tokens.get(++i))) {
                    sb.append(tokens.get(i)).append(" ");
                }

                System.out.println("i :" + i + "tokens at i : " + tokens.get(i));
                if(!"'".equals(tokens.get(i))) {
                    throw new InvalidSqlException("[ERROR]: Invalid query");
                }
                toCombine.add(sb.toString().replaceFirst("\\s++$", ""));
            }
        }
        List<String> res = new ArrayList<>(toCombine);
        return res;
    }

    public static void checkName(String input) throws InvalidSqlException {
        // regex checks for letter name
        String attributeName = ("^[a-zA-Z_]*$");
        if(!input.matches(attributeName)){
            throw new InvalidSqlException("[ERROR]: Invalid Attribute Name");
        }
    }

    public static void checkNameShowInvalidQuery(String input) throws InvalidSqlException {
        // regex checks for letter name
        String attributeName = ("^[a-zA-Z_]*$");
        if(!input.matches(attributeName)){
            throw new InvalidSqlException("[ERROR]: Invalid query");
        }
    }

    public static void checkEndQuery(String input) throws InvalidSqlException {
        if(!input.endsWith(";")){
            throw new InvalidSqlException("[ERROR]: Semi colon missing at end of line");
        }
    }

    public static boolean isValidInsertIntoStatement(List<String> sqlTokens) {
        final String sqlStatement = toString(sqlTokens);
        // System.out.println(sqlStatement);
        return Pattern.matches(validInsertPat, sqlStatement);
    }

    public static boolean isValidAlterTableStatement(List<String> sqlTokens) {
        final String sqlStatement = toString(sqlTokens.stream().map(t -> t.toUpperCase()).collect(Collectors.toList()));
        // System.out.println(sqlStatement);
        return Pattern.matches(validAlterPat, sqlStatement);
    }

    public static boolean isValidJoinTableStatement(List<String> sqlTokens) {
        final String sqlStatement = toString(sqlTokens.stream().map(t -> t.toUpperCase()).collect(Collectors.toList()));
        // System.out.println(sqlStatement);
        return Pattern.matches(validJoinPat, sqlStatement);
    }

    private static String toString(List<String> sqlTokens) {
        return String.join(" ", sqlTokens);
    }

    public static Map<SqlKeyWord, Integer>  getKeywordIdx  (final List<String> tokens)  {
        final SqlKeyWord [] keywords = { SqlKeyWord.FROM, SqlKeyWord.WHERE, SqlKeyWord.SET};
        final List<String> tokensInLowercase = tokens.stream().map(String::toLowerCase).collect(Collectors.toList());

        Map<SqlKeyWord, Integer> keywordToIdx = new HashMap<>();

        for(SqlKeyWord keyword : keywords) {
            keywordToIdx.put(keyword, tokensInLowercase.indexOf(keyword.pattern()));
        }

        return keywordToIdx;
    }

    public static Condition parseCondition(List<String> conditionTokens) throws InvalidSqlException {
        return new Condition(conditionTokens.get(0),
                   conditionTokens.get(1),
                   conditionTokens.get(2));
    }

    public static Condition toCondition(DBContext ctx, List<String> tokens) throws InvalidSqlException {
        return parseCondition(tokens);
    }

    public static List<NameValue> toNameValueList(DBContext ctx, List<String> tokens) throws InvalidSqlException {
        if(!(tokens.size()%3==0)) {
            throw new InvalidSqlException(Config.ERROR() + " : Invalid SET statement " + String.join(" ", tokens));
        }

        List<NameValue> nameValues = new ArrayList<>();

        // SET name = jordan email = jordan@gmail.com
        for(int i=0; i<tokens.size(); i+=3) {
           nameValues.add(new NameValue(tokens.get(i), tokens.get(i+2)));
        }
        return nameValues;
    }

    public static List<String> toAttributeList(DBContext ctx, List<String> attributeListTokens) throws InvalidSqlException {
        // Check if Attribute does exist or not

        if (!isValidAttributeList(ctx.getCmdType(), attributeListTokens)) {
            throw new InvalidSqlException("[ERROR]: Invalid query");
        }

        List<String> mayQuotedTokens = attributeListTokens.stream()
                .filter(t -> !"(".equals(t) &&
                             !",".equals(t) &&
                             !")".equals(t) &&
                             !";".equals(t))
                .collect(Collectors.toList());

        return mayQuotedTokens;
    }

    public static String unquoted(String quotedString) {
        return quotedString.replaceAll("'", "");
    }

    public static List<String> unquoted(List<String> tokens ) {
        String tokenString = toString(tokens);

        String regex = "\\'([^\\']*)\\'|(\\S+)";

        List<String> res = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(tokenString);
        while (m.find()) {
            if (m.group(1) != null) {
                res.add(m.group(1).trim());
            } else {
                res.add(m.group(2).trim());
            }
        }
        return res;
    }

    private static boolean isValidOptionalQuoteToken(String token) {
        return Pattern.matches(validQuotedToken, token) ||
               Pattern.matches(validToken, token) ||
               Pattern.matches(validNumberToken, token);
    }

    public static ValType getValType(String val) {
        if (Pattern.matches(ValType.BOOL.pattern(), val)) {
            return ValType.BOOL;
        } else if (Pattern.matches(ValType.FLOAT.pattern(), val)) {
            return ValType.FLOAT;
        } else if (Pattern.matches(ValType.INT.pattern(), val)) {
            return ValType.INT;
        } else {
            return ValType.STRING;
        }
    }

    // create table xxx (attr,attr,attr);
    // select xxx, xxx, xxx from (without bracket)
    private static boolean isValidAttributeList(SqlCmdType cmdType, List<String> attributeListTokens) throws InvalidSqlException {
        final String attributeListTokensInString = toString(attributeListTokens);

        switch (cmdType) {
            case CREATE:
                return Pattern.matches(validAttributeListOfCreateTablePat, attributeListTokensInString);
            case SELECT:
                return Pattern.matches(validAttributeListOfSelectTablePat, attributeListTokensInString);
            case INSERT:
                // System.out.println(cmdType.toString());
                // System.out.println(attributeListTokens.toString());
                // System.out.println(attributeListTokensInString);
                // (.....);
                if (!Pattern.matches(validPairEndSemiColon, attributeListTokensInString)) {
                    // System.out.println("in1");
                    return false;
                }

                for(String token : attributeListTokensInString
                        .replaceAll("\\(", "")
                        .replaceAll("\\)", "")
                        .replaceAll("\\s", "")
                        .replaceAll(";", "")
                        .split(",")) {

                    // 'attr' or attr
                    if(!isValidOptionalQuoteToken(token)) {
                        // System.out.println("in2");
                        // System.out.println(token);
                        return false;
                    }
                }

                return true;
            default:
                throw new InvalidSqlException("[ERROR]: Invalid command type");
        }
    }
}
