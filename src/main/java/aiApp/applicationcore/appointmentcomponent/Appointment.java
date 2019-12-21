package aiApp.applicationcore.appointmentcomponent;

import aiApp.applicationcore.employeecomponent.Employee;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Entity class to represent an appointment.
 */
@Entity
public class Appointment implements Serializable {
    /**
     * magic empty constructor - don't touch.
     */
    @SuppressWarnings("unused")
    public Appointment() {

    }

    /**
     * Creates a new Appointment instance from a TimeSpan.
     *
     * @param timeSpan The TimeSpan for which to schedule the Appointment.
     * @throws InvalidTimeSpanException If the TimeSpan passed in is null.
     * @throws InvalidWeekException If the TimeSpan passed in does not begin in a valid week.
     */
    public Appointment(TimeSpan timeSpan) throws InvalidTimeSpanException, InvalidWeekException {
        this(timeSpan, null);
    }

    /**
     * Getter for the id of the appointment.
     *
     * @return The id of the appointment.
     */
    @SuppressWarnings("unused")
    public Integer getId() {
        return id;
    }

    /**
     * Getter for the TimeSpan of the appointment.
     *
     * @return the TimeSpan of the appointment.
     */
    @SuppressWarnings("unused")
    public TimeSpan getTimeSpan() {
        return timeSpan;
    }

    /**
     * Returns the week in which the appointment begins.
     *
     * @return The week in which the appointment begins. Guaranteed to not be null.
     */
    @NotNull
    @SuppressWarnings("unused")
    public Integer getStartWeek() {
        return startWeek;
    }

    /**
     * Getter for the attendees field.
     *
     * @apiNote While this getter seems completely unnecessary, it is in fact necessary as stupid freaking JPA
     *          won't bloody show the stupid ass attendees otherwise, piece of fucking shit!
     * @return the attendees field.
     */
    @SuppressWarnings("unused")
    public ArrayList<Employee> getAttendees() {
        return attendees;
    }

    /**
     * Function to add employees to this appointment as attendees.
     *
     * @param attendeesToAdd The employees to add to this appointment as attendees of this appointment.
     * @apiNote Does nothing if the list passed into the parameter is null.
     * @apiNote Only add in employees that actually exist in their repository.
     *          The EmployeeComponent class offers a function doAllEmployeesExist for that as well as
     *          doesEmployeeExist.
     */
    public void addEmployees(List<Employee> attendeesToAdd) {
        if (attendeesToAdd != null) {
            for (Employee e : attendeesToAdd) {
                addEmployee(e);
            }
        }
    }

