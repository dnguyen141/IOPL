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

  /**
   * The API end-point to get current user information.
   *
   * @return ResponseEntity that contains a message, http response code, a Map contains user information in success case
   * or violations from input or verification in error case
   */
  @GetMapping("/current")
  ResponseEntity<Object> getCurrentUser();

  /**
   * The API end-point for listing a user's information.
   * There are 3 types of user: ADMIN, MODERATOR and USER. ADMINs can see MODERATORs and USERs information,
   * MODERATORs can only see USERs information.
   *
   * @param  id user's id
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @GetMapping("/{id}")
  ResponseEntity<Object> getUserById(@PathVariable Long id);

  /**
   * The API end-point for listing users for admins and moderators.
   * There are 3 types of user: ADMIN, MODERATOR and USER. ADMINs can see MODERATORs and USERs information,
   * MODERATORs can only see USERs information.
   *
   * @param  pageNumber page number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @GetMapping("")
  ResponseEntity<Object> listAllUsers(@RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "20") Integer pageSize);

  /**
   * The API end-point for create new user. It also sends email for user to confirm their identity.
   *
   * @param  request create user request
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @PostMapping("/create")
  ResponseEntity<Object> createUser(@RequestBody RegisterDto request);

  /**
   * The API end-point used by admins and moderators to update other user's profile.
   *
   * @param  request create user request
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateOtherUserProfile(@PathVariable Long id, @RequestBody ChangeUserDetailsDto request);

  /**
   * The API end-point used by users to update their profile.
   *
   * @param  request create user request
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @PutMapping("/edit")
  ResponseEntity<Object> updateUserProfile(@RequestBody ChangeDetailsDto request);

  /**
   * The API end-point used by admins and moderators to delete a user by their ID. Can only delete if the account has
   * less authority.
   *
   * @param  id User ID to delete.
   * @return ResponseEntity with success message.
   */
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteUserById(@PathVariable Long id);

  /**
   * Delete the currently authenticated user.
   *
   * @return ResponseEntity with success message.
   */
  @DeleteMapping("/delete")
  ResponseEntity<Object> deleteUser();
}