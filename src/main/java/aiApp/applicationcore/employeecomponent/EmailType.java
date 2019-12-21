package aiApp.applicationcore.employeecomponent;

import java.io.Serializable;

/**
 * Created by srs on 08.12.15.
 */
public class EmailType implements Serializable {
    @SuppressWarnings("unused")
    public EmailType() {

    }

    public EmailType(String email) {
        if (email == null) {
            throw new IllegalArgumentException("not a valid email address");
        }

        if (!isValidEmailAddress(email)) {
            throw new IllegalArgumentException("not a valid email address:" + email);
        }

        this.email = email;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("{\"email\":\"%s\"}", getEmail());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmailType emailType = (EmailType) o;

        return getEmail().equals(emailType.getEmail());
    }

    @Override
    public int hashCode() {
        return getEmail().hashCode();
    }

    private static boolean isValidEmailAddress(String email) {
        return email.matches(EMAIL_PATTERN);
    }

    /**
     * @see <a href="http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/">E-Mail-Validation</a>
     */
    private static final String EMAIL_PATTERN =
            "^[\\w!#$%&\u2019*+/=?`{|}~^-]+(?:\\.[\\w!#$%&\u2019*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    private String email;
}
