package aiApp.applicationcore.facade;

import aiApp.applicationcore.appointmentcomponent.Appointment;
import aiApp.applicationcore.appointmentcomponent.AppointmentComponentInterface;
import aiApp.applicationcore.appointmentcomponent.AppointmentNotFoundException;
import aiApp.applicationcore.employeecomponent.Employee;
import aiApp.applicationcore.employeecomponent.EmployeeComponentInterface;
import aiApp.applicationcore.appointmentcomponent.FailedToCreateAppointmentException;
import aiApp.applicationcore.employeecomponent.FailedToFireEmployeeException;
import aiApp.applicationcore.employeecomponent.FailedToHireEmployeeException;
import aiApp.applicationcore.appointmentcomponent.InvalidAppointmentIdException;
import aiApp.applicationcore.appointmentcomponent.InvalidWeekException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The REST-API endpoint.
 */
@RestController
class ApplicationFacadeController {
    /**
     * Constructs an ApplicationFacadeController.
     *
     * @param appointmentComponentInterface the AppointmentComponentInterface to use.
     * @param employeeComponentInterface the EmployeeComponentInterface to use.
     */
    @Autowired
    public ApplicationFacadeController(AppointmentComponentInterface appointmentComponentInterface,
                                       EmployeeComponentInterface employeeComponentInterface) {

        this.appointmentComponentInterface = appointmentComponentInterface;
        this.employeeComponentInterface = employeeComponentInterface;
    }

