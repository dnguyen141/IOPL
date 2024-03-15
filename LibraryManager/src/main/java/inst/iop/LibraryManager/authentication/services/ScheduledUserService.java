package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ScheduledUserService {

  private final UserRepository userRepository;

  @Scheduled(cron = "0 0 0 */2 * *", zone = "Europe/Berlin")
  public void checkUnconfirmedUser() {
    log.info("Start checking for unconfirmed users...");
    List<User> lateUnconfirmedUsers = userRepository.findAllLateUsers();
    if (lateUnconfirmedUsers.isEmpty()) {
      log.info("No unconfirmed user found");
      return;
    }
    log.info("Unconfirmed users detected. Start deleting...");
    userRepository.deleteAll(lateUnconfirmedUsers);
    log.info("Successfully delete unconfirmed users");
  }
}
