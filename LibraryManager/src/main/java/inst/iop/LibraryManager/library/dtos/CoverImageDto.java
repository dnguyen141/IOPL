package inst.iop.LibraryManager.library.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoverImageDto {

  private String imageName;

  private String imageContentType;

  private byte[] imageData;
}
