package inst.iop.LibraryManager.library.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowEntryDto {

  private long id;

  private long userId;

  private long bookId;

  private String borrowStatus;

  private String borrowDate;

  private String returnDate;
}
