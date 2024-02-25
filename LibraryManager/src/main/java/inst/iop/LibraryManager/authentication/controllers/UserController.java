package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@Tag(name = "user-controller")
@Validated
public interface UserController {

  @Operation(summary = "Get current users. Only for logged in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get user information",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get user",
          content = @Content)
  })
  @GetMapping("/current")
  ResponseEntity<Object> getCurrentUser();

  @Operation(summary = "Get user information by id. Only for admins and moderators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get user information",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get user",
          content = @Content)
  })
  @GetMapping("/{id}")
  ResponseEntity<Object> getUserById(@Parameter(description = "id of the user") @PathVariable Long id);

  @Operation(summary = "List all users and their information. Only for admins and moderators")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully list users information",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to list users",
          content = @Content)
  })
  @GetMapping("")
  ResponseEntity<Object> listAllUsers(@Parameter(description = "Page number")
                                      @RequestParam(defaultValue = "0") Integer pageNumber,
                                      @Parameter(description = "Number of entries in the page")
                                      @RequestParam(defaultValue = "20") Integer pageSize);

  @Operation(summary = "Create new user. Only for admins and moderators and the new user must have less authorities " +
      "than the user that creates it.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully create new user",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to create user",
          content = @Content)
  })
  @PostMapping("/create")
  ResponseEntity<Object> createUser(@Parameter(description = "New user's information")
                                    @RequestBody RegisterDto request);

  @Operation(summary = "Update other user's profile. Only for admins and moderators and it's impossible for an user " +
      "to change information of an other user that has more privileges than them.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Successfully update user's information",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to update user's information",
          content = @Content)
  })
  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateOtherUserProfile(@Parameter(description = "id of the user whose information needs to be " +
      "changed") @PathVariable Long id,
                                                @Parameter(description = "updated information")
                                                @RequestBody ChangeUserDetailsDto request);

  @Operation(summary = "Update current user's profile. Only for logged in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Successfully update current user's information",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to update current user's information",
          content = @Content)
  })
  @PutMapping("/edit")
  ResponseEntity<Object> updateUserProfile(@Parameter(description = "updated information")
                                           @RequestBody ChangeDetailsDto request);

  @Operation(summary = "Delete other user by their id. Only for admins and moderators." +
      "User can't delete accounts with higher privileges")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully delete user",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to delete user",
          content = @Content)
  })
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteUserById(@Parameter(description = "id of the user that will be deleted")
                                        @PathVariable Long id);

  @Operation(summary = "Delete current user. Only for logged in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully delete current user",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to delete current user",
          content = @Content)
  })
  @DeleteMapping("/delete")
  ResponseEntity<Object> deleteUser();
}