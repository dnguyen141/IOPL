package inst.iop.LibraryManager.library.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inst.iop.LibraryManager.library.entities.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBookDto {

  @TitleConstraint
  private String title;

  @AuthorsConstraint
  private String authors;

  @PublisherConstraint
  private String publisher;

  @TypeConstraint
  private String type;

  @FieldConstraint
  private String field;

  @YearConstraint
  private Integer year;

  @EditionConstraint
  private Integer edition;

  @IsbnConstraint
  private String isbn;

  @QuantityConstraint
  private Integer quantity;

  @UrlConstraint
  private String coverUrl;
}
