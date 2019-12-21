package aiApp.applicationcore.facade;

import aiApp.applicationcore.Application;
import aiApp.applicationcore.appointmentcomponent.*;
import aiApp.applicationcore.employeecomponent.EmailType;
import aiApp.applicationcore.employeecomponent.Employee;
import aiApp.applicationcore.employeecomponent.EmployeeRepository;
import aiApp.applicationcore.employeecomponent.InvalidEmployeeNameException;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.*;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationFacadeControllerTest {
    @Before
    public void setUp() {
        try {
            employeeRepository.deleteAll();

            appointmentRepository.deleteAll();

            employee1 = new Employee("Peter", "Schmidt", new EmailType("peter.schmidt@mail.com"));
            employee2 = new Employee("Hans", "Wurst", null);
            employee3 = new Employee("firstName", "lastName", null);

            employeeRepository.save(Arrays.asList(employee1, employee2, employee3));

            final int beginYear = 2017;
            final int beginMonth = TimePoint.APRIL;
            final int beginDay = 16;
            final int beginHour = 19;
            final int beginMinute = 0;
            final int beginSecond = 0;

            TimePoint begin = new TimePoint(beginYear, beginMonth, beginDay, beginHour, beginMinute, beginSecond);

            final int endYear = 2017;
            final int endMonth = TimePoint.APRIL;
            final int endDay = 16;
            final int endHour = 20;
            final int endMinute = 0;
            final int endSecond = 0;

            TimePoint end = new TimePoint(endYear, endMonth, endDay, endHour, endMinute, endSecond);

            TimeSpan timeSpan = new TimeSpan(begin, end);

            appointment = new Appointment(timeSpan);

            appointment.addEmployees(Arrays.asList(employee1, employee2, employee3));

            appointmentRepository.save(appointment);
        } catch (InvalidEmployeeNameException | InvalidDateException | InvalidTimePointException
                 | InvalidTimeSpanException | InvalidWeekException e) {
            e.printStackTrace();
        }

        RestAssured.port = port;
    }

    // GET /appointment
    @Test
    public void canGetAllAppointments() {
        final String path = "/appointment";

        when().
                get(path).
        then().
                statusCode(HttpStatus.ACCEPTED.value());

        Response response = get(path);

        checkAppointmentGottenWithGet(response);
    }

    // GET /appointment
    @Test
    public void canGetAllAppointmentsOfWeek() {
        // note that in America the week begins on Sunday.
        given().
                queryParam("week", appointment.getStartWeek()).
        when().
                get("/appointment").
        then().
                statusCode(HttpStatus.ACCEPTED.value());

        Response response = get(String.format("/appointment?week=%d", appointment.getStartWeek()));

        checkAppointmentGottenWithGet(response);
    }

    // GET /appointment
    @Test
    public void canGetPreconditionFailureForInvalidWeek() {
        final int invalidWeeks[] = new int[] {
            -1, 0, 53,
            -2, -999, 999,
            5000, 0xFFFFFFFF, 0x7FFFFFFF
        };

        for (int invalidWeek : invalidWeeks) {
            given().
                    queryParam("week", invalidWeek).
            when().
                    get("/appointment").
            then().
                    statusCode(HttpStatus.PRECONDITION_FAILED.value());
        }
    }

    // POST /appointment
    @Test
    public void canCreateAppointment() {
        try {
            final String bodyToSendStr = String.format("TimeSpan{%s%s}",
                    "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                    "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}");

            TimeSpan timeSpan = TimeSpan.fromString(bodyToSendStr);
            Appointment appointmentSent = new Appointment(timeSpan);
            Integer expectedId = appointment.getId() + 1;

            given().
                    contentType(ContentType.TEXT).
                    body(bodyToSendStr).
            expect().
                    statusCode(HttpStatus.CREATED.value()).
                    body("id", is(expectedId)).
                    body("startWeek", is(appointmentSent.getStartWeek())).
                    body("attendees", is(appointmentSent.getAttendees())).
            when().
                    post("/appointment");

            RequestSpecification requestSpecification = given().contentType(ContentType.TEXT).body(bodyToSendStr);

            Response response = requestSpecification.when().post("/appointment");

            JSONObject responseJsonObject = new JSONObject(response.asString());

            assertEquals(appointmentSent.getTimeSpan().toString(), responseJsonObject.getString("timeSpan"));
        } catch (ArgumentNotValidException | InvalidDateException | InvalidTimePointException
                 | JSONException | InvalidTimeSpanException | InvalidWeekException e) {
            assertEquals("An exception occurred!", "Exception in canCreateAppointment!");
        }
    }

    // POST /appointment
    @Test
    public void canGetPreconditionFailureForGarbageTimeSpanString() {
        final int preconditionFailure = HttpStatus.PRECONDITION_FAILED.value();
        final String path = "/appointment";

        final String garbage[] = new String[] {
                "thoanhustns,huc", "RC57G1", "*][{(![{(]",
                "-", "~", "@", "^", "<", "'", "\"",
                String.format("{%s%s}",
                        "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}"),
                String.format("TimeSpan{{%s%s}",
                        "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                        "TiePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month4, day=15, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0"),
                String.format("TimeSpan{%s%s",
                        "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}"),
                String.format("TimeSpan{%s}",
                        "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}"),
                String.format("TimeSpan{%s%s}",
                        "TimePoint{year=2017, month=4, hour=16, minute=0, second=0}",
                        "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}")
        };

        for (String invalidBody : garbage) {
            given().
                    contentType(ContentType.TEXT).
                    body(invalidBody).
            expect().
                    statusCode(preconditionFailure).
            when().
                    post(path);
        }
    }

    // POST /appointment
    @Test
    public void canGetPreconditionFailureForLogicallyInvalidTimeSpanString() {
        final String bodyString = String.format("TimeSpan{%s%s}",
                "TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}",
                "TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}");

        given().
                contentType(ContentType.TEXT).
                body(bodyString).
        expect().
                statusCode(HttpStatus.PRECONDITION_FAILED.value()).
        when().
                post("/appointment");
    }

    // DELETE /appointment/{id}
    @Test
    public void canDeleteAppointment() {
        when().
                delete("/appointment/{id}", appointment.getId()).
        then().
               statusCode(HttpStatus.ACCEPTED.value());
    }

    // DELETE /appointment/{id}
    @Test
    public void canGetNotFoundErrorIfDeletingNonExistentAppointment() {
        final Integer ids[] = new Integer[] {
                -1, 0, appointment.getId() + 1,
                0x7FFFFFFF
        };

        for (Integer id : ids) {
            when().
                    delete("/appointment/{id}", id).
            then().
                    statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // PUT /appointment/{id}
    @Test
    public void canAddEmployeeToAppointment() {
        try {
            final String appointmentPath = "/appointment/{id}";
            final Integer appointmentId = appointment.getId();

            RequestSpecification employeeSentRequestSpecification = given().contentType(ContentType.TEXT).body("Test Name");
            Response employeeResponse = employeeSentRequestSpecification.when().post("/employee");
            JSONObject employeeSentJson = new JSONObject(employeeResponse.asString());
            int employeeSentId = employeeSentJson.getInt("id");
            JSONArray employeeSentIdsJsonArray = new JSONArray();
            employeeSentIdsJsonArray.put(employeeSentId);

            RequestSpecification putRequestSpecification = given().contentType(ContentType.JSON)
                    .body(employeeSentIdsJsonArray.toString());

            Response putResponse = putRequestSpecification.when().put(appointmentPath, appointmentId);

            assertEquals(HttpStatus.ACCEPTED.value(), putResponse.getStatusCode());

            JSONObject appointmentJson = new JSONObject(putResponse.body().asString());
            JSONArray attendees = appointmentJson.getJSONArray("attendees");

            JSONObject employeeReceived = attendees.getJSONObject(attendees.length() - 1);

            assertEquals(employeeSentJson.toString(), employeeReceived.toString());

            given().
                    contentType(ContentType.JSON).
                    body(employeeSentIdsJsonArray.toString()).
            expect().
                    statusCode(HttpStatus.NO_CONTENT.value()).
            when().
                    put(appointmentPath, appointmentId);

            given().
                    contentType(ContentType.JSON).
            expect().
                    statusCode(HttpStatus.NO_CONTENT.value()).
            when().
                    put(appointmentPath, appointmentId);

            given().
                    contentType(ContentType.JSON).
                    body("").
            expect().
                    statusCode(HttpStatus.NO_CONTENT.value()).
            when().
                    put(appointmentPath, appointmentId);

            given().
                    contentType(ContentType.JSON).
                    body("[]").
            expect().
                    statusCode(HttpStatus.NO_CONTENT.value()).
            when().
                    put(appointmentPath, appointmentId);

        } catch (JSONException e) {
            assertEquals("Caught an exception:", "message: " + e.getMessage());
        }
    }

    // PUT /appointment/{id}
    @Test
    public void canGetErrorIfInvalidPutAppointment() {
        final String appointmentPath = "/appointment/{id}";

        final Integer invalidAppointmentIds[] = new Integer[] {
                -1, 0, appointment.getId() + 1,
                500, 9999, 0x7FFFFFFF
        };

        for (Integer invalidId : invalidAppointmentIds) {
            given().
                    contentType(ContentType.JSON).
                    body(String.format("[%d]", employee1.getId())).
            expect().
                    statusCode(HttpStatus.NOT_FOUND.value()).
            when().
                    put(appointmentPath, invalidId);
        }

        final Integer invalidEmployeeIds[] = new Integer[] {
                -1, 0, employee3.getId() + 1, 500, 9999, 247034076,
                0x7FFFFFFF
        };

        for (Integer invalidId : invalidEmployeeIds) {
            given().
                    contentType(ContentType.JSON).
                    body(String.format("[%d]", invalidId)).
            expect().
                    statusCode(HttpStatus.PRECONDITION_FAILED.value()).
            when().
                    put(appointmentPath, appointment.getId());
        }
    }

    // GET /employee
    @Test
    public void canGetAllEmployees() {
        try {
            final String path = "/employee";

            when().
                    get(path).
            then().
                    statusCode(HttpStatus.ACCEPTED.value());

            Response response = get(path);
            JSONArray responseJsonArray = new JSONArray(response.asString());
            List<Employee> employees = Arrays.asList(employee1, employee2, employee3);

            assertEquals(employees.size(), responseJsonArray.length());

            for (int i = 0; i < responseJsonArray.length(); ++i) {
                assertEquals(employees.get(i).toString(), responseJsonArray.getJSONObject(i).toString());
            }
        } catch (JSONException e) {
            assertEquals("An error occurred", "Message: " + e.getMessage());
        }
    }

    // POST /employee
    @Test
    public void canCreateNewEmployee() {
        given().
                contentType(ContentType.TEXT).
                body("Test Name").
        expect().
                statusCode(HttpStatus.CREATED.value()).
                body("id", is(employee3.getId() + 1)).
                body("firstName", is("Test")).
                body("lastName", is("Name")).
                body("name", is("Test Name")).
                body("email", is(nullValue())).
        when().
               post("/employee");
    }

    // POST /employee
    @Test
    public void canGetErrorForInvalidEmployeeName() {
        final String path = "/employee";

        final String invalidNames[] = new String[]{
                "invalid", "", " ", "Peter Schm1dt", "{(}={", "7 of 9"
        };

        for (String invalidName : invalidNames) {
            given().
                    contentType(ContentType.TEXT).
                    body(invalidName).
            expect().
                    statusCode(HttpStatus.PRECONDITION_FAILED.value()).
            when().
                    post(path);
        }

        given().
                contentType(ContentType.TEXT).
        expect().
                statusCode(HttpStatus.PRECONDITION_FAILED.value()).
        when().
                post(path);
    }

    // DELETE /employee/{id}
    @Test
    public void canDeleteEmployees() {
        List<Employee> employees = Arrays.asList(employee1, employee2, employee3);

        for (Employee employee : employees) {
            when().
                    delete("/employee/{id}", employee.getId()).
            then().
                    statusCode(HttpStatus.ACCEPTED.value());
        }
    }

    // DELETE /employee/{id}
    @Test
    public void canNotDeleteNonExistentEmployees() {
        Integer invalidIds[] = new Integer[] {
                -1, 0, employee3.getId() + 1, 0x7FFFFFFF
        };

        for (Integer id : invalidIds) {
            when().
                    delete("/employee/{id}", id).
            then().
                    statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // GET /appointment
    private void checkAppointmentGottenWithGet(Response response) {
        String bodyString = response.body().asString();

        try {
            JSONArray appointmentsJsonArray = new JSONArray(bodyString);

            final int expectedAmountOfAppointments = 1;
            assertEquals(expectedAmountOfAppointments, appointmentsJsonArray.length());

            final int appointmentIdx = 0;
            JSONObject appointmentJsonObject = appointmentsJsonArray.getJSONObject(appointmentIdx);

            assertEquals(appointment.toString(), appointmentJsonObject.toString());
        } catch (JSONException e) {
            assertEquals("JSONExceptionWasThrown", e.getMessage());
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;

    private Appointment appointment;
}