    /**
     * GET /appointment
     * Returns the appointments that begin in the week passed in, or if week is null all appointments.
     *
     * @param week The week for which to get the appointments that begin in that week.
     *        May be null, in that case all appointments will be returned.
     * @return The appointments requested on success with the HTTP status code 202.
     *         If the week passed in is not a valid week error code 412 is returned.
     *         If an internal server error occurred the error code 500 is returned.
     * @apiNote Note that the week must be passed as a request parameter as part of the URL itself.
     */
    @RequestMapping(value = "/appointment", method = RequestMethod.GET)
    public ResponseEntity<?> getAppointmentsOfWeek(@RequestParam(value = "week", required = false) Integer week) {
        final String invalidWeekErrorTxt = "Invalid week.";
        List<Appointment> appointments;

        try {
            appointments = appointmentComponentInterface.getAppointmentsOfWeek(week);

            return new ResponseEntity<>(appointments, HttpStatus.ACCEPTED);
        } catch (AppointmentNotFoundException | InvalidWeekException e) {
            return new ResponseEntity<>(invalidWeekErrorTxt, HttpStatus.PRECONDITION_FAILED);
        } catch (Throwable e) {
            return new ResponseEntity<>(internalServerErrorTxt, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST /appointment
     * Creates a new appointment from a String of the TimeSpan for which to schedule the appointment.
     *
     * @param timeSpanString The time span for which to schedule the appointment.
     *        Must be formatted correctly.
     *        Example of a valid timeSpanString below:
     *        TimeSpan{TimePoint{year=2017, month=4, day=15, hour=16, minute=0, second=0}TimePoint{year=2017, month=4, day=15, hour=17, minute=0, second=0}}
     * @return The appointment created with the HTTP status code 201 on success.
     *         Error code 412 if the time span passed in was not valid.
     *         Error code 500 if an internal server error occurred.
     * @apiNote Note that the timeSpanString must be passed in the RequestBody as text.
     */
    @RequestMapping(value = "/appointment", method = RequestMethod.POST)
    public ResponseEntity<?> createAppointment(@RequestBody String timeSpanString) {
        final String invalidTimeSpanErrorTxt = "Invalid time span.";
        Appointment newAppointment;

        try {
            newAppointment = appointmentComponentInterface.addAppointment(timeSpanString);
            return new ResponseEntity<>(newAppointment, HttpStatus.CREATED);
        } catch (FailedToCreateAppointmentException e) {
            return new ResponseEntity<>(invalidTimeSpanErrorTxt, HttpStatus.PRECONDITION_FAILED);
        } catch (Throwable e) {
            return new ResponseEntity<>(internalServerErrorTxt, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE /appointment/{id}
     * Deletes the appointment with the id given.
     *
     * @param appointmentId The id of the appointment to delete.
     * @return HTTP status code 202 is returned on success.
     *         error code 404 is returned when attempting to delete an appointment that doesn't exist.
     *         Return error code 500 if an internal server error occurs.
     * @apiNote The appointmentId must be passed as a path variable.
     */
    @RequestMapping(value = "/appointment/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAppointment(@PathVariable("id") Integer appointmentId) {
        final String appointmentErrorTxt = "Appointment does not exist.";
        final String appointmentSuccessTxt = "Appointment was deleted.";

        try {
            appointmentComponentInterface.deleteAppointment(appointmentId);
            return new ResponseEntity<>(appointmentSuccessTxt, HttpStatus.ACCEPTED);
        } catch (InvalidAppointmentIdException e) {
            return new ResponseEntity<>(appointmentErrorTxt, HttpStatus.NOT_FOUND);
        } catch (Throwable e) {
            return new ResponseEntity<>(internalServerErrorTxt, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * PUT /appointment/{id}
     * Function to add employees as attendees to an appointment.
     *
     * @param appointmentId The ID of the appointment to add employees as attendees to.
     *        Must be passed as a path variable.
     * @param employeesToAdd An array of integer IDs of the employees to add to the appointment identified by
     *        appointmentId as attendees.
     *        Note that this array must be passed as a JSON array in the request body.
     * @return On success the appointment is returned along with the HTTP status code 202.
     *         If the arguments passed in were accepted but did not modify the appointment the HTTP status code 204 is returned.
     *         This happens if the array of employee IDs parameter is ignored or an empty array is passed in.
     *         This will also happen if all of the employees to be added to the appointment were already registered as
     *         attendees to that appointment.
     *         If the appointment does not exist error code 404 is returned.
     *         If one or more of the employee IDs passed in is invalid error code 412 is returned.
     *         If an internal server error occurred error code 500 is returned.
     */
    @RequestMapping(value = "/appointment/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAppointment(@PathVariable("id") Integer appointmentId, @RequestBody(required = false) List<Integer> employeesToAdd) {
        final String appointmentErrorTxt = "Appointment does not exist.";
        final String employeeIdErrorTxt = "Invalid employee id/s";
        Appointment returnValue;

        try {
            final boolean noContent = (employeesToAdd == null) || employeesToAdd.isEmpty()
                    || appointmentComponentInterface.doesAppointmentHaveEmployees(appointmentId, employeesToAdd);

            // if any of them does not exist.
            if (!employeeComponentInterface.doAllEmployeesExist(employeesToAdd)) {
                return new ResponseEntity<>(employeeIdErrorTxt, HttpStatus.PRECONDITION_FAILED);
            }

            List<Employee> actualEmployees = employeeComponentInterface.getEmployeesById(employeesToAdd);
            returnValue = appointmentComponentInterface.addEmployeesToAppointment(appointmentId, actualEmployees);
            if (noContent) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(returnValue, HttpStatus.ACCEPTED);
            }
        } catch (InvalidAppointmentIdException | AppointmentNotFoundException e) {
            return new ResponseEntity<>(appointmentErrorTxt, HttpStatus.NOT_FOUND);
        } catch (Throwable e) {
            return new ResponseEntity<>(internalServerErrorTxt, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /employee
     * Returns all employees.
     *
     * @return On success returns all employees along with the HTTP status code 202.
     *         If an internal server error occurred error code 500 is returned.
     * @apiNote The array returned may be empty if there are no employees.
     */
    @RequestMapping(value = "/employee", method = RequestMethod.GET)
    public ResponseEntity<?> getAllEmployees() {
        try {
            return new ResponseEntity<>(employeeComponentInterface.getAllEmployees(), HttpStatus.ACCEPTED);
        } catch (Throwable e) {
            return new ResponseEntity<>(internalServerErrorTxt, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST /employee
     * Function to add a new employee.
     *
     * @param name The full name of the employee.
     *        The first name must come first and be followed by a single space character followed by the last name.
     * @return On success the employee is returned along with the HTTP status code 201.
     *         If the employee's name was invalid error code 412 is returned.
     *         If an internal server error occurred error code 500 is returned.
     * @apiNote note that the name must be passed in the request body as text.
     */
    @RequestMapping(value = "/employee", method = RequestMethod.POST)
    public ResponseEntity<?> hireNewEmployee(@RequestBody(required = false) String name) {
        final String failedToHireEmployeeErrorTxt = "Name of employee was invalid.";
        Employee employee;

        try {
            employee = employeeComponentInterface.hireNewEmployee(name);
            return new ResponseEntity<>(employee, HttpStatus.CREATED);
        } catch (FailedToHireEmployeeException e) {
            return new ResponseEntity<>(failedToHireEmployeeErrorTxt, HttpStatus.PRECONDITION_FAILED);
        } catch (Throwable e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE /employee/{id}
     * Deletes an employee.
     *
     * @param employeeId The ID of the employee to delete.
     * @return On success HTTP status code 202 is returned.
     *         If the employee didn't exist error code 404 is returned.
     *         If an internal server error occurred error code 500 is returned.
     */
    @RequestMapping(value = "/employee/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> fireEmployee(@PathVariable("id") Integer employeeId) {
        final String failedToFireEmployeeErrorTxt = "Employee does not exist";

        try {
            employeeComponentInterface.fireEmployee(employeeId);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (FailedToFireEmployeeException e) {
            return new ResponseEntity<>(failedToFireEmployeeErrorTxt, HttpStatus.NOT_FOUND);
        } catch (Throwable e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The string to use when an internal server error has occurred.
     */
    private static final String internalServerErrorTxt = "Internal server error.";

    /**
     * The AppointmentComponentInterface
     */
    private final AppointmentComponentInterface appointmentComponentInterface;

    /**
     * The EmployeeComponentInterface
     */
    private final EmployeeComponentInterface employeeComponentInterface;
}
