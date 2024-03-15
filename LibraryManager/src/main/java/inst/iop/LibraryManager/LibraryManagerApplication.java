package inst.iop.LibraryManager;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.authentication.services.AuthenticationService;
import inst.iop.LibraryManager.authentication.services.JwtService;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Data
@SpringBootApplication
public class LibraryManagerApplication {

  private final UserRepository userRepository;

  private final BookRepository bookRepository;

  private final BorrowEntryRepository borrowEntryRepository;

  private final JwtService jwtService;

  private final AuthenticationService authenticationService;

  public static void main(String[] args) {
    SpringApplication.run(LibraryManagerApplication.class, args);
  }

  @Bean
  @Transactional
  public CommandLineRunner addDefaultObjects(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder) {
    return args -> {
      User admin = User.builder()
          .email("admin@iopl.com")
          .password(passwordEncoder.encode("a123456"))
          .firstName("The")
          .lastName("Admin")
          .role(Role.ADMIN)
          .enabled(true)
          .createdDate(LocalDate.of(2024, 1, 1))
          .build();
      userRepository.save(admin);
    };
  }
}
