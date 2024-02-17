package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.PageNumberConstraint;
import inst.iop.LibraryManager.library.entities.constraints.YearConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchBooksDto {

  private String term;

  @YearConstraint
  private Integer beforeYear;

  @YearConstraint
  private Integer afterYear;

  @PageNumberConstraint
  private Integer pageNumber;

  @PageNumberConstraint
  private Integer pageSize;
}
