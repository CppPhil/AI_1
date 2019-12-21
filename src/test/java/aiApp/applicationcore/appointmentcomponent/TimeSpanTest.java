package aiApp.applicationcore.appointmentcomponent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class TimeSpanTest {
    @Before
    public void setUp() {
        try {
            beginTimePoint = new TimePoint(beginYear, beginMonth, beginDay, beginHour, beginMinute, beginSecond);
            endTimePoint = new TimePoint(endYear, endMonth, endDay, endHour, endMinute, endSecond);
            timeSpan = new TimeSpan(beginTimePoint, endTimePoint);
        } catch (InvalidDateException | InvalidTimePointException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateTimeSpan() {
        try {
            @SuppressWarnings("unused") TimeSpan aTimeSpan = new TimeSpan(beginTimePoint, endTimePoint);
            assertTrue(true);
        } catch (InvalidTimePointException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testCreateTimeSpanInvalid() {
        assertThatThrownBy(() -> new TimeSpan(null, null))
                .isInstanceOf(InvalidTimePointException.class);

        assertThatThrownBy(() -> new TimeSpan(null, endTimePoint))
                .isInstanceOf(InvalidTimePointException.class);

        assertThatThrownBy(() -> new TimeSpan(beginTimePoint, null))
                .isInstanceOf(InvalidTimePointException.class);

        assertThatThrownBy(() -> new TimeSpan(endTimePoint, beginTimePoint))
                .isInstanceOf(InvalidTimePointException.class);
    }

    @Test
    public void testFromString() {
        try {
            TimeSpan testTimeSpan = TimeSpan.fromString(validTimeSpanString);
            assertTrue(true);
        } catch (ArgumentNotValidException | InvalidDateException | InvalidTimePointException | RuntimeException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testFromStringFailure() {
        final String garbage[] = new String[] {
                null, "", " ", "%&&{&%", "}yioeie",
                validTimeSpanString + "otnauhonetu"
        };

        for (String garbageString : garbage) {
            assertThatThrownBy(() -> TimeSpan.fromString(garbageString))
                    .isInstanceOf(ArgumentNotValidException.class);
        }

        String str = String.format(timeSpanFormatString,
                2017, 4, 15, 16, 2, 2,
                2017, 3, 0, 0, 0, 0);

        final String finalStr = str;
        assertThatThrownBy(() -> TimeSpan.fromString(finalStr))
                .isInstanceOf(InvalidDateException.class);

        str = String.format(timeSpanFormatString,
                2017, 2, 29, 0, 0, 0,
                2017, 1, 1, 1, 1, 1);

        final String finalStr1 = str;
        assertThatThrownBy(() -> TimeSpan.fromString(finalStr1))
                .isInstanceOf(InvalidDateException.class);
    }

    @Test
    public void testTimeSpanGetters() {
        assertEquals(beginTimePoint, timeSpan.getStartTimePoint());
        assertEquals(endTimePoint, timeSpan.getEndTimePoint());
    }

    @Test
    public void testEquals() {
        try {
            TimeSpan testTimeSpan = new TimeSpan(beginTimePoint, endTimePoint);
            assertEquals(timeSpan, testTimeSpan);

            assertThat(timeSpan).isNotEqualTo(null);

            assertThat(timeSpan).isNotEqualTo(new ArrayList<String>());

            assertThat(timeSpan).isNotEqualTo(TimeSpan.fromString(validTimeSpanString));

        } catch (InvalidTimePointException | ArgumentNotValidException | InvalidDateException | RuntimeException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    private final int beginYear = 2017;
    private final int beginMonth = TimePoint.APRIL;
    private final int beginDay = 17;
    private final int beginHour = 22;
    private final int beginMinute = 0;
    private final int beginSecond = 0;
    private TimePoint beginTimePoint;
    private final int endYear = 2017;
    private final int endMonth = TimePoint.APRIL;
    private final int endDay = 17;
    private final int endHour = 23;
    private final int endMinute = 0;
    private final int endSecond = 0;
    private TimePoint endTimePoint;
    private TimeSpan timeSpan;
    private final String validTimeSpanString = "TimeSpan{TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}}";
    private final String timeSpanFormatString = "TimeSpan{TimePoint{year=%d, month=%d, day=%d, hour=%d, minute=%d, second=%d}TimePoint{year=%d, month=%d, day=%d, hour=%d, minute=%d, second=%d}}";
}
