package aiApp.applicationcore.appointmentcomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown in AppointmentComponent::throwIfAppointmentIdIsInvalid(Integer)
 */
public class InvalidAppointmentIdException extends Throwable {
    /**
     * Creates a new InvalidAppointmentIdException.
     *
     * @param errorMsg The error message to use. May not be null.
     */
    InvalidAppointmentIdException(@NotNull String errorMsg) {
        super(errorMsg);
    }
}
