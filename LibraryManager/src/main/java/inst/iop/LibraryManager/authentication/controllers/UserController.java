package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@Validated
public interface UserController {

  @GetMapping("")
  ResponseEntity<Object> listAllUsers(@RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "20") Integer pageSize);

  @GetMapping("/current")
  ResponseEntity<Object> getCurrentUser();

  @GetMapping("/{id}")
  ResponseEntity<Object> getUserById(@PathVariable Long id);

  @PostMapping("/create")
  ResponseEntity<Object> createUser(@RequestBody RegisterDto request);

  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateOtherUserProfile(@PathVariable Long id, @RequestBody ChangeUserDetailsDto request);

  @PutMapping("/edit")
  ResponseEntity<Object> updateUserProfile(@RequestBody ChangeDetailsDto request);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteUserById(@PathVariable Long id);

  @DeleteMapping("/delete")
  ResponseEntity<Object> deleteUser();
}