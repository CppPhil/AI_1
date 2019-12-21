package aiApp.applicationcore.appointmentcomponent;

/**
 * Because InvalidArgumentException doesn't work with Gradle.
 */
public class ArgumentNotValidException extends Throwable {
    /**
     * Creates a ArgumentNotValidException
     *
     * @param errorMsg The error message. May not be null!
     */
    @SuppressWarnings("unused")
    public ArgumentNotValidException(String errorMsg) {
        super(errorMsg);
    }

    /**
     * Creates a new ArgumentNotValidException object.
     *
     * @param errorMsg An array that holds the error message.
     *        May not be null!
     *        Must be of size 1!
     *        The String in the array may not be null!
     */
    public ArgumentNotValidException(String errorMsg[]) {
        super(errorMsg[0]);
    }
}
