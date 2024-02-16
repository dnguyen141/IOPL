package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import inst.iop.LibraryManager.library.entities.constraints.PageNumberConstraint;
import inst.iop.LibraryManager.library.entities.constraints.PageSizeConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListBorrowEntriesDto {

  @BorrowStatusConstraint
  private String status;

  @PageNumberConstraint
  private Integer pageNumber = 0;

  @PageSizeConstraint
  private Integer pageSize = 10;
}
