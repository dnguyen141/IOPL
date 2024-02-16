package inst.iop.LibraryManager;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.authentication.services.AuthenticationService;
import inst.iop.LibraryManager.authentication.services.JwtService;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
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
  public CommandLineRunner addDefaultUsers(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder) {
    return args -> {
      User admin = User.builder()
          .email("mail@email.com")
          .password(passwordEncoder.encode("a123456"))
          .firstName("Dinh")
          .lastName("Nguyen")
          .role(Role.ADMIN)
          .enabled(true)
          .created(LocalDate.now())
          .build();
      userRepository.save(admin);
      authenticationService.saveToken(admin, "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJJT1AiLCJzdWIiOiJtYWlsQGVtYWls" +
          "LmNvbSIsImlhdCI6MTcwNzY4NzI0OCwiZXhwIjoxNzA4MjkyMDQ4fQ.T9bQVlcR_NY0KfNVdY38o1f8kD2KsSF2Fjhpx7dzP4o");

      User moderator = User.builder()
          .email("anothermail@email.com")
          .password(passwordEncoder.encode("a123456"))
          .firstName("Minh")
          .lastName("Tran")
          .role(Role.MODERATOR)
          .enabled(true)
          .created(LocalDate.now())
          .build();
      userRepository.save(moderator);
      authenticationService.saveToken(moderator, "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJJT1AiLCJzdWIiOiJhbm90aGVyb" +
          "WFpbEBlbWFpbC5jb20iLCJpYXQiOjE3MDc2ODcyNDgsImV4cCI6MTcwODI5MjA0OH0.L_Crdum-M94xWAHFmCd1cLNQnWYfzql-NQLkddr6uZ8");

      User user = User.builder()
          .email("mail1@email.com")
          .password(passwordEncoder.encode("a123456"))
          .firstName("Ken")
          .lastName("Pro")
          .role(Role.USER)
          .enabled(true)
          .created(LocalDate.now())
          .build();
      userRepository.save(user);
      authenticationService.saveToken(user, "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJJT1AiLCJzdWIiOiJtYWlsMUBlbWFpb" +
          "C5jb20iLCJpYXQiOjE3MDc5MTY0NDUsImV4cCI6MTcwODUyMTI0NX0.5C2tH2ATVu_XZ-fbL4C_W-BXVkqe14xi5pjmDEi098k");

      Book book = Book.builder()
          .title("Atomistic and Continuum Scale Models for Flexoelectric Nanostructures and Composites")
          .authors("Bo He")
          .publisher("LUH")
          .type(BookType.builder().name("Book").build())
          .field(BookField.builder().name("Physics").build())
          .year(2023)
          .edition(1)
          .isbn("978-3-949403-45-3")
          .inventoryNumber("100 CSST TH 2023")
          .coverImage(null)
          .quantity(2)
          .build();
      bookRepository.save(book);

      BorrowEntry borrowEntry = BorrowEntry.builder()
          .user(user)
          .book(book)
          .borrowDate(LocalDate.of(2024, 2, 14))
          .returnDate(LocalDate.of(2024, 2, 15))
          .status(BorrowStatus.Issued)
          .build();
      borrowEntryRepository.save(borrowEntry);
    };
  }
}
