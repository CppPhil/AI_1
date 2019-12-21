package aiApp.applicationcore.appointmentcomponent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * A repository for Appointments.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    /**
     * Function to get all the Appointments that start in the week passed in.
     *
     * @param startWeek The week for which to get all the appointments of.
     * @return A Optional List of all the Appointments that begin in startWeek.
     */
    Optional<List<Appointment>> findByStartWeek(Integer startWeek);
}
