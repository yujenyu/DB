package core.exception;

public class DataBaseExistsException extends Exception{
    String msg;

    public DataBaseExistsException(String msg) {
        this.msg = msg;
    }
    public String toString() { return msg; }
}
