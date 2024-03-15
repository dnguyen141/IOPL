package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBorrowEntryDto {

  private Long borrowEntryId;

  private Long userId;

  private Long bookId;

  @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE)
  private LocalDate borrowDate;

  @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE)
  private LocalDate returnDate;

  @BorrowStatusConstraint(isNotNullConstrain = false)
  private String status;
}
