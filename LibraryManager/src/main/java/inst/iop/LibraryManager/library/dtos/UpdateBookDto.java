package inst.iop.LibraryManager.library.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBookDto {

  @TitleConstrain(isNotNullConstrain = false)
  private String title;

  @AuthorsConstrain(isNotNullConstrain = false)
  private String authors;

  @PublisherConstrain(isNotNullConstrain = false)
  private String publisher;

  @TypeConstrain(isNotNullConstrain = false)
  private String type;

  @FieldConstrain(isNotNullConstrain = false)
  private String field;

  @YearConstrain(isNotNullConstrain = false)
  private Integer year;

  @EditionConstrain(isNotNullConstrain = false)
  private Integer edition;

  @IsbnConstrain(isNotNullConstrain = false)
  private String isbn;

  @InventoryNumberConstrain
  private String inventoryNumber;

  @QuantityConstrain(isNotNullConstrain = false)
  private Integer quantity;

  @UrlConstrain
  private String coverUrl;

  @Nullable
  private MultipartFile coverImage;
}
