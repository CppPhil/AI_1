package aiApp.applicationcore.employeecomponent;

import aiApp.applicationcore.Application;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class EmployeeTest {
    @Before
    public void setUp() {
        try {
            employeeRepository.deleteAll();
            employee = new Employee("Peter", "Schmidt", new EmailType("peter.schmidt@testers.com"));
            employee = employeeRepository.save(employee);
        } catch (InvalidEmployeeNameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateEmployee() {
        try {
            Employee anEmployee = new Employee("Test", "LastName", null);
            anEmployee = employeeRepository.save(anEmployee);

            assertEquals("Test", anEmployee.getFirstName());

            assertEquals("LastName", anEmployee.getLastName());

            assertEquals("Test LastName", anEmployee.getName());

            assertEquals(null, anEmployee.getEmail());

            assertEquals(employee.getId() + 1, anEmployee.getId().intValue());
        } catch (InvalidEmployeeNameException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testCreateEmployeeFailure() {
        final String invalidNames[] = new String[] {
                null, "", " ", "%", "$&", "~", "er{hu]+rh}rcuh{}r",
                "7 of 9", "                        ", "\u1234", "???",
                "NAMEHER3", "onthucr}{hur}{hircl.hpircp", "nth.,ruc{}h"
        };

        for (String invalidName : invalidNames) {
            assertThatThrownBy(() -> new Employee(invalidName, null, null))
                    .isInstanceOf(InvalidEmployeeNameException.class);
            assertThatThrownBy(() -> new Employee(null, invalidName, null))
                    .isInstanceOf(InvalidEmployeeNameException.class);
        }
    }

    @Test
    public void testGetters() {
        assertEquals("Peter", employee.getFirstName());
        assertEquals("Schmidt", employee.getLastName());
        assertEquals("Peter Schmidt", employee.getName());
        assertEquals(new EmailType("peter.schmidt@testers.com"), employee.getEmail());
    }

    @Test
    public void testSetEmail() {
        EmailType email = new EmailType("test@test.org");
        employee.setEmail(email);

        assertEquals(email, employee.getEmail());

        employee.setEmail(null);

        assertEquals(null, employee.getEmail());
    }

    @Test
    public void testEquals() {
        try {
            Employee testEmployee = new Employee("Peter", "Schmidt", new EmailType("peter.schmidt@testers.com"));
            testEmployee = employeeRepository.save(testEmployee);

            assertThat(testEmployee).isNotEqualTo(employee);

            assertThat(employee).isNotEqualTo(testEmployee);

            assertThat(employee).isNotEqualTo(null);

            assertThat(employee).isNotEqualTo(new JSONObject("{\"cookies\":5}"));
        } catch (InvalidEmployeeNameException | JSONException e) {
            assertEquals("Exception", e.getMessage());
        }
    }

    private Employee employee;

    @Autowired
    private EmployeeRepository employeeRepository;
}
