package inst.iop.LibraryManager.library.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum BorrowStatus {
  Requested(0),
  Issued(-1),
  Returned(0),
  Overdue(-1),
  Lost(-1);

  private final int state;

  public static Optional<BorrowStatus> getBorrowStatusFromString(String status) {
    if (status != null && !status.trim().isEmpty()) {
      for (BorrowStatus bs : values()) {
        if (bs.name().equalsIgnoreCase(status.trim())) {
          return Optional.of(bs);
        }
      }
    }
    return Optional.empty();
  }
}
