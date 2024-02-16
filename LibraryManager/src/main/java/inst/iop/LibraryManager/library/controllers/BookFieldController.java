package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/fields")
@Validated
public interface BookFieldController {

  @GetMapping("")
  ResponseEntity<Object> listAllBookFields();

  @GetMapping("/{id}")
  ResponseEntity<Object> getBookFieldById(@PathVariable Long id);

  @PostMapping("/create")
  ResponseEntity<Object> createBookField(@RequestBody CreateUpdateFieldDto request);

  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateBookField(@PathVariable Long id, @RequestBody CreateUpdateFieldDto request);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookField(@PathVariable Long id);
}
