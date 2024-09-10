package coms309.demo_nhat.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration // The class will define application context
public class StudentConfig {

    @Bean // Define a Bean that Spring will handle its lifecycle.
    // In this case, CommandLineRunner object/ bean runs the "run" method.
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> {
            Student nhat = new Student(
                    "Nhat Le",
                    LocalDate.of(2005, Month.AUGUST, 9),
                    "lan0908@iastate.edu"
            );

            Student bach = new Student(
                    "Bach Nguyen",
                    LocalDate.of(2005, Month.NOVEMBER, 14),
                    "ntbach@iastate.edu"
            );

            repository.saveAll(
                    List.of(nhat, bach)
            );
        };
    }
}
