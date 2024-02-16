package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ScheduledUserService {

  private static final Logger logger = LoggerFactory.getLogger(ScheduledUserService.class);
  private final UserRepository userRepository;

  @Scheduled(cron = "0 1 0 */2 * *", zone = "Europe/Berlin")
  public void checkUnconfirmedUser() {
    logger.info("Start checking for unconfirmed users...");
    List<User> lateUnconfirmedUsers = userRepository.findAllLateUsers();
    if (lateUnconfirmedUsers.isEmpty()) {
      logger.info("No late unconfirmed user found");
      return;
    }
    logger.info("Unconfirmed users detected. Start deleting...");
    userRepository.deleteAll(lateUnconfirmedUsers);
    logger.info("Successfully delete unconfirmed users");
  }
}
