package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constrains.BorrowStatusConstrain;
import inst.iop.LibraryManager.library.entities.constrains.PageNumberConstrain;
import inst.iop.LibraryManager.library.entities.constrains.PageSizeConstrain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListBorrowEntriesDto {

  @BorrowStatusConstrain
  private String status;

  @PageNumberConstrain
  private Integer pageNumber = 0;

  @PageSizeConstrain
  private Integer pageSize = 10;
}
