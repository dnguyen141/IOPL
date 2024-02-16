package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/types")
@Validated
public interface BookTypeController {

  @GetMapping("")
  ResponseEntity<Object> listAllBookTypes();

  @GetMapping("/{id}")
  ResponseEntity<Object> getBookTypeById(@PathVariable Long id);

  @PostMapping("/create")
  ResponseEntity<Object> createBookType(@RequestBody CreateUpdateTypeDto request);

  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateBookType(@PathVariable Long id, @RequestBody CreateUpdateTypeDto request);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookType(@PathVariable Long id);
}
