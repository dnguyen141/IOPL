package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ScheduledBorrowEntryService {

  private final BorrowEntryRepository borrowEntryRepository;

  @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Berlin")
  public void checkStatusAllBorrowEntries() {
    log.info("Start checking for late borrow entries...");
    List<BorrowEntry> lateBorrowEntries = borrowEntryRepository.getLateBorrowEntry();
    if (lateBorrowEntries.isEmpty()) {
      log.info("No late borrow entries found");
      return;
    }
    log.info("Late borrow entries detected. Start correcting...");
    lateBorrowEntries.forEach(be -> {
      be.setStatus(BorrowStatus.Overdue);
      borrowEntryRepository.saveAndFlush(be);
    });
    log.info("Successfully saved late borrow entries");
  }
}
