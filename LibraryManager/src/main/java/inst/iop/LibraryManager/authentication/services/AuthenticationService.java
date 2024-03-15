package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.JwtToken;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface AuthenticationService {

  User register(RegisterDto request) throws BadRequestDetailsException;

  Map<String, Object> login(LoginDto request) throws BadRequestDetailsException;

  Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws BadRequestDetailsException;

  JwtToken saveToken(User user, String jwtToken);

  void logout(HttpServletRequest request, HttpServletResponse response);

  void confirmRegistration(String email, String confirmationCode) throws BadRequestDetailsException;
}
