package aiApp.applicationcore.appointmentcomponent;

import org.jetbrains.annotations.Contract;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * Represents a point in time, identified by the year, month, day, hour, minute and second of that point in time.
 */
public class TimePoint implements Serializable, Comparable<TimePoint> {
    /**
     * Magic empty constructor - don't touch.
     */
    @SuppressWarnings("unused")
    TimePoint() {

    }

    /**
     * Creates a TimePoint from a year, month, day, hour, minute and second.
     * The arguments passed into the parameters must represent a real point in time.
     *
     * @param year The year of the point in time
     * @param month The month of the point in time
     * @param day The day of the point in time
     * @param hour The hour of the point in time
     * @param minute The minute of the point in time
     * @param second The second of the point in time
     * @throws InvalidDateException if any of the arguments passed into the parameters are invalid.
     */
    public TimePoint(int year, int month, int day, int hour, int minute, int second) throws InvalidDateException {
        // invalid numbers are not allowed
        if (year < 0 || month <= 0 || day <= 0 || hour < 0 || minute < 0 || second < 0) {
            throw new InvalidDateException("Negative number in TimePoint ctor.");
        }

        final int maxMonth = 12;
        final int maxDay = 31;
        final int maxHour = 23;
        final int maxMinute = 59;
        final int maxSecond = 59;

        if (month > maxMonth || day > maxDay || hour > maxHour || minute > maxMinute || second > maxSecond) {
            throw new InvalidDateException("Too large value passed to TimePoint ctor.");
        }

        // set the fields of this instance
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;

        final String datePattern = "yyyyMMdd";
        // convert to a string using the yyyyMMdd pattern
        final String dateStr = asStandardFormat();

        // check if the date is a valid one.
        SimpleDateFormat df = new SimpleDateFormat(datePattern);
        df.setLenient(false);
        try {
            df.parse(dateStr);
        } catch (ParseException e) {
            throw new InvalidDateException("Not a valid date in TimePoint ctor.");
        }
    }

    /**
     * Constant for the month of january.
     * Can be used in the TimePoint constructor.
     */
    public static final int JANUARY = 1;

    /**
     * Constant for the month of february.
     * Can be used in the TimePoint constructor.
     */
    public static final int FEBRUARY = 2;

    /**
     * Constant for the month of march.
     * Can be used in the TimePoint constructor.
     */
    public static final int MARCH = 3;

    /**
     * Constant for the month of april.
     * Can be used in the TimePoint constructor.
     */
    public static final int APRIL = 4;

    /**
     * Constant for the month of may.
     * Can be used in the TimePoint constructor.
     */
    public static final int MAY = 5;

    /**
     * Constant for the month of june.
     * Can be used in the TimePoint constructor.
     */
    public static final int JUNE = 6;

    /**
     * Constant for the month of july.
     * Can be used in the TimePoint constructor.
     */
    public static final int JULY = 7;

    /**
     * Constant for the month of august.
     * Can be used in the TimePoint constructor.
     */
    public static final int AUGUST = 8;

    /**
     * Constant for the month of september.
     * Can be used in the TimePoint constructor.
     */
    public static final int SEPTEMBER = 9;

    /**
     * Constant for the month of october.
     * Can be used in the TimePoint constructor.
     */
    public static final int OCTOBER = 10;

    /**
     * Constant for the month of november.
     * Can be used in the TimePoint constructor.
     */
    public static final int NOVEMBER = 11;

    /**
     * Constant for the month of december.
     * Can be used in the TimePoint constructor.
     */
    public static final int DECEMBER = 12;

    /**
     * Creates a TimePoint object representing the current time.
     *
     * @return The TimePoint for the current time. Will never be null.
     * @throws InvalidDateException if the date created is not a valid date. This should never happen.
     */
    @org.jetbrains.annotations.NotNull
    public static TimePoint current() throws InvalidDateException {
        LocalDateTime localDateTime = LocalDateTime.now();
        return new TimePoint(
            localDateTime.getYear(),
            localDateTime.getMonthValue(),
            localDateTime.getDayOfMonth(),
            localDateTime.getHour(),
            localDateTime.getMinute(),
            localDateTime.getSecond()
        );
    }

    /**
     * Getter for the year.
     *
     * @return Returns the year of this TimePoint
     */
    public int getYear() {
        return year;
    }

    /**
     * Getter for the month.
     *
     * @return Returns the month of this TimePoint
     */
    public int getMonth() {
        return month;
    }

    /**
     * Getter for the day.
     *
     * @return Returns the day of this TimePoint
     */
    public int getDay() {
        return day;
    }

