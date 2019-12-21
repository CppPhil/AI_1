package aiApp.applicationcore.appointmentcomponent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class TimePointTest {
    @Before
    public void setUp() {
        try {
            timePoint = new TimePoint(year, month, day, hour, minute, second);
        } catch (InvalidDateException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateTimePoint() {
        try {
            final int yearToUse = 2017;
            final int monthToUse = TimePoint.SEPTEMBER;
            final int dayToUse = 1;
            final int hourToUse = 23;
            final int minuteToUse = 7;
            final int secondToUse = 4;

            TimePoint tp = new TimePoint(yearToUse, monthToUse, dayToUse, hourToUse, minuteToUse, secondToUse);
            tp = TimePoint.current();

            assertTrue(true);
        } catch (InvalidDateException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testCreateTimePointInvalid() {
        assertThatThrownBy(() -> new TimePoint(year, month, day, hour, minute, -1))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(year, month, day, hour, -1, second))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(year, month, day, -1, minute, second))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(year, month, 0, hour, minute, second))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(year, 0, day, hour, minute, second))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(-1, month, day, hour, minute, second))
                .isInstanceOf(InvalidDateException.class);

        final int maxMonth = TimePoint.DECEMBER;
        final int maxDay = 31;
        final int maxMinute = 59;
        final int maxSecond = 59;

        assertThatThrownBy(() -> new TimePoint(year, maxMonth + 1, day, hour, minute, second))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(year, month, maxDay + 1, hour, minute, second))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(year, month, day, hour, maxMinute + 1, second))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(year, month, day, hour, minute, maxSecond + 1))
                .isInstanceOf(InvalidDateException.class);

        assertThatThrownBy(() -> new TimePoint(2017, TimePoint.FEBRUARY, 29, 0, 0, 0))
                .isInstanceOf(InvalidDateException.class);
    }

    @Test
    public void testTimePointGetters() {
        assertEquals(year, timePoint.getYear());
        assertEquals(month, timePoint.getMonth());
        assertEquals(day, timePoint.getDay());
        assertEquals(hour, timePoint.getHour());
        assertEquals(minute, timePoint.getMinute());
        assertEquals(second, timePoint.getSecond());
    }

    @Test
    public void testEquals() {
        try {
            TimePoint tp = new TimePoint(year, month, day, hour, minute, second);
            assertEquals(timePoint, tp);

            assertThat(timePoint).isNotEqualTo(null);

            assertThat(timePoint).isNotEqualTo(5);
        } catch (InvalidDateException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testCompareTo() {
        try {
            TimePoint equal = new TimePoint(year, month, day, hour, minute, second);

            assertEquals(0, timePoint.compareTo(equal));

            TimePoint before = new TimePoint(year - 1, month, day, hour, minute, second);

            assertTrue(timePoint.compareTo(before) > 0);

            TimePoint after = new TimePoint(year, month, day, hour, minute, second + 1);

            assertTrue(timePoint.compareTo(after) < 0);
        } catch (InvalidDateException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testFromString() {
        try {
            TimePoint tp = TimePoint.fromString(validTimePointString);
            assertTrue(true);

            assertEquals(2017, tp.getYear());
            assertEquals(TimePoint.APRIL, tp.getMonth());
            assertEquals(15, tp.getDay());
            assertEquals(17, tp.getHour());
            assertEquals(0, tp.getMinute());
            assertEquals(0, tp.getSecond());
        } catch (ArgumentNotValidException | InvalidDateException | RuntimeException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testFromStringNull() {
        assertThatThrownBy(() -> TimePoint.fromString(null))
                .isInstanceOf(ArgumentNotValidException.class);
    }

    @Test
    public void testFromStringInvalidDate() {
        final String str = "TimePoint{year=2017, month=2, day=29, hour=17, minute=0, second=0}";

        assertThatThrownBy(() -> TimePoint.fromString(str))
                .isInstanceOf(InvalidDateException.class);

    }

    @Test
    public void testFromStringGarbage() {
        final String garbageArray[] = new String[] {
                "", " ", "oeioei", "}{(}*(=", "51595959",
                "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0",
                "TimePointyear=2017, month=4, day=15, hour=17, minute=0, second=0}",
                "TimePoint{year=2017, month=4, day=15, hour17, minute=0, second=0}",
                "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=}",
                "{year=2017, month=4, day=15, hour=17, minute=0, second=0}",
                "Point{year=2017, month=4, day=15, hour=17, minute=0, second=0}"
        };

        for (String s : garbageArray) {
            System.out.println(s);
            assertThatThrownBy(() -> TimePoint.fromString(s))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    private final int year = 2016;
    private final int month = TimePoint.JANUARY;
    private final int day = 1;
    private final int hour = 0;
    private final int minute = 0;
    private final int second = 0;
    private TimePoint timePoint;
    private final String validTimePointString = "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}";
}
