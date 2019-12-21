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

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AppointmentComponentTest {
    @Before
    public void setUp() {
        try {
            employeeRepository.deleteAll();
            appointmentRepository.deleteAll();

            appointmentComponentInterface = new AppointmentComponent(appointmentRepository);

            final int yearBegin = 2017;
            final int monthBegin = TimePoint.JANUARY;
            final int dayBegin = 1;
            final int hourBegin = 0;
            final int minuteBegin = 0;
            final int secondBegin = 0;

            TimePoint beginTimePoint = new TimePoint(yearBegin, monthBegin, dayBegin, hourBegin, minuteBegin, secondBegin);

            final int yearEnd = 2017;
            final int monthEnd = TimePoint.FEBRUARY;
            final int dayEnd = 28;
            final int hourEnd = 13;
            final int minuteEnd = 30;
            final int secondEnd = 59;

            TimePoint endTimePoint = new TimePoint(yearEnd, monthEnd, dayEnd, hourEnd, minuteEnd, secondEnd);

            timeSpan = new TimeSpan(beginTimePoint, endTimePoint);

            appointment = new Appointment(timeSpan);

            employee1 = new Employee("Peter", "Tester", new EmailType("peter.tester@test.com"));

            employee2 = new Employee("dummyFirstName", "dummyLastName", null);

            List<Employee> employees = Arrays.asList(employee1, employee2);

            employeeRepository.save(employees);

            appointment.addEmployees(employees);

            appointmentRepository.save(appointment);
        } catch (InvalidDateException | InvalidTimePointException | InvalidTimeSpanException
                 | InvalidWeekException | InvalidEmployeeNameException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllAppointments() {
        try {
            final int expectedAmountOfAppointments = 1;
            final int appointmentIdx = expectedAmountOfAppointments - 1;

            List<Appointment> appointments = appointmentComponentInterface.getAppointmentsOfWeek(null);

            assertThat(appointments).hasSize(expectedAmountOfAppointments);

            assertEquals(appointment, appointments.get(appointmentIdx));

            assertEquals(appointmentRepository.findAll(), appointments);
        } catch (AppointmentNotFoundException | InvalidWeekException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testGetAllAppointmentsOfWeek() {
        try {
            final List<Appointment> emptyList = new ArrayList<>();
            final int expectedAmountOfAppointments = 1;
            final int appointmentIdx = expectedAmountOfAppointments - 1;
            final Integer weekOfAppointment = appointment.getStartWeek();
            final int maxWeek = 52;

            List<Appointment> appointments = appointmentComponentInterface.getAppointmentsOfWeek(weekOfAppointment);

            assertThat(appointments).hasSize(expectedAmountOfAppointments);

            assertEquals(appointment, appointments.get(appointmentIdx));

            Optional<List<Appointment>> appointmentsFromRepoOptional = appointmentRepository.findByStartWeek(weekOfAppointment);

            assertTrue(appointmentsFromRepoOptional.isPresent());

            List<Appointment> appointmentsFromRepo = appointmentsFromRepoOptional.get();

            assertEquals(appointmentsFromRepo, appointments);

            for (int i = weekOfAppointment + 1; i <= maxWeek; ++i) {
                assertEquals(emptyList, appointmentComponentInterface.getAppointmentsOfWeek(i));

                appointmentsFromRepoOptional = appointmentRepository.findByStartWeek(i);
                assertTrue(appointmentsFromRepoOptional.isPresent());

                appointmentsFromRepo = appointmentsFromRepoOptional.get();

                assertEquals(appointmentsFromRepo, appointmentComponentInterface.getAppointmentsOfWeek(i));
            }
        } catch (AppointmentNotFoundException | InvalidWeekException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testGetAppointmentsFromInvalidWeek() {
        final Integer invalidWeeks[] = new Integer[] {
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                0, -1, 0xFFFFFFFF, 0x7FFFFFFF,
                5000, 9999, 47053275, 452047
        };

        for (Integer invalidWeek : invalidWeeks) {
            assertThatThrownBy(() -> appointmentComponentInterface.getAppointmentsOfWeek(invalidWeek))
                    .isInstanceOf(InvalidWeekException.class);
        }
    }

    @Test
    public void testAddAppointment() {
        try {
            final String timeSpanString = String.format("TimeSpan{%s%s}",
                    "TimePoint{year=2017, month=3, day=1, hour=1, minute=0, second=0}",
                    "TimePoint{year=2017, month=3, day=1, hour=5, minute=0, second=0}");

            Appointment appointment = appointmentComponentInterface.addAppointment(timeSpan);

            assertEquals(appointment.getTimeSpan(), timeSpan);

            assertThat(appointment.getAttendees()).isEmpty();

            assertThat(appointmentRepository.findAll()).contains(appointment);

            Appointment appointmentFromStr = appointmentComponentInterface.addAppointment(timeSpanString);

            assertThat(appointmentFromStr.getAttendees()).isEmpty();

            assertThat(appointmentRepository.findAll()).contains(appointmentFromStr);
        } catch (FailedToCreateAppointmentException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testAddAppointmentInvalidTimeSpan() {
        final String invalidStrings[] = new String[] {
                null, "", "\"", "'", "eoieuie", "2)+{*", ")}*{(*{(", "42075421074",
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=3, day=1, hour=1, minute=, second=0}",
                        "TimePoint{year=2017, month=3, day=1, hour=5, minute=0, second=0}"),
                String.format("{%s%s}",
                        "TimePoint{year=2017, month=3, day=1, hour=1, minute=0, second=0}",
                        "TimePoint{year=2017, month=3, day=1, hour=5, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoi{year=2017, month=3, day=1, hour=1, minute=0, second=0}",
                        "TimePoint{year=2017, month=3, day=1, hour=5, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=3, day=1, hour=1, minute=0, second=0}",
                        "TimePoint{year=2017, month=3, day=1, hour=5, minute=0, second=0"),
                String.format("TimeSpan{%s%s",
                        "TimePoint{year=2017, month=3, day=1, hour=1, minute=0, second=0}",
                        "TimePoint{year=2017, month=3, day=1, hour=5, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=1, day=1, hour=2, minute=0, second=0}",
                        "TimePoint{year=2017, month=1, day=1, hour=1, minute=30, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=3, day=1, hour=1, minute=650, second=0}",
                        "TimePoint{year=2017, month=3, day=1, hour=5, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=3, day=1, hour=1, minute=0, second=0}",
                        "TimePoint{year=2017, month=3, day=1, hour=5, minuteueoiuedfi(pd(=d(ypdypudp")
        };

        assertThatThrownBy(() -> appointmentComponentInterface.addAppointment((TimeSpan) null))
                .isInstanceOf(FailedToCreateAppointmentException.class);

        for (String str : invalidStrings) {
            assertThatThrownBy(() -> appointmentComponentInterface.addAppointment(str))
                    .isInstanceOf(FailedToCreateAppointmentException.class);
        }
    }

    @Test
    public void testDeleteAppointment() {
        try {
            appointmentComponentInterface.deleteAppointment(appointment.getId());
        } catch (InvalidAppointmentIdException e) {
            assertEquals("Exception:", e.getMessage());
        }

        assertThat(appointmentRepository.findAll()).doesNotContain(appointment);

        assertThatThrownBy(() -> appointmentComponentInterface.deleteAppointment(appointment.getId()))
                .isInstanceOf(InvalidAppointmentIdException.class);
    }

    @Test
    public void testDeleteInvalidAppointment() {
        final Integer ids[] = new Integer[] {
                -1, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, null,
                appointment.getId() + 1, 0xFFFFFFFF, 0x7FFFFFFF
        };

        for (Integer id : ids) {
            assertThatThrownBy(() -> appointmentComponentInterface.deleteAppointment(id))
                    .isInstanceOf(InvalidAppointmentIdException.class);
        }
    }

    @Test
    public void testAddEmployeesToAppointment() {
        try {
            Employee e1 = new Employee("testFirstname", "testLastname", new EmailType("test@test.com"));
            Employee e2 = new Employee("a", "b", null);

            List<Employee> employeeList = Arrays.asList(e1, e2);

            employeeRepository.save(employeeList);

            Appointment a = appointmentComponentInterface.addEmployeesToAppointment(appointment.getId(), employeeList);

            assertThat(a.getAttendees()).containsAll(employeeList);

            Appointment appointmentGotten = appointmentComponentInterface.getAppointmentsOfWeek(a.getStartWeek()).get(0);

            assertEquals(appointmentGotten, a);

            assertThat(appointmentGotten.getAttendees()).containsAll(employeeList);

            a = appointmentComponentInterface.addEmployeesToAppointment(a.getId(), employeeList);
            final int expectedCount = 1;
            for (Employee e : employeeList) {
                assertEquals(expectedCount, Collections.frequency(a.getAttendees(), e));
            }

        } catch (InvalidEmployeeNameException | InvalidAppointmentIdException
                 | AppointmentNotFoundException | InvalidWeekException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testAddEmployeesToNonExistentAppointment() {
        final Integer ids[] = new Integer[] {
                -1, 0, appointment.getId() + 1, null, Integer.MIN_VALUE, Integer.MAX_VALUE
        };

        for (Integer id : ids) {
            assertThatThrownBy(() -> appointmentComponentInterface.addEmployeesToAppointment(id, Arrays.asList(employee1, employee2)))
                    .isInstanceOf(InvalidAppointmentIdException.class);
        }
    }

    @Test
    public void testDoesAppointmentHaveEmployees() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        List<Integer> employeeIds = new ArrayList<>();
        for (Employee e : employees) {
            employeeIds.add(e.getId());
        }

        try {
            assertTrue(appointmentComponentInterface.doesAppointmentHaveEmployees(appointment.getId(), employeeIds));
        } catch (AppointmentNotFoundException | InvalidAppointmentIdException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testDoesAppointmentHaveEmployeesInvalidAppointmentId() {
        final Integer ids[] = new Integer[] {
                0, -1, 0xFFFFFFFF, 0x7FFFFFFF, null, Integer.MAX_VALUE, Integer.MIN_VALUE,
                appointment.getId() + 1, 500, 9999
        };

        for (Integer id : ids) {
            assertThatThrownBy(() -> appointmentComponentInterface.doesAppointmentHaveEmployee(id, employee1.getId()))
                    .isInstanceOf(InvalidAppointmentIdException.class);
        }
    }

    private AppointmentComponentInterface appointmentComponentInterface;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Appointment appointment;

    private TimeSpan timeSpan;

    private Employee employee1;

    private Employee employee2;
}
