package aiApp.applicationcore.employeecomponent;

import aiApp.applicationcore.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class EmployeeComponentTest {
    @Before
    public void setUp() {
        try {
            employeeRepository.deleteAll();
            employeeComponentInterface = new EmployeeComponent(employeeRepository);

            employee1 = new Employee("Peter", "Schmidt", new EmailType("peter.schmidt@mail.de"));
            employee2 = new Employee("Test", "LastName", new EmailType("test@testing.org"));
            employee3 = new Employee("A", "B", null);

            employees = Arrays.asList(employee1, employee2, employee3);

            employeeIds = new ArrayList<>();

            employeeRepository.save(employees);

            // must come after save as save modifies the objects
            for (Employee e : employees) {
                employeeIds.add(e.getId());
            }
        } catch (InvalidEmployeeNameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHireEmployee() {
        try {
            Employee e = employeeComponentInterface.hireNewEmployee("FirstName", "LastName");

            assertEquals(null, e.getEmail());

            assertEquals("FirstName", e.getFirstName());

            assertEquals("LastName", e.getLastName());

            assertEquals("FirstName LastName", e.getName());

            Employee lastEmployee = employees.get(employees.size() - 1);

            assertEquals((long)lastEmployee.getId() + 1, (long)e.getId());

            assertThat(employeeRepository.findAll()).contains(e);

            Optional<List<Employee>> opt = employeeRepository.findByName("FirstName LastName");
            assertTrue(opt.isPresent());

            List<Employee> list = opt.get();

            assertThat(list).hasSize(1);

            assertThat(list.get(0)).isSameAs(e);
        } catch (FailedToHireEmployeeException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testHireInvalidNameEmployee() {
        final String invalidNames[] = new String[] {
                null, "", "\"", "'", "7 of 9", "$", "?", "peterschmidt",
                "hucrl.iuc+r.h}", " ", "  ", "                       ", "~"
        };

        for (String str : invalidNames) {
            assertThatThrownBy(() -> employeeComponentInterface.hireNewEmployee(str))
                    .isInstanceOf(FailedToHireEmployeeException.class);
        }
    }

    @Test
    public void testFireEmployees() {
        try {
            for (Integer employeeId : employeeIds) {
                employeeComponentInterface.fireEmployee(employeeId);
            }

            assertThat(employeeRepository.findAll()).isEmpty();

            for (Integer employeeId : employeeIds) {
                assertFalse(employeeComponentInterface.doesEmployeeExist(employeeId));

                assertThatThrownBy(() -> employeeComponentInterface.fireEmployee(employeeId))
                        .isInstanceOf(FailedToFireEmployeeException.class);
            }
        } catch (FailedToFireEmployeeException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testFireNonExistentEmployees() {
        final Integer invalidIds[] = new Integer[] {
                null, 0, -1, Integer.MIN_VALUE, Integer.MAX_VALUE,
                employee3.getId() + 1,
                0x7FFFFFFF, 0xFFFFFFFF
        };

        for (Integer invalidId : invalidIds) {
            assertThatThrownBy(() -> employeeComponentInterface.fireEmployee(invalidId))
                    .isInstanceOf(FailedToFireEmployeeException.class);
        }
    }

    @Test
    public void testGetAllEmployees() {
        try {
            List<Employee> employeeList = employeeComponentInterface.getAllEmployees();

            assertEquals(employees, employeeList);

            for (Employee employee : employeeList) {
                employeeComponentInterface.fireEmployee(employee.getId());
            }

            assertThat(employeeComponentInterface.getAllEmployees()).isEmpty();
            assertThat(employeeRepository.findAll()).isEmpty();
        } catch (FailedToFireEmployeeException e) {
            assertEquals("Exception", e.getMessage());
        }
    }

    @Test
    public void testGetEmployeesById() {
        List<Employee> employeeList = employeeComponentInterface.getEmployeesById(employeeIds);

        assertEquals(employees, employeeList);

        assertThat(employeeComponentInterface.getEmployeesById(null)).isEmpty();
        assertThat(employeeComponentInterface.getEmployeesById(new ArrayList<>())).isEmpty();

        List<Integer> argument = new ArrayList<>();
        argument.add(0);

        assertThat(employeeComponentInterface.getEmployeesById(argument)).isEmpty();
    }

    @Test
    public void testDoEmployeesExist() {
        final Integer invalidIds[] = {
                0, -1, null, 999, 9999, Integer.MAX_VALUE, Integer.MIN_VALUE, 0xFFFFFFFF
        };

        assertTrue(employeeComponentInterface.doAllEmployeesExist(employeeIds));

        try {
            for (Integer employeeId : employeeIds) {
                employeeComponentInterface.fireEmployee(employeeId);
            }
        } catch (FailedToFireEmployeeException e) {
            assertEquals("Exception:", e.getMessage());
        }

        for (Integer employeeId : employeeIds) {
            assertFalse(employeeComponentInterface.doesEmployeeExist(employeeId));
        }

        for (Integer invalidId : invalidIds) {
            assertFalse(employeeComponentInterface.doesEmployeeExist(invalidId));
        }
    }

    private EmployeeComponentInterface employeeComponentInterface;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;
    private List<Employee> employees;
    private List<Integer> employeeIds;
}
