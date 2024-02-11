package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.constrains.*;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
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

  @BorrowStatusConstrain
  private String borrowStatus;
}
