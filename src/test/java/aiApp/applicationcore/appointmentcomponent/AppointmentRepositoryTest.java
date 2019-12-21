package aiApp.applicationcore.appointmentcomponent;

import aiApp.applicationcore.Application;
import aiApp.applicationcore.employeecomponent.EmailType;
import aiApp.applicationcore.employeecomponent.Employee;
import aiApp.applicationcore.employeecomponent.EmployeeRepository;
import aiApp.applicationcore.employeecomponent.InvalidEmployeeNameException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AppointmentRepositoryTest {
    @Before
    public void setUp() {
        try {
            employeeRepository.deleteAll();
            appointmentRepository.deleteAll();

            final int yearBegin = 2015;
            final int monthBegin = TimePoint.JUNE;
            final int dayBegin = 5;
            final int hourBegin = 0;
            final int minuteBegin = 0;
            final int secondBegin = 0;

            TimePoint beginTimePoint = new TimePoint(yearBegin, monthBegin, dayBegin, hourBegin, minuteBegin, secondBegin);

            final int yearEnd = 2015;
            final int monthEnd = TimePoint.JUNE;
            final int dayEnd = 5;
            final int hourEnd = 10;
            final int minuteEnd = 0;
            final int secondEnd = 0;

            TimePoint endTimePoint = new TimePoint(yearEnd, monthEnd, dayEnd, hourEnd, minuteEnd, secondEnd);

            TimeSpan timeSpan = new TimeSpan(beginTimePoint, endTimePoint);

            appointment = new Appointment(timeSpan);

            Employee employee1 = new Employee("Peter", "Tester", new EmailType("peter.tester@test.com"));

            Employee employee2 = new Employee("dummyFirstName", "dummyLastName", null);

            List<Employee> employees = Arrays.asList(employee1, employee2);

            employeeRepository.save(employees);

            appointment.addEmployees(employees);

            appointmentRepository.save(appointment);
        } catch (InvalidDateException | InvalidTimeSpanException
                 | InvalidEmployeeNameException | InvalidWeekException | InvalidTimePointException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAll() {
        List<Appointment> appointments = appointmentRepository.findAll();

        assertThat(appointments).hasSize(1);

        assertThat(appointments).containsExactly(appointment);

    }

    @Test
    public void testFindByStartWeek() {
        Optional<List<Appointment>> optional = appointmentRepository.findByStartWeek(appointment.getStartWeek());

        assertTrue(optional.isPresent());

        List<Appointment> appointments = optional.get();

        assertThat(appointments).hasSize(1);

        assertThat(appointments).containsExactly(appointment);

        Appointment appointmentGotten = appointments.get(0);

        assertEquals(appointment, appointmentGotten);

        final int week = 23;

        optional = appointmentRepository.findByStartWeek(week);

        assertTrue(optional.isPresent());

        assertThat(optional.get()).containsExactly(appointment);
    }

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Appointment appointment;
}
