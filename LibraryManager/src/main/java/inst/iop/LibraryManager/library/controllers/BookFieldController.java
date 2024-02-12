package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/book-field")
public interface BookFieldController {

  @GetMapping("")
  ResponseEntity<Object> listAllBookFields();

  @GetMapping("/{id}")
  ResponseEntity<Object> getBookFieldById(@PathVariable Long id);

  @PostMapping("/create")
  ResponseEntity<Object> createBookField(@RequestBody CreateUpdateFieldDto request, BindingResult bindingResult);

  @PutMapping("/{id}/edit")
  ResponseEntity<Object> updateBookField(@PathVariable Long id, @RequestBody CreateUpdateFieldDto request,
                                         BindingResult bindingResult);

  @DeleteMapping("/{id}")
  ResponseEntity<Object> deleteBookField(@PathVariable Long id);
}