    /**
     * Determines if this appointment has the employee identified by the employeeId passed into the parameter
     * registered as an attendee.
     *
     * @param employeeId The id of the employee to determine whether or not he is an attendee of this appointment.
     * @return true if the employee identified by employeeId is an attendee of this appointment; false otherwise.
     */
    @Contract("null -> false")
    public boolean hasAttendee(Integer employeeId) {
        if (employeeId == null) {
            return false;
        }

        for (Employee attendee: attendees) {
            if (employeeId.equals(attendee.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Compares this object to another for equality.
     * This object is considered equal to the object passed into the parameter if both objects
     * are of type Appointment and their ids compare equal.
     *
     * @param other The other object to compare this object to.
     * @return true if this object is considered equal to the object passed into the parameter.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Appointment that = (Appointment) other;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    /**
     * Returns the hash code for this object, which is the hash code of its id.
     *
     * @return The hash code of this object's id.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Creates a textual representation of this object in order to make it printable.
     *
     * @return A String that holds the textual representation of this object.
     */
    @Override
    public String toString() {
        return String.format("{\"startWeek\":%d,\"attendees\":%s,\"timeSpan\":%s,\"id\":%d}",
                             getStartWeek(), attendeesAsString(), getTimeSpan(), getId());
    }

    /**
     * Constructor used by the public constructor.
     * Creates a new Appointment instance.
     *
     * @param timeSpan The TimeSpan of the appointment to create.
     * @param attendees The attendees of the appointment, always null. The employees that attend this appointment
     *        are to be added after the appointment has been created.
     * @throws InvalidTimeSpanException if timeSpan is null.
     * @throws InvalidWeekException if the TimeSpan passed into the first parameter doesn't begin in a valid week or it is null.
     */
    private Appointment(TimeSpan timeSpan, List<Employee> attendees) throws InvalidTimeSpanException, InvalidWeekException {
        if (timeSpan == null) {
            throw new InvalidTimeSpanException("timeSpan in Appointment ctor was null.");
        }

        this.timeSpan = timeSpan;

        // we don't wont a null attendees data member, so replace it with an empty list if the argument passed in was null.
        if (attendees == null) {
            attendees = new ArrayList<>();
        }

        this.attendees = new ArrayList<>();
        for (Employee e : attendees) {
            addEmployee(e);
        }

        startWeek = determineStartWeek(this.timeSpan);
    }

    /**
     * Returns the week in which a given TimeSpan starts.
     *
     * @param timeSpan The TimeSpan to get the start week of.
     * @return The week in which the TimeSpan given starts.
     * @throws InvalidWeekException if the TimeSpan passed in doesn't begin in a valid week or is null.
     */
    @NotNull
    @Contract("null -> fail")
    static private Integer determineStartWeek(TimeSpan timeSpan) throws InvalidWeekException {
        if (timeSpan == null) {
            throw new InvalidWeekException("A null TimeSpan does not have a start week.");
        }

        try {
            final String format = "yyyyMMdd";

            TimePoint startTimePoint = timeSpan.getStartTimePoint();
            String formattedTimeString = startTimePoint.asStandardFormat();

            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Date date = dateFormat.parse(formattedTimeString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            return calendar.get(Calendar.WEEK_OF_YEAR);
        } catch (ParseException e) {
            throw new InvalidWeekException("tried to create an appointment that would have an invalid start week");
        }
    }

    /**
     * Adds the employee passed in as an attendee of this appointment if the employee passed in is not null
     * and is not already an attendee of this appointment.
     *
     * @param attendeeToAdd The employee to add to the list of attendees of this appointment.
     */
    private void addEmployee(Employee attendeeToAdd) {
        if (attendeeToAdd != null && !hasAttendee(attendeeToAdd)) {
            attendees.add(attendeeToAdd);
        }
    }

    /**
     * Determines whether the employee passed in is an attendee of this appointment.
     *
     * @param employee The employee for which to check whether he is an attendee of this appointment.
     * @return true if the employee passed in is an attendee of this appointment; false otherwise.
     *         If null is passed in false is returned.
     */
    @Contract("null -> false")
    private boolean hasAttendee(Employee employee) {
        if (employee == null) {
            return false;
        }

        for (Employee attendee : attendees) {
            if (employee.equals(attendee)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper function to create the string for the attendees non static data member.
     *
     * @return The string for the attendees non static data member.
     */
    @NotNull
    private String attendeesAsString() {
        final int numAttendees = getAttendees().size();
        final int lastAttendeeIdx = numAttendees - 1;

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < numAttendees; ++i) {
            sb.append(getAttendees().get(i).toString());

            if (i != lastAttendeeIdx) {
                sb.append(",");
            }
        }

        sb.append("]");

        return sb.toString();
    }

    /**
     * The id of the employee.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * The time span for which this appointment is scheduled.
     * The Column annotation makes it so that the application doesn't crash on startup.
     */
    @Column(length = 100000)
    private TimeSpan timeSpan;

    /**
     * the week in which the appointment begins.
     */
    private Integer startWeek;

    /**
     * The attendees of this appointment.
     * The Column annotation makes it so that the application doesn't crash on startup.
     * ArrayList as JPA will otherwise be too damn stupid to 'serialize' the bloody thing, thanks a lot!
     */
    @Column(length = 100000)
    private ArrayList<Employee> attendees;
}
