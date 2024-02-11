package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constrains.BorrowStatusConstrain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @BorrowStatusConstrain(isNotNullConstrain = true)
  private String status;

  @NotNull
  private LocalDate returnDate;
}
