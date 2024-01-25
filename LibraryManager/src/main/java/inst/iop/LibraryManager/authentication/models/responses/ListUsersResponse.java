package inst.iop.LibraryManager.authentication.models.responses;

import inst.iop.LibraryManager.authentication.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListUsersResponse {
  private List<User> users;
}
