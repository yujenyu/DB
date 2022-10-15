package core.exception;

public class ParseException extends Exception{
    String msg;

    public ParseException(String msg) {
        this.msg = msg;
    }
    public String toString() { return msg; }
}
