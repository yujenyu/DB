package core.exception;

public class InvalidSqlException extends Exception{
    String msg;

    public InvalidSqlException(String msg) {
        this.msg = msg;
    }
    public String toString() { return msg; }
}
