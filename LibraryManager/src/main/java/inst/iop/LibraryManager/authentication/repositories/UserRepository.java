package inst.iop.LibraryManager.authentication.repositories;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> findUserByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findUserById(Long id);

  @Query("SELECT u FROM User u WHERE u.role = 'USER' ORDER BY u.id")
  Page<User> findAllUsers(Pageable pageable);

  @Query("SELECT u FROM User u WHERE u.role = 'MODERATOR' or u.role = 'USER' ORDER BY u.id")
  Page<User> findAllModeratorsAndUsers(Pageable pageable);

  @Query("SELECT u FROM User u WHERE u.role = 'USER' and u.createdDate < current_date and u.enabled = false")
  List<User> findAllLateUsers();

  @Modifying
  @Query("UPDATE User u " +
      "SET u.password = CASE WHEN (:password is not null) THEN :password ELSE u.password END, " +
      "u.firstName = CASE WHEN (:firstName is not null) THEN :firstName ELSE u.firstName END, " +
      "u.lastName = CASE WHEN (:lastName is not null) THEN :lastName ELSE u.lastName END, " +
      "u.role = CASE WHEN (:role is not null) THEN :role ELSE u.role END " +
      "WHERE u.email = :email")
  void updateUserByEmail(String email, String password, String firstName, String lastName, Role role);

  @Modifying
  @Query("UPDATE User u " +
      "SET u.password = CASE WHEN (:password is not null) THEN :password ELSE u.password END, " +
      "u.firstName = CASE WHEN (:firstName is not null) THEN :firstName ELSE u.firstName END, " +
      "u.lastName = CASE WHEN (:lastName is not null) THEN :lastName ELSE u.lastName END, " +
      "u.role = CASE WHEN (:role is not null) THEN :role ELSE u.role END " +
      "WHERE u.id = :id")
  void updateUserById(Long id, String password, String firstName, String lastName, Role role);

  @Modifying
  @Query("DELETE FROM User u WHERE u.id = :id")
  void deleteUserById(Long id);

  @Modifying
  @Query("DELETE FROM User u WHERE u.email = :email")
  void deleteUserByEmail(String email);
}
