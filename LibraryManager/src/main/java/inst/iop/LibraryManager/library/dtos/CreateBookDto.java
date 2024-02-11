package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constrains.*;
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
public class CreateBookDto {

  @TitleConstrain
  private String title;

  @AuthorsConstrain
  private String authors;

  @PublisherConstrain
  private String publisher;

  @TypeConstrain
  private String type;

  @FieldConstrain
  private String field;

  @YearConstrain
  private Integer year;

  @EditionConstrain
  private Integer edition;

  @IsbnConstrain
  private String isbn;

  @QuantityConstrain
  private Integer quantity;

  @UrlConstrain
  private String coverUrl;

  @Nullable
  private MultipartFile coverImage;
}
