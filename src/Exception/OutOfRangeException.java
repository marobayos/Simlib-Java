package Exception;

public class OutOfRangeException extends RuntimeException {
	public OutOfRangeException(String name, int size, int index) {
		super("The " + name + " has not "+index+" beause has only "+size);
	}
}