    /**
     * Getter for the hour.
     *
     * @return Returns the hour of this TimePoint
     */
    public int getHour() {
        return hour;
    }

    /**
     * Getter for the minute.
     *
     * @return Returns the minute of this TimePoint
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Getter for the second.
     *
     * @return Returns the second of this TimePoint
     */
    public int getSecond() {
        return second;
    }

    /**
     * Determines if this instance is equal to the object passed into the parameter.
     *
     * @param o The other object to compare this instance with.
     * @return true if this object is equal to the object passed into the parameter; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        final int equal = 0;

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimePoint timePoint = (TimePoint) o;

        return compareTo(timePoint) == equal;
    }

    /**
     * Calculates the hash code of this object from all of its fields.
     *
     * @return The hash code calculated.
     */
    @Override
    public int hashCode() {
        final int magicNumber = 31;

        int result = year;
        result = magicNumber * result + month;
        result = magicNumber * result + day;
        result = magicNumber * result + hour;
        result = magicNumber * result + minute;
        result = magicNumber * result + second;

        return result;
    }

    /**
     * Creates a textual representation of this object.
     * Can be used to be able to print instances of the TimePoint type.
     *
     * @return A textual representation of this object as String.
     */
    @Override
    public String toString() {
        return String.format("{\"month\":%d,\"hour\":%d,\"year\":%d,\"day\":%d,\"minute\":%d,\"second\":%d}",
                             getMonth(), getHour(), getYear(), getDay(), getMinute(), getSecond());

    }

    /**
     * Compares this instance to another TimePoint.
     *
     * @param o The other TimePoint to compare this instance to.
     * @return Returns a negative value if the argument passed in is considered greater than the receiver.
     *         Returns a positive value if the argument passed in is considered lesser than the receiver.
     *         Return 0 if the argument passed in is equal to the receiver.
     */
    @Override
    public int compareTo(@org.jetbrains.annotations.NotNull TimePoint o) {
        // this awful code really makes me wonder if there is such a thing as the 'tie-idiom' in Java (http://en.cppreference.com/w/cpp/utility/tuple/tie).

        final int equal = 0;

        Integer thisYear = getYear();
        Integer thisMonth = getMonth();
        Integer thisDay = getDay();
        Integer thisHour = getHour();
        Integer thisMinute = getMinute();
        Integer thisSecond = getSecond();

        Integer otherYear = o.getYear();
        Integer otherMonth = o.getMonth();
        Integer otherDay = o.getDay();
        Integer otherHour = o.getHour();
        Integer otherMinute = o.getMinute();
        Integer otherSecond = o.getSecond();

        int cmpRes;
        cmpRes = thisYear.compareTo(otherYear);

        if (cmpRes != equal) {
            return cmpRes;
        }

        cmpRes = thisMonth.compareTo(otherMonth);

        if (cmpRes != equal) {
            return cmpRes;
        }

        cmpRes = thisDay.compareTo(otherDay);

        if (cmpRes != equal) {
            return cmpRes;
        }

        cmpRes = thisHour.compareTo(otherHour);

        if (cmpRes != equal) {
            return cmpRes;
        }

        cmpRes = thisMinute.compareTo(otherMinute);

        if (cmpRes != equal) {
            return cmpRes;
        }

        cmpRes = thisSecond.compareTo(otherSecond);

        if (cmpRes != equal) {
            return cmpRes;
        }

        return equal;
    }

    /**
     * Gives a String of the 'standard' format for this instance's year, month and day.
     * The 'standard' format is "yyyyMMdd", that is four digits for the year, followed by two digits for the month
     * and two digits for the day.
     *
     * @return A String of the 'standard' format of this instance's year, month and day.
     */
    String asStandardFormat() {
        return String.format("%s%s%s",
                String.format("%04d", getYear()), String.format("%02d", getMonth()), String.format("%02d", getDay()));
    }

