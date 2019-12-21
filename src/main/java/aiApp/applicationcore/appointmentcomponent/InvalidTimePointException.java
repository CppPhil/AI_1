package aiApp.applicationcore.appointmentcomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown in TimeSpan::TimeSpan(TimePoint, TimePoint) if one of the TimePoints is invalid.
 */
public class InvalidTimePointException extends Throwable {
    /**
     * Creates a new InvalidTimePointException object.
     *
     * @param errorMsg The error message to use. May not be null.
     */
    InvalidTimePointException(@NotNull String errorMsg) {
        super(errorMsg);
    }
}
