package aiApp.applicationcore.appointmentcomponent;

import javax.validation.constraints.NotNull;

/**
 * Thrown in TimePoint::TimePoint(int, int, int int, int, int)
 */
public class InvalidDateException extends Throwable {
    /**
     * Creates a new InvalidDateException.
     *
     * @param errorMsg The error message to use. May not be null.
     */
    InvalidDateException(@NotNull String errorMsg) {
        super(errorMsg);
    }
}
