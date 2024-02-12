package inst.iop.LibraryManager;

import inst.iop.LibraryManager.authentication.entities.JwtToken;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.entities.enums.TokenType;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.authentication.services.AuthenticationService;
import inst.iop.LibraryManager.authentication.services.JwtService;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Data
@SpringBootApplication
public class LibraryManagerApplication {

	private final UserRepository userRepository;
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
					.created(LocalDateTime.now())
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
					.created(LocalDateTime.now())
					.build();
			userRepository.save(moderator);
			authenticationService.saveToken(moderator, "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJJT1AiLCJzdWIiOiJhbm90aGVyb" +
					"WFpbEBlbWFpbC5jb20iLCJpYXQiOjE3MDc2ODcyNDgsImV4cCI6MTcwODI5MjA0OH0.L_Crdum-M94xWAHFmCd1cLNQnWYfzql-NQLkddr6uZ8");
		};
	}
}
