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

  @TitleConstraint(isNotNullConstrain = false)
  private String title;

  @AuthorsConstraint(isNotNullConstrain = false)
  private String authors;

  @PublisherConstraint(isNotNullConstrain = false)
  private String publisher;

  @TypeConstraint(isNotNullConstrain = false)
  private String type;

  @FieldConstraint(isNotNullConstrain = false)
  private String field;

  @YearConstraint(isNotNullConstrain = false)
  private Integer year;

  @EditionConstraint(isNotNullConstrain = false)
  private Integer edition;

  @IsbnConstraint(isNotNullConstrain = false)
  private String isbn;

  @QuantityConstraint(isNotNullConstrain = false)
  private Integer quantity;

  @UrlConstraint
  private String coverUrl;

  @Nullable
  private MultipartFile coverImage;
}
