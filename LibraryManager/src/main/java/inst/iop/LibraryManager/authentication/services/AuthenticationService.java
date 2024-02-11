package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;

import java.util.*;

public interface AuthenticationService {

  void register(RegisterDto request, BindingResult bindingResult)
      throws BadRequestDetailsException;

  Map<String, Object> login(LoginDto request) throws BadRequestDetailsException;

  Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws BadRequestDetailsException;

  void logout(HttpServletRequest request, HttpServletResponse response);

  void confirmRegister(String email, String confirmationCode);
}
