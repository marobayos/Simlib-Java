package simlib.exception;

public class PasstEventException  extends RuntimeException {
    public PasstEventException() {
        super("Events can not have less time than the current time.");
    }
}
