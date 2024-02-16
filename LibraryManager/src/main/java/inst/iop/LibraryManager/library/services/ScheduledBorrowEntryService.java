package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
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
public class ScheduledBorrowEntryService {

  private static final Logger logger = LoggerFactory.getLogger(ScheduledBorrowEntryService.class);
  private final BorrowEntryRepository borrowEntryRepository;

  @Scheduled(cron = "1 0 0 * * *", zone = "Europe/Berlin")
  public void checkStatusAllBorrowEntries() {
    logger.info("Start checking for late borrow entries...");
    List<BorrowEntry> lateBorrowEntries = borrowEntryRepository.getLateBorrowEntry();
    if (lateBorrowEntries.isEmpty()) {
      logger.info("No late borrow entries found");
      return;
    }
    logger.info("Late borrow entries detected. Start correcting...");
    lateBorrowEntries.forEach(be -> {
      be.setStatus(BorrowStatus.Overdue);
      borrowEntryRepository.saveAndFlush(be);
    });
    logger.info("Successfully saved late borrow entries");
  }
}
