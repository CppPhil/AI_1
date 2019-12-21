package aiApp.applicationcore.appointmentcomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown by Appointment::determineStartWeek and AppointmentComponent::getAppointmentsOfWeek.
 */
public class InvalidWeekException extends Throwable {
    /**
     * Creates an InvalidWeekException object.
     *
     * @param errorMsg The error message to use. May not be null.
     */
    InvalidWeekException(@NotNull String errorMsg) {
        super(errorMsg);
    }
}