    /**
     * Creates a TimePoint from a valid timePointString.
     * The timePointString passed in must be formatted correctly.
     * A valid timePointString is TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}
     * for instance.
     *
     * @param timePointString The timePointString to create the corresponding TimePoint for. Must be properly
     *        formatted, as specified above.
     * @return The TimePoint created from the timePointString passed in.
     * @throws ArgumentNotValidException if the timePointString passed in is null.
     * @throws InvalidDateException if the TimePoint to be created from the timePointString would be an invalid TimePoint.
     * @throws RuntimeException If the timePointString passed in is not properly formatted.
     */
    @org.jetbrains.annotations.NotNull
    @Contract("null -> fail")
    static TimePoint fromString(String timePointString) throws ArgumentNotValidException, InvalidDateException, RuntimeException {
        final String yearStr = "year=";
        final String monthStr = "month=";
        final String dayStr = "day=";
        final String hourStr = "hour=";
        final String minuteStr = "minute=";
        final String secondStr = "second=";

        final int yearIdx = 0;
        final int monthIdx = 1;
        final int dayIdx = 2;
        final int hourIdx = 3;
        final int minuteIdx = 4;
        final int secondIdx = 5;

        final int begin = 0;
        final int next = 1;

        final int notFound = -1;

        if (timePointString == null) {
            throw new ArgumentNotValidException(new String[]{"timePointString in TimePoint::fromString was null"});
        }

        if (!timePointString.startsWith("TimePoint{") || ! timePointString.endsWith("}")) {
            throw new RuntimeException("garbage string");
        }

        final String ary[] = new String[]{
                yearStr, monthStr, dayStr, hourStr, minuteStr, secondStr
        };

        int yearExtracted = 0;
        int monthExtracted = 0;
        int dayExtracted = 0;
        int hourExtracted = 0;
        int minuteExtracted = 0;
        int secondExtracted = 0;

        int idx = begin;
        for (int i = 0; i < ary.length; ++i) {
            idx = timePointString.indexOf(ary[i], idx);

            if (idx == notFound) {
                throw new RuntimeException("ary[i] could not be found in TimePoint::fromString");
            }

            idx += ary[i].length() - 1;

            final int numExtracted = getNumberAtIdx(timePointString, idx + next);

            switch (i) {
                case yearIdx:
                    yearExtracted = numExtracted;
                    break;
                case monthIdx:
                    monthExtracted = numExtracted;
                    break;
                case dayIdx:
                    dayExtracted = numExtracted;
                    break;
                case hourIdx:
                    hourExtracted = numExtracted;
                    break;
                case minuteIdx:
                    minuteExtracted = numExtracted;
                    break;
                case secondIdx:
                    secondExtracted = numExtracted;
                    break;
                default:
                    break;
            }
        }

        return new TimePoint(yearExtracted, monthExtracted, dayExtracted,
                hourExtracted, minuteExtracted, secondExtracted);
    }

    /**
     * Extracts the decimal number beginning at index idx in the String str until the end of the decimal number.
     *
     * @param str The String to extract a decimal number from.
     * @param idx The index at which the decimal number starts in the String str.
     * @return The decimal number extracted from str at idx as int.
     * @throws IndexOutOfBoundsException If the index idx passed into the second parameter is not within the range
     *         of valid indices of the String str.
     * @throws ArgumentNotValidException if a decimal number could not be extracted from the String str.
     */
    private static int getNumberAtIdx(@NotNull String str, int idx) throws RuntimeException, ArgumentNotValidException {
        // a decimal number is to be extracted.
        final int base = 10;

        if (idx >= str.length()) {
            throw new IndexOutOfBoundsException("idx out of bounds in TimePoint::getNUmberAtIdx");
        }

        int accumulator = 0;
        char curChar = str.charAt(idx);

        if (!Character.isDigit(curChar)) {
            throw new RuntimeException(String.format("no digit found in TimePoint::getNumberAtIdx\ncurChar: %s idx: %d",
                                                     curChar, idx)
            );
        }

        for (int i = idx; i < str.length() && Character.isDigit(curChar); ++i, curChar = str.charAt(i)) {
            accumulator *= base; // multiply with 10 to get the number right.
            accumulator += charAsInt(curChar); // add the current char converted to int on top.
        }

        // return the decimal number just extracted.
        return accumulator;
    }

    /**
     * Helper function to convert a char ch to an int.
     *
     * @param ch The character to be converted to an int.
     *        Must be '0', '1', '2', '3', '4', '5', '6', '7', '8', or '9'.
     * @return The number that corresponds the character passed in.
     * @throws ArgumentNotValidException if the character passed in was not a digit.
     */
    private static int charAsInt(char ch) throws ArgumentNotValidException {
        final int notFound = -1;

        // the indices of the chars in this string are the integers with the values corresponding
        // to the Character-Digits that are expected as the input to this function.
        final String digits = "0123456789";

        // get the corresponding number.
        final int res = digits.indexOf(ch);

        if (res == notFound) {
            throw new ArgumentNotValidException(new String[]{String.format("Invalid char: %c", ch)});
        }

        return res;
    }

    /**
     * The year of this TimePoint
     */
    private int year;

    /**
     * The month of this TimePoint
     */
    private int month;

    /**
     * The day of this TimePoint
     */
    private int day;

    /**
     * The hour of this TimePoint
     */
    private int hour;

    /**
     * The minute of this TimePoint
     */
    private int minute;

    /**
     * The second of this TimePoint
     */
    private int second;
}
