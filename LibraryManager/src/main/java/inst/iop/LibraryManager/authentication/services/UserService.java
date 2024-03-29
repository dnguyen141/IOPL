package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.UpdateDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.UpdateUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.data.domain.Page;

public interface UserService {

  User findUserById(Long id) throws BadRequestDetailsException;

  User findUserByEmail(String email) throws BadRequestDetailsException;

  Page<User> findAllModeratorsAndUsers(Integer pageNumber, Integer pageSize);

  Page<User> findAllUsers(Integer pageNumber, Integer pageSize);

  User createUser(RegisterDto request) throws BadRequestDetailsException;

  void updateOtherUserById(Long id, UpdateUserDetailsDto request) throws BadRequestDetailsException;

  void updateUserByEmail(UpdateDetailsDto request) throws BadRequestDetailsException;

  void deleteUser();

  void deleteUserById(Long id);
}
