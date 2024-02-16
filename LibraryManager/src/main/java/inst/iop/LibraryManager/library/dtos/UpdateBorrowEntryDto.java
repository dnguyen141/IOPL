package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBorrowEntryDto {

  private Long userId;

  private Long bookId;

  private LocalDate borrowDate;

  private LocalDate returnDate;

  @BorrowStatusConstraint(isNotNullConstrain = false)
  private String borrowStatus;
}
