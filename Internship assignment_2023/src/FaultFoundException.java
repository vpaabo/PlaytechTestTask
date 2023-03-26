public class FaultFoundException extends Exception{
    /**
     * An exception that indicates a faulty turn, which then can be caught
     */
    public FaultFoundException(String message) {
        super(message);
    }
}
