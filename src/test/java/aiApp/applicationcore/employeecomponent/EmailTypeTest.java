package aiApp.applicationcore.employeecomponent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class EmailTypeTest {
    @Before
    public void setUp() {
        try {
            email = new EmailType("test@test.de");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreate() {
        final String invalidMails[] = new String[] {
                "", " ", " oa ", "@@@@@@@", "" +
                "                      uu",
                "testmail@mail.mail@mail",
                "@mail.de"
        };

        assertThatThrownBy(() -> new EmailType(null))
                .isInstanceOf(IllegalArgumentException.class);

        for (String invalidMail : invalidMails) {
            assertThatThrownBy(() -> new EmailType(invalidMail))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void testEquals() {
        try {
            EmailType emailType = new EmailType("test@test.de");
            assertEquals(email, emailType);

            assertThat(email).isNotEqualTo(null);

            assertThat(email).isNotEqualTo(new HashMap<String, Integer>());

            assertThat(email).isNotEqualTo(new EmailType("test@test.com"));
        } catch (IllegalArgumentException e) {
            assertEquals("Exception:", e.getMessage());
        }
    }

    @Test
    public void testHashCode() {
        final String str = "mail@mail.de";
        try {
            EmailType emailType = new EmailType(str);

            assertThat(str.hashCode()).isEqualTo(emailType.hashCode());
        } catch (IllegalArgumentException e) {
            assertEquals("Exception:", e.getMessage());
        }

    }

    private EmailType email;
}
