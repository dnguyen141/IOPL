package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import jakarta.validation.constraints.NotNull;
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
public class CreateBorrowEntryDto {

  @NotNull
  private Long userId;

  @NotNull
  private Long bookId;

  @BorrowStatusConstraint
  private String status;

  @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE)
  private LocalDate borrowDate;

  @DateTimeFormat(pattern = "dd-MM-yyyy", iso = DateTimeFormat.ISO.DATE)
  private LocalDate returnDate;
}
