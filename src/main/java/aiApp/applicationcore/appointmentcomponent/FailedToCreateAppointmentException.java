package aiApp.applicationcore.appointmentcomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown in AppointmentComponent::addAppointment.
 */
public class FailedToCreateAppointmentException extends Throwable {
    /**
     * Creates a new FailedToCreateAppointmentException object.
     *
     * @param errorMsg The error message to use. May not be null.
     */
    FailedToCreateAppointmentException(@NotNull String errorMsg) {
        super(errorMsg);
    }
}
