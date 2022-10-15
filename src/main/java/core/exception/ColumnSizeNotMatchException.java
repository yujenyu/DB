package core.exception;

public class ColumnSizeNotMatchException extends Exception{
    String msg;

    public ColumnSizeNotMatchException(String msg) {
        this.msg = msg;
    }
    public String toString() { return msg; }
}
