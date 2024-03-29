package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.PageNumberConstraint;
import inst.iop.LibraryManager.library.entities.constraints.PageSizeConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAllBooksDto {

  @PageNumberConstraint
  private Integer pageNumber;

  @PageSizeConstraint
  private Integer pageSize;
}
