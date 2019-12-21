package aiApp.applicationcore.employeecomponent;

import java.util.List;

/**
 * Interface of the EmployeeComponent.
 */
public interface EmployeeComponentInterface {
    /**
     * Function to hire a new employee.
     *
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @return The employee just hired.
     * @throws FailedToHireEmployeeException if the employee to be hired has no name.
     */
    Employee hireNewEmployee(String firstName, String lastName) throws FailedToHireEmployeeException;

    /**
     * Function to hire a new employee by name.
     *
     * @param fullName The full name of the employee.
     * @return The employee just hired.
     * @throws FailedToHireEmployeeException if the employee had an invalid name.
     */
    Employee hireNewEmployee(String fullName) throws FailedToHireEmployeeException;

    /**
     * Function to fire an employee.
     *
     * @param employeeId The ID of the employee to be fired.
     * @throws FailedToFireEmployeeException if there is no employee corresponding to the ID passed in.
     */
    void fireEmployee(Integer employeeId) throws FailedToFireEmployeeException;

    /**
     * Function to get all employees
     *
     * @return A list of all employees
     */
    List<Employee> getAllEmployees();

    /**
     * Function to get all employees identified by a list of employee IDs.
     *
     * @param employeeIds The IDs of the employees to get.
     * @return A list of the employees corresponding to the IDs passed in. May be empty.
     */
    List<Employee> getEmployeesById(List<Integer> employeeIds);

    /**
     * Function to check whether or not an employee exists.
     *
     * @param employeeId The ID of the employee to check.
     * @return true if an employee corresponding to the ID passed in exists;
     *         false otherwise.
     */
    boolean doesEmployeeExist(Integer employeeId);

    /**
     * Function to check whether or not all employees identified by their IDs passed in exist.
     *
     * @param employeeIds A list of employee IDs to check for existence.
     * @return true there is an employee for every employee ID passed in; false otherwise.
     *         If null is passed in or an empty list is passed in true is returned.
     */
    boolean doAllEmployeesExist(List<Integer> employeeIds);
}
