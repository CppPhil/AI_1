package aiApp.applicationcore.appointmentcomponent;

import aiApp.applicationcore.employeecomponent.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The appointment component class that implements the AppointmentComponentInterface.
 */
@Component
public class AppointmentComponent implements AppointmentComponentInterface {
    /**
     * Creates a new AppointmentComponent instance.
     *
     * @param appointmentRepository The AppointmentRepository to use.
     */
    @Autowired
    public AppointmentComponent(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Returns the appointments of a given week.
     * If the week is null, all appointments are returned.
     *
     * @param week The week for which to get the appointments that begin in that week. Or null to get all appointments.
     * @return A list of the appointments that begin in the week passed into the parameter.
     *         Or a list of all appointments if week was null.
     *         May be an empty list if there are no appointments beginning in the week passed into the parameter or
     *         no appointments exist at all.
     * @throws AppointmentNotFoundException If the optional returned by the repository didn't have a value.
     *         Should never occur, if no appointments begin in the week given or no appointments exist at all
     *         an empty list should be returned.
     * @throws InvalidWeekException If the week passed in was invalid. Week must be greater than 0 and may not be
     *         larger than 52.
     */
    @Override
    public List<Appointment> getAppointmentsOfWeek(Integer week) throws AppointmentNotFoundException, InvalidWeekException {
        final int maxWeeks = 52;

        if (week == null) {
            return appointmentRepository.findAll();
        }

        if (week <= 0 || week > maxWeeks) {
            throw new InvalidWeekException("week was invalid");
        }

        Optional<List<Appointment>> res = appointmentRepository.findByStartWeek(week);
        if (res.isPresent()) {
            return res.get();
        } else {
            throw new AppointmentNotFoundException("Optional was empty in AppointmentComponent::getAppointmentsOfWeek.");
        }
    }

    /**
     * Creates a new appointment from a TimeSpan.
     *
     * @param timeSpan The TimeSpan to create a new appointment from.
     * @return The appointment created.
     * @throws FailedToCreateAppointmentException if the appointment could not be created.
     *         Possible reasons include: The TimeSpan was invalid, or begins in an invalid week.
     */
    @Override
    public Appointment addAppointment(TimeSpan timeSpan) throws FailedToCreateAppointmentException {
        final String exceptStr = "Could not create appointment, reason: ";

        Appointment newAppointment;

        try {
            newAppointment = new Appointment(timeSpan);
        } catch (InvalidWeekException | InvalidTimeSpanException e) {
            throw new FailedToCreateAppointmentException(
                exceptStr + e.getMessage()
            );
        }

        appointmentRepository.save(newAppointment);

        return newAppointment;
    }

    /**
     * Creates a new appointment from a string that represents a TimeSpan.
     *
     * @param timeSpanString The string that represents a TimeSpan to create the appointment from.
     *        Must be correctly formatted. Example of a correct timeSpanString below:
     *        TimeSpan{TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}}
     * @return The Appointment created.
     * @throws FailedToCreateAppointmentException if the timeSpanString passed into the parameter was incorrectly formatted
     *         or did not represent a valid TimeSpan, albeit being in the correct format.
     */
    @Override
    public Appointment addAppointment(String timeSpanString) throws FailedToCreateAppointmentException {
        if (timeSpanString == null) {
            throw new FailedToCreateAppointmentException("timeSpanString was null in AppointmentComponent::addAppointment/1");
        }

        TimeSpan theTimeSpan;

        try {
            theTimeSpan = TimeSpan.fromString(timeSpanString);
        } catch (InvalidDateException | InvalidTimePointException | ArgumentNotValidException | RuntimeException e) {
            throw new FailedToCreateAppointmentException("Could not create appointment, reason: " + e.getMessage());
        }

        return addAppointment(theTimeSpan);
    }

    /**
     * Deletes an appointment identified by its ID.
     *
     * @param appointmentId The ID of the appointment to delete.
     * @throws InvalidAppointmentIdException if the appointmentId given was invalid, that is it was null,
     *         less than 1, or no appointment with that ID was to be found.
     */
    @Override
    public void deleteAppointment(Integer appointmentId) throws InvalidAppointmentIdException {
        throwIfAppointmentIdIsInvalid(appointmentId);

        appointmentRepository.delete(appointmentId);
    }

    /**
     * Adds employees to an existing appointment.
     * If null or an empty list is passed into the employeesToAdd parameter nothing happens.
     * Likewise if all of the employees in employeesToAdd are already registered as attendees of the appointment
     * identified by the appointmentId nothing happens as well.
     * Note that the employees corresponding to the employeeIds in employeesToAdd must actually exist.
     * The EmployeeComponent class offers a member function called doAllEmployeesExist to check for their existence.
     *
     * @param appointmentId The ID of the appointment to add the employees to.
     * @param employeesToAdd The IDs of the employees to add to the appointment. All of these employees must exist!
     * @return The Appointment that the employees were added to.
     * @throws InvalidAppointmentIdException If the appointmentId passed in was invalid.
     * @throws AppointmentNotFoundException If an error occurred trying to fetch the Appointment that is identified by
     *         the appointmentId.
     */
    @Override
    public Appointment addEmployeesToAppointment(Integer appointmentId, List<Employee> employeesToAdd) throws InvalidAppointmentIdException, AppointmentNotFoundException {
        throwIfAppointmentIdIsInvalid(appointmentId);

        if (employeesToAdd == null) {
            employeesToAdd = new ArrayList<>();
        }

        Appointment targetAppointment = appointmentRepository.findOne(appointmentId);

        if (targetAppointment == null) {
            throw new AppointmentNotFoundException("Appointment couldn't be found in AppointmentComponent::addEmployeesToAppointment");
        }

        targetAppointment.addEmployees(employeesToAdd);

        targetAppointment = appointmentRepository.save(targetAppointment);

        return targetAppointment;
    }

    /**
     * Determines if the employee identified by employeeId is an attendee of the appointment identified by
     * appointmentId.
     *
     * @param appointmentId The id of the appointment of which it shall be determined whether the employee identified
     *        by employeeId is an attendee of.
     * @param employeeId The id of the employee to determine for whether he is an attendee of the appointment identified
     *        by the appointmentId passed into the first parameter.
     * @return true if the employee identified by employeeId is an attendee of the appointment identified by appointmentId;
     *         false otherwise.
     * @throws InvalidAppointmentIdException if the appointmentId was not a valid appointment ID.
     * @throws AppointmentNotFoundException if an error occurred trying to fetch the Appointment that is identified by
     *         the appointmentId.
     */
    @Override
    public boolean doesAppointmentHaveEmployee(Integer appointmentId, Integer employeeId) throws InvalidAppointmentIdException, AppointmentNotFoundException {
        throwIfAppointmentIdIsInvalid(appointmentId);

        if (employeeId == null) {
            return false;
        }

        Appointment appointment = appointmentRepository.findOne(appointmentId);

        if (appointment == null) {
            throw new AppointmentNotFoundException("Appointment couldn't be found in AppointmentComponent::doesAppointmentHaveEmployee");
        }

        return appointment.hasAttendee(employeeId);
    }

    /**
     * Determines whether all the employees identified by the employeeIds in the List of employee IDs passed into the
     * second parameter are attendees of the appointment identified by the appointmentId passed into the first parameter.
     *
     * @param appointmentId The id of the appointment of which it shall be determined whether every employee
     *        identified by their respective employee ID in the list of employee IDs passed into the second parameter
     *        are attendees of.
     * @param employeeIds A list containing employee IDs for which it shall be determined whether every employee
     *        identified by their respective employee ID from that list is an attendee of the appointment identified
     *        by the appointmentId passed into the first parameter.
     * @return true if every employee identified by their respective employee ID from employeeIds is an attendee of
     *         the appointment identified by appointmentId.
     * @throws AppointmentNotFoundException if an error occurred trying to fetch the Appointment that is identified by
     *         the appointmentId.
     * @throws InvalidAppointmentIdException if the appointmentId was invalid.
     */
    @Override
    public boolean doesAppointmentHaveEmployees(Integer appointmentId, List<Integer> employeeIds) throws AppointmentNotFoundException, InvalidAppointmentIdException {
        for (Integer employeeId : employeeIds) {
            if (!doesAppointmentHaveEmployee(appointmentId, employeeId)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Helper function that throws InvalidAppointmentIdException if the appointmentId passed in is invalid.
     *
     * @param appointmentId The appointmentId to check the validity of.
     * @throws InvalidAppointmentIdException if the appointmentId passed in is invalid.
     */
    private void throwIfAppointmentIdIsInvalid(Integer appointmentId) throws InvalidAppointmentIdException {
        if (appointmentId == null || appointmentId <= 0 || !appointmentRepository.exists(appointmentId)) {
            throw new InvalidAppointmentIdException("appointmentId was invalid");
        }
    }

    /**
     * Autowiring by Constructor-Injection
     */
    private AppointmentRepository appointmentRepository;
}
