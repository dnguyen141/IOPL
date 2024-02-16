package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookDto {

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
