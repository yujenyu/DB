package core.exception;
public class TableExistsException extends Exception{
    String msg;

    public TableExistsException(String msg) {
        this.msg = msg;
    }
    public String toString() { return msg; }
}
