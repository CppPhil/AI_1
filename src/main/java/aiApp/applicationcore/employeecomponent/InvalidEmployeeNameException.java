package aiApp.applicationcore.employeecomponent;

/**
 * Thrown in Employee::Employee(String, String, EmailType)
 */
public class InvalidEmployeeNameException extends Throwable {
    /**
     * Creates a new InvalidEmployeeNameException.
     * The message used is: "Name of employee was invalid.".
     */
    InvalidEmployeeNameException() {
        super("Name of employee was invalid.");
    }
}
