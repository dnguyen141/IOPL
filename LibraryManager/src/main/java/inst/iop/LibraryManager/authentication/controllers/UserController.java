package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/user")
public interface UserController {

  @GetMapping("")
  ResponseEntity<Object> getCurrentUser();

  @GetMapping("/{id}")
  ResponseEntity<Object> getUserById(@PathVariable Long id);

  @GetMapping("/list")
  ResponseEntity<Object> listAllUsers();

  @PostMapping("/create")
  ResponseEntity<Object> createUser(@Valid RegisterDto request, BindingResult bindingResult);

  @PutMapping("/edit-user")
  ResponseEntity<Object> updateOtherUserProfile(@Valid ChangeUserDetailsDto request, BindingResult bindingResult);

  @PutMapping("/edit")
  ResponseEntity<Object> updateUserProfile(@Valid ChangeDetailsDto request, BindingResult bindingResult);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteUserById(@PathVariable Long id);

  @DeleteMapping("/delete")
  ResponseEntity<Object> deleteUser();
}