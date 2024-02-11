package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface UserService {

  User findUserById(Long id) throws BadRequestDetailsException;

  User findUserByEmail(String email) throws BadRequestDetailsException;

  void deleteUserById(Long id) throws BadRequestDetailsException;

  List<User> findAllUsers();

  List<User> findAllModerators();

  List<User> findAllAdmins();

  void createUser(RegisterDto request, BindingResult bindingResult) throws BadRequestDetailsException;

  void updateOtherUserByEmail(ChangeUserDetailsDto userDetailsRequest, BindingResult bindingResult)
      throws BadRequestDetailsException;

  void updateOtherUserByEmail(ChangeDetailsDto userDetailsRequest, BindingResult bindingResult)
      throws BadRequestDetailsException;

  void deleteUser();
}
