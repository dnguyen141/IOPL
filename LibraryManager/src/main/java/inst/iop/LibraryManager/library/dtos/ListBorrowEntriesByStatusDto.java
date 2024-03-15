package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import inst.iop.LibraryManager.library.entities.constraints.PageNumberConstraint;
import inst.iop.LibraryManager.library.entities.constraints.PageSizeConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListBorrowEntriesByStatusDto {

  @BorrowStatusConstraint
  private String status;

  @PageNumberConstraint
  private Integer pageNumber;

  @PageSizeConstraint
  private Integer pageSize;
}
