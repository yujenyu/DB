package core.exception;

public class TableNotExistsException extends Exception{
    String msg;

    public TableNotExistsException(String msg) {
        this.msg = msg;
    }
    public String toString() { return msg; }
}
