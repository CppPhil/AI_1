package aiApp.applicationcore.appointmentcomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown in Appointment::Appointment(TimeSpan, List<Employee>.
 */
public class InvalidTimeSpanException extends Throwable {
    /**
     * Creates an InvalidTimeSpanException object.
     *
     * @param errorMessage The error message to use. May not be null.
     */
    InvalidTimeSpanException(@NotNull String errorMessage) {
        super(errorMessage);
    }
}
