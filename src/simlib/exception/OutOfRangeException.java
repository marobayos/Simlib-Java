package simlib.exception;

public class OutOfRangeException extends RuntimeException {
	public OutOfRangeException(String name, int size, int index) {
		super(name + " can't have attribute "+index+" because it has only "+size+" positions.");
	}
}
