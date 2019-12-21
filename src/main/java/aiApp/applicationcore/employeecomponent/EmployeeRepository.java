package aiApp.applicationcore.employeecomponent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * A repository for employees.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    /**
     * Finds employees by their full name.
     *
     * @param name The first and last name of the employee as one String.
     *        The first name must come first and must be separated from the last name with a single space character.
     * @return An Optional List of the Employees found.
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    Optional<List<Employee>> findByName(String name);
}
