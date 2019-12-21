package aiApp.applicationcore.employeecomponent;

import org.jetbrains.annotations.Contract;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * This class is used to represent employees.
 */
@Entity
public class Employee implements Serializable {
    /**
     * magic empty constructor - don't touch.
     */
    @SuppressWarnings("unused")
    public Employee() {

    }

    /**
     * Creates an Employee with an email address.
     *
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @param email The email address of the employee.
     * @throws InvalidEmployeeNameException if the first or last name is null or is empty or either one is nonsense.
     */
    public Employee(String firstName, String lastName, EmailType email) throws InvalidEmployeeNameException {
        if (!areFirstAndLastNameValid(firstName, lastName)) {
            throw new InvalidEmployeeNameException();
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        // this call will set the name field.
        createAndSetName();
    }

    /**
     * Getter if the ID of the employee.
     *
     * @return This employee's ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Getter for the first name of the employee.
     *
     * @return This employee's first name.
     */
    @SuppressWarnings("unused")
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for the last name of the employee.
     *
     * @return This employee's last name.
     */
    @SuppressWarnings("unused")
    public String getLastName() {
        return lastName;
    }

    /**
     * Function to get the full name of an employee.
     * The full name consists of the the first name followed by a single space character followed by the last name.
     *
     * @return The full name of the employee.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the email of the employee.
     *
     * @return The email address of this employee. May be null if the employee has no email address.
     */
    @SuppressWarnings("unused")
    public EmailType getEmail() {
        return email;
    }

    /**
     * Setter for the email of the employee.
     *
     * @param email The new email address of this employee. May be null.
     * @return A reference to this object.
     */
    @SuppressWarnings("unused")
    public Employee setEmail(EmailType email) {
        this.email = email;
        return this;
    }

    /**
     * Determines if this Employee is equal to another.
     *
     * @param other The other Object to compare this Employee too.
     * @return true if the receiver is considered equal to the argument; false otherwise.
     * @apiNote An employee is considered to be equal to another Employee, if their IDs compare equal,
     *          that is they are the very same employee.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Employee employee = (Employee) other;

        return getId() != null ? getId().equals(employee.getId()) : employee.getId() == null;
    }

    /**
     * Gives the caller the hash code of this employee's ID.
     *
     * @return The hash code if this employee's ID.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Makes a String that holds the textual representation of this Employee object, so that it can be printed.
     *
     * @return A String that holds the textual representation if this object.
     */
    @Override
    public String toString() {
        return String.format("{\"firstName\":\"%s\",\"lastName\":\"%s\",\"name\":\"%s\",\"id\":%d,\"email\":%s}",
                             getFirstName(), getLastName(), getName(), getId(), getEmail());
    }

    /**
     * Function to check if the first and last name of an employee are valid.
     *
     * @param firstName The first name of the employee.
     * @param lastName The last name of the employee.
     * @return true if both the first name and the last name of the employee are considered to be valid; false otherwise.
     */
    private static boolean areFirstAndLastNameValid(String firstName, String lastName) {
        return isFirstLastNameStringValid(firstName) && isFirstLastNameStringValid(lastName);
    }

    /**
     * Function to check if a given first name or last name is valid.
     *
     * @param firstLastName The first name or the last name of an employee to check.
     * @return true if the first name or last name passed into the parameter is considered valid; false otherwise.
     */
    @Contract("null -> false")
    private static boolean isFirstLastNameStringValid(String firstLastName) {
        final String invalidCharacters = "0123456789$~&%[{}(=*)+]!#`;:,<.>/?@^\\|-_'\"";

        return firstLastName != null && !areAllCharactersSpaces(firstLastName) && !firstLastName.isEmpty()
               && hasNoCharacterOf(firstLastName, invalidCharacters);
    }

    /**
     * Function to determine whether or not a given String contains a specific character.
     *
     * @param str The String to check.
     * @param ch The character to find.
     * @return true if the String str contains the char ch; false otherwise.
     */
    @Contract(pure = true)
    private static boolean hasChar(@NotNull String str, char ch) {
        final int notFound = -1;

        return str.indexOf(ch) != notFound;
    }

    /**
     * Function to check if a String has any of the characters contained in another string.
     *
     * @param strToSearchIn The string to look for characters in.
     * @param characters The collection of characters to look for.
     * @return true if strToSearchIn contains any of the characters in characters; false otherwise.
     */
    private static boolean hasAnyCharacter(@NotNull String strToSearchIn, @NotNull String characters) {
        for (int i = 0; i < characters.length(); ++i) {
            if (hasChar(strToSearchIn, characters.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Function to check if a String has none of the characters contained in another string.
     *
     * @param strToSearchIn The string to look for characters in.
     * @param characters The collection of characters to look for.
     * @return true if strToSearchIn contains none of the characters in characters; false otherwise.
     */
    private static boolean hasNoCharacterOf(@NotNull String strToSearchIn, @NotNull String characters) {
        return !hasAnyCharacter(strToSearchIn, characters);
    }

    /**
     * Determines if all characters in a string are the space character (' ').
     *
     * @param str The string to check. May not be null.
     * @return true if all the characters in str are ' '; false otherwise.
     *         If the string is empty true is returned.
     */
    private static boolean areAllCharactersSpaces(@NotNull String str) {
        final char space = ' ';

        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) != space) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sets the name field by creating the full name from the first and last name of the employee.
     * @implNote Called in the constructors of this class.
     */
    private void createAndSetName() {
        name = String.format("%s %s", firstName, lastName);
    }

    /**
     * The ID that identifies an employee.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * The first name of the employee.
     */
    private String firstName;

    /**
     * The last name of the employee.
     */
    private String lastName;

    /**
     * The full name of the employee.
     * This shall consist of the first name, followed by a single space character and the last name of the employee.
     */
    private String name;

    /**
     * The email address of the employee.
     * May be null if the employee has no email address.
     */
    private EmailType email;
}
