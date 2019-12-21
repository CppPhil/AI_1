package aiApp.applicationcore.appointmentcomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown in AppointmentComponent::addEmployeesToAppointment(Integer, List<Employee>
 * and in AppointmentComponent::doesAppointmentHaveEmployee(Integer, Integer)
 * and in AppointmentComponent::getAppointmentsOfWeek(Integer).
 */
public class AppointmentNotFoundException extends Throwable {
    /**
     * Creates a new AppointmentNotFoundException object.
     *
     * @param errorMessage The error message to use. May not be null.
     */
    AppointmentNotFoundException(@NotNull String errorMessage) {
        super(errorMessage);
    }
}
