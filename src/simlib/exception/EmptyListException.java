package simlib.exception;

public class EmptyListException extends RuntimeException {
	public EmptyListException(String name) {
		super(name + " is empty");
	}
}

