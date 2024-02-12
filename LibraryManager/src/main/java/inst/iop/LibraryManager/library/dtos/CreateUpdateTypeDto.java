package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constrains.TypeConstrain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateTypeDto {

  @TypeConstrain
  private String name;
}
