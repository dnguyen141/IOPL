package inst.iop.LibraryManager.authentication.repositories;


import inst.iop.LibraryManager.authentication.entities.JwtToken;
import inst.iop.LibraryManager.authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TokenRepository extends JpaRepository<JwtToken, Long> {

  @Query("SELECT t " +
      "FROM JwtToken t INNER JOIN User u ON t.user.id = u.id " +
      "WHERE u.id = :id and (t.expired = false or t.revoked = false)")
  List<JwtToken> findAllValidTokensByUserId(Long id);

  @Query("SELECT t FROM JwtToken t WHERE t.token = :token")
  Optional<JwtToken> findTokenByString(String token);

  @Modifying
  @Query("DELETE FROM JwtToken t WHERE t.user = :user")
  void deleteTokenByUser(User user);
}
