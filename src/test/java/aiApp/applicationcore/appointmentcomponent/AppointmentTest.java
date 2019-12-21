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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AppointmentTest {
    @Before
    public void setUp() {
        try {
            employeeRepository.deleteAll();
            appointmentRepository.deleteAll();

            final int beginYear = 2016;
            final int beginMonth = TimePoint.AUGUST;
            final int beginDay = 2;
            final int beginHour = 14;
            final int beginMinute = 0;
            final int beginSecond = 0;

            beginTimePoint = new TimePoint(beginYear, beginMonth, beginDay, beginHour, beginMinute, beginSecond);

            final int endYear = 2016;
            final int endMonth = TimePoint.AUGUST;
            final int endDay = 2;
            final int endHour = 16;
            final int endMinute = 0;
            final int endSecond = 0;

            endTimePoint = new TimePoint(endYear, endMonth, endDay, endHour, endMinute, endSecond);

            timeSpan = new TimeSpan(beginTimePoint, endTimePoint);

            appointment = new Appointment(timeSpan);

            employee1 = new Employee("Peter", "Schmidt", new EmailType("peter.schmidt@internet.de"));
            employee2 = new Employee("ABC", "DEF", null);

            employee1 = employeeRepository.save(employee1);
            employee2 = employeeRepository.save(employee2);

            employees = Arrays.asList(employee1, employee2);

            appointment.addEmployees(employees);

            appointment = appointmentRepository.save(appointment);
        } catch (InvalidDateException | InvalidTimePointException | InvalidTimeSpanException
                 | InvalidWeekException | InvalidEmployeeNameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateAppointment() {
        try {
            @SuppressWarnings("unused") Appointment anAppointment = new Appointment(timeSpan);
            assertTrue(true);
        } catch (InvalidTimeSpanException | InvalidWeekException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testCreateAppointmentInvalidTimeSpan() {
        final int year = 2016;
        final int month = TimePoint.AUGUST;
        final int day = 2;
        final int hour = 14;
        final int minute = 0;
        final int second = 0;

        TimePoint tp1 = null;
        TimePoint tp2 = null;

        try {
            tp1 = new TimePoint(year, month, day, hour, minute, second);

            tp2 = new TimePoint(year, month, day - 1, hour, minute, second);
        } catch (InvalidDateException e) {
            assertEquals("Exception:", e.getMessage());
        }

        assertNotNull(tp1);
        assertNotNull(tp2);

        TimePoint finalTp = tp1;
        TimePoint finalTp1 = tp2;
        assertThatThrownBy(() -> new TimeSpan(finalTp, finalTp1))
                .isInstanceOf(InvalidTimePointException.class);

        assertThatThrownBy(() -> new Appointment(null))
                .isInstanceOf(InvalidTimeSpanException.class);
    }

    @Test
    public void testGetTimeSpan() {
        assertEquals(timeSpan, appointment.getTimeSpan());
    }

    @Test
    public void testGetStartWeek() {
        final int week = 32;

        assertEquals(week, appointment.getStartWeek().intValue());
    }

    @Test
    public void testGetAttendees() {
        assertEquals(employees, appointment.getAttendees());
    }

    @Test
    public void testAddAttendees() {
        try {
            Employee e = new Employee("test", "testLastName", null);
            e = employeeRepository.save(e);

            List<Employee> employeeList = new ArrayList<>();
            employeeList.add(e);
            appointment.addEmployees(employeeList);

            employeeList = appointment.getAttendees();

            assertThat(employeeList).containsExactly(employee1, employee2, e);

            final int siz = appointment.getAttendees().size();

            appointment.addEmployees(employeeList);

            assertEquals(siz, appointment.getAttendees().size());
        } catch (InvalidEmployeeNameException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testHasAttendee() {
        assertTrue(appointment.hasAttendee(employee1.getId()));
        assertTrue(appointment.hasAttendee(employee2.getId()));
        assertFalse(appointment.hasAttendee(null));

        try {
            Employee e = new Employee("A", "bc", null);
            e = employeeRepository.save(e);

            assertFalse(appointment.hasAttendee(e.getId()));

        } catch (InvalidEmployeeNameException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testEquals() {
        assertEquals(appointment, appointment);

        assertThat(appointment).isNotEqualTo(null);

        try {
            assertThat(appointment).isNotEqualTo(new Appointment(timeSpan));
        } catch (InvalidTimeSpanException | InvalidWeekException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Appointment appointment;
    private Employee employee1;
    private Employee employee2;
    private List<Employee> employees;
    private TimeSpan timeSpan;
    private TimePoint beginTimePoint;
    private TimePoint endTimePoint;
}
