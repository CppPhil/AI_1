package aiApp.applicationcore.appointmentcomponent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * Represents a span of time, that is a span from one point in time to another.
 * Both of the time points are considered part of the range, thus creating a closed range.
 */
public class TimeSpan implements Serializable {
    /**
     * magic empty constructor - don't touch.
     */
    @SuppressWarnings("unused")
    public TimeSpan() {

    }

    /**
     * Creates a TimeSpan from a begin and an end TimePoint.
     *
     * @param begin The TimePoint at which the TimeSpan begins.
     * @param end The TimePoint at which the TimeSpan ends.
     * @throws InvalidTimePointException if either of the parameters is null or end represents an earlier point in time
     *         than begin.
     */
    public TimeSpan(TimePoint begin, TimePoint end) throws InvalidTimePointException {
        if (begin == null || end == null || (begin.compareTo(end) > 0)) {
            throw new InvalidTimePointException("begin or end was null, or end was an earlier point in time than begin in TimeSpan ctor.");
        }

        this.begin = begin;
        this.end = end;
    }

    /**
     * Creates a TimeSpan from a time span string.
     * The time span string must be formatted properly.
     * Example of valid time span string:
     * TimeSpan{TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}}
     *
     * @param timeSpanString The time span string, must have the format specified above.
     * @return The TimeSpan created from the timeSpanString.
     * @throws ArgumentNotValidException if the timeSpanString is invalid.
     * @throws InvalidDateException if one of the dates of one of the TimePoints that the TimeSpan would consist of
     *         would be invalid if constructed from the timeSpanString passed in.
     * @throws InvalidTimePointException if one of the TimePoints made to construct a TimeSpan from was invalid.
     * @throws RuntimeException if either timePointString created from the timeSpanString is not correctly formatted in TimePoint.
     */
    @Contract("null -> fail")
    @NotNull
    public static TimeSpan fromString(String timeSpanString) throws ArgumentNotValidException, InvalidDateException, InvalidTimePointException, RuntimeException {
        if (timeSpanString == null) {
            throw new ArgumentNotValidException(new String[]{"timeSpanString in TimeSpan::fromString was null."});
        }

        if (!isTimeSpanStringOk(timeSpanString)) {
            throw new ArgumentNotValidException(new String[]{"timeSpanString in TimeSpan::fromString was invalid"});
        }

        final String timePoint = "TimePoint";

        // get the beginning of the first "TimePoint"-String.
        final int beginOfFirstTimePointString = timeSpanString.indexOf(timePoint);

        // this index will refer to the 't'-Character at the end of the first "TimePoint"-String
        final int endOfFirstTimePointString = beginOfFirstTimePointString + (timePoint.length() - 1);

        // get the beginning of the second "TimePoint"-String
        final int beginOfSecondTimePoint = timeSpanString.indexOf(timePoint, endOfFirstTimePointString);

        // from the 'T'-character that the first "TimePoint"-String starts out with
        // to the character just before where the second TimePoint-Part begins.
        String firstTimePointStr = timeSpanString.substring(beginOfFirstTimePointString, beginOfSecondTimePoint);

        // from the 'T-'character that the second "TimePoint"-String starts out with
        // to the end, excluding the closing curly brace, that belongs to the surrounding TimeSpan.
        String secondTimePointStr = timeSpanString.substring(beginOfSecondTimePoint, timeSpanString.length() - 1);

        // create the first time point
        TimePoint firstTimePoint = TimePoint.fromString(firstTimePointStr);

        // create the second time point
        TimePoint secondTimePoint = TimePoint.fromString(secondTimePointStr);

        // create the TimeSpan
        return new TimeSpan(firstTimePoint, secondTimePoint);
    }

    /**
     * Getter for the start time point.
     *
     * @return The TimePoint at which this TimeSpan begins.
     */
    public TimePoint getStartTimePoint() {
        return begin;
    }

    /**
     * Getter for the end time point.
     *
     * @return The TimePoint at which this TimeSpan ends.
     */
    public TimePoint getEndTimePoint() {
        return end;
    }

    /**
     * Compares this instance for equality with another Object.
     *
     * @param o The other Object to compare to.
     * @return true if this instance is considered equal to the Object passed into the parameter;
     *         false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimeSpan timeSpan = (TimeSpan) o;

        return (begin.equals(timeSpan.begin))
                && (end.equals(timeSpan.end));
    }

    /**
     * Calculates the hash code for this object.
     *
     * @return The hash code calculated.
     */
    @Override
    public int hashCode() {
        final int magicNumber = 31;

        int result = begin.hashCode();
        result = magicNumber * result + end.hashCode();
        return result;
    }

    /**
     * Creates a textual representation of this object, allowing it to be printed.
     *
     * @return A String that holds the textual representation of this object.
     */
    @Override
    public String toString() {
        return String.format("{\"startTimePoint\":%s,\"endTimePoint\":%s}",
                             getStartTimePoint(), getEndTimePoint());

    }

    /**
     * Checks if the timeSpanString passed in is ok.
     *
     * @param timeSpanString The timeSpanString to check.
     * @return true if the timeSpanString passed in is considered 'ok'; false otherwise.
     */
    @Contract("null -> false")
    private static boolean isTimeSpanStringOk(String timeSpanString) {
        final int expectedTimePointOccurrences = 2;
        final int expectedCurlyBracePairs = 3;

        final String timePointStr = "TimePoint";
        final String openingCurlyBrace = "{";
        final String closingCurlyBrace = "}";

        return (timeSpanString != null)
                && timeSpanString.endsWith("}}")
                && timeSpanString.startsWith("TimeSpan")
                && (StringUtils.countOccurrencesOf(timeSpanString, timePointStr) == expectedTimePointOccurrences)
                && !((StringUtils.countOccurrencesOf(timeSpanString, openingCurlyBrace) != expectedCurlyBracePairs)
                || (StringUtils.countOccurrencesOf(timeSpanString, closingCurlyBrace) != expectedCurlyBracePairs));

    }

    /**
     * The start TimePoint, where the TimeSpan begins.
     * Considered to be part of the range represented by this TimeSpan.
     */
    private TimePoint begin;

    /**
     * The end TimePoint, where the TimeSpan ends.
     * Considered to be part of the range represented by this TimeSpan.
     */
    private TimePoint end;
}
