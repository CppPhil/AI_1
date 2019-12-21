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

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class EmployeeRepositoryTest {
    @Before
    public void setUp() {
        try {
            employeeRepository.deleteAll();
            employee = new Employee("Peter", "Schmidt", new EmailType("peter.schmidt@testing.org"));

            employeeRepository.save(employee);
        } catch (InvalidEmployeeNameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAll() {
        try {
            assertThat(employeeRepository.findAll()).containsExactly(employee);

            Employee anotherEmployee = new Employee("blah", "blub", null);

            anotherEmployee = employeeRepository.save(anotherEmployee);

            assertThat(employeeRepository.findAll()).containsExactly(employee, anotherEmployee);

            employeeRepository.deleteAll();

            assertThat(employeeRepository.findAll()).isEmpty();
        } catch (InvalidEmployeeNameException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testFindByName() {
        final String strToSearchFor = "Peter Schmidt";

        try {
            Optional<List<Employee>> opt = employeeRepository.findByName(strToSearchFor);

            assertTrue(opt.isPresent());

            List<Employee> list = opt.get();

            assertThat(list).hasSize(1);

            Employee e = list.get(0);

            assertEquals(employee, e);

            Employee sameName = new Employee("Peter", "Schmidt", null);
            sameName = employeeRepository.save(sameName);

            opt = employeeRepository.findByName(strToSearchFor);

            assertTrue(opt.isPresent());

            assertThat(opt.get()).containsExactly(employee, sameName);
        } catch (InvalidEmployeeNameException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;
}
