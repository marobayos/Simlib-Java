package simlib.exception;

public class HasAlreadyElements extends RuntimeException {
    public HasAlreadyElements(String name){
        super("The resource "+name+" has already one element.");
    }
}
