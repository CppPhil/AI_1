package aiApp.applicationcore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The application class.
 */
@SpringBootApplication
public class Application {
    /**
     * Some magical init function.
     *
     * @return Some lambda that will be run.
     */
    @Bean
    CommandLineRunner init() {
        return (String... args) -> {

        };
    }

    /**
     * The entry point of the application.
     * Starts the application.
     *
     * @param args The command line arguments to the application that will be handed of to Spring.
     */
    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }
}
