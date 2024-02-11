package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constrains.PageNumberConstrain;
import inst.iop.LibraryManager.library.entities.constrains.YearConstrain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchBooksDto {

  private String term = "";

  @YearConstrain
  private Integer beforeYear = 1900;

  @YearConstrain
  private Integer afterYear = Year.now().getValue();

  @PageNumberConstrain
  private Integer pageNumber = 0;

  @PageNumberConstrain
  private Integer pageSize = 10;
}
