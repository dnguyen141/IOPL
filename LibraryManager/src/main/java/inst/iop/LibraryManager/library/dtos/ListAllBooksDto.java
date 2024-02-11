package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constrains.PageNumberConstrain;
import inst.iop.LibraryManager.library.entities.constrains.PageSizeConstrain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAllBooksDto {

  @PageNumberConstrain
  private Integer pageNumber = 0;

  @PageSizeConstrain
  private Integer pageSize = 10;
}
