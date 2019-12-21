package aiApp.applicationcore.employeecomponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * The EmployeeComponent.
 */
@Component
public class EmployeeComponent implements EmployeeComponentInterface {
    /**
     * Creates an EmployeeComponent form an EmployeeRepository.
     *
     * @param employeeRepository The EmployeeRepository to use.
     */
    @Autowired
    public EmployeeComponent(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a new Employee and adds the Employee created to the repository.
     *
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @return The Employee created.
     * @throws FailedToHireEmployeeException if The name of the employee is empty or his email address is invalid.
     */
    @Override
    public Employee hireNewEmployee(String firstName, String lastName) throws FailedToHireEmployeeException {
        final String fullName = firstName + " " + lastName;

        Employee employee;

        try {
            employee = new Employee(firstName, lastName, null);
        } catch (InvalidEmployeeNameException | IllegalArgumentException e) {
            throw new FailedToHireEmployeeException(fullName);
        }

        employeeRepository.save(employee);

        return employee;
    }

    /**
     * Creates a new Employee from just their full name and adds the Employee created to the repository.
     *
     * @param fullName The full name of the employee.
     * @return The Employee just created.
     * @throws FailedToHireEmployeeException if the first or last name is invalid.
     */
    @Override
    public Employee hireNewEmployee(String fullName) throws FailedToHireEmployeeException {
        final char space = ' ';
        final int notFound = -1;
        final int begin = 0;
        final int nextChar = 1;

        if (fullName == null) {
            throw new FailedToHireEmployeeException("null employees are not allowed.");
        }

        final int firstSpace = fullName.indexOf(space);

        if (firstSpace == notFound) {
            throw new FailedToHireEmployeeException("employee " + fullName + " features no space");
        }

        String firstName = fullName.substring(begin, firstSpace);
        String lastName = fullName.substring(firstSpace + nextChar);

        return hireNewEmployee(firstName, lastName);
    }

    /**
     * Deletes the employee identified by the employee ID passed in from the repository.
     *
     * @param employeeId The ID of the employee to be fired.
     * @return A reference to this object.
     * @throws FailedToFireEmployeeException If there is no employee with the ID passed in.
     */
    @Override
    public void fireEmployee(Integer employeeId) throws FailedToFireEmployeeException {
        if (!doesEmployeeExist(employeeId)) {
            throw new FailedToFireEmployeeException();
        }

        employeeRepository.delete(employeeId);
    }

    /**
     * Gives the caller a list of all Employees
     *
     * @return A list of all employees. May be an empty list if there are no employees.
     */
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Gives the caller a list of the employees corresponding to the IDs passed in.
     *
     * @param employeeIds The IDs of the employees to get.
     * @return A list of the employees that correspond to the IDs passed in.
     *         Will be empty if no corresponding employees are found or if
     *         the argument passed into the parameter was null or if the argument
     *         passed into the parameter was an empty list.
     */
    @Override
    public List<Employee> getEmployeesById(List<Integer> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return new ArrayList<>();
        }

        return employeeRepository.findAll(employeeIds);
    }

    /**
     * Determines whether an employee exists by looking for an employee with the ID passed in.
     *
     * @param employeeId The ID of the employee to check.
     * @return true if the employee identified by employeeId exist; false otherwise.
     *         If null is passed in or the employeeId passed in is negative (which is invalid) false is returned.
     */
    @Override
    public boolean doesEmployeeExist(Integer employeeId) {
        return !(employeeId == null || employeeId <= 0) && employeeRepository.exists(employeeId);
    }

    /**
     * Determines if the IDs passed in correspond to employees.
     *
     * @param employeeIds A list of employee IDs to check for existence.
     * @return true if every single employee ID in the parameter has an associated employee; false otherwise.
     *         If null or an empty list is passed in true is returned.
     */
    @Override
    public boolean doAllEmployeesExist(List<Integer> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return true;
        }

        for (Integer employeeId : employeeIds) {
            if (!doesEmployeeExist(employeeId)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Autowiring by Constructor-Injection
     */
    private EmployeeRepository employeeRepository;
}
