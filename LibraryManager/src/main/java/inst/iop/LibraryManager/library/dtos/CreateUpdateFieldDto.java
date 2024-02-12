package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constrains.FieldConstrain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateFieldDto {
  
  @FieldConstrain
  private String name;
}
