package inst.iop.LibraryManager.library.dtos;

import inst.iop.LibraryManager.library.entities.constraints.FieldConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateFieldDto {
  
  @FieldConstraint
  private String name;
}
