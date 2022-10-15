package core.exception;

public class DataBaseNotExistsException extends Exception{
    String msg;

    public DataBaseNotExistsException(String msg) {
        this.msg = msg;
    }
    public String toString() { return msg; }
}
