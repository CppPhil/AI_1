package aiApp.applicationcore.employeecomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown in EmployeeComponent::hireNewEmployee
 */
public class FailedToHireEmployeeException extends Throwable {
    /**
     * Creates a new FailedToHireEmployeeException.
     *
     * @param employeeName The name, consisting of the first name followed by a single space character and the last name,
     *        of the employee, that could not be hired. May not be null.
     */
    FailedToHireEmployeeException(@NotNull String employeeName) {
        super("Employee " + employeeName + " could not be hired.");
    }
}
