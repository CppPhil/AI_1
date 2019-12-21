package aiApp.applicationcore.employeecomponent;

/**
 * Thrown in EmployeeComponent::fireEmployee(Integer)
 * Thrown if attempting to fire an employee who does not exist.
 */
public class FailedToFireEmployeeException extends Throwable {
    /**
     * Creates a new FailedToFireEmployeeException.
     * The message used is: "Employee did not exist!".
     *
     * @apiNote This is supposed to be used when attempting to delete an employee who does not exist.
     */
    FailedToFireEmployeeException() {
        super("Employee did not exist!");
    }
}
