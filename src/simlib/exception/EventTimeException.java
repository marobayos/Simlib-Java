package simlib.exception;

public class EventTimeException extends RuntimeException {
    public EventTimeException() {
        super("Events can't have a negative time.");
    }
}
