package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/book-type")
public interface BookTypeController {

  @GetMapping("")
  ResponseEntity<Object> listAllBookTypes();

  @GetMapping("/{id}")
  ResponseEntity<Object> getBookTypeById(@PathVariable Long id);

  @PostMapping("/create")
  ResponseEntity<Object> createBookType(@RequestBody CreateUpdateTypeDto request, BindingResult bindingResult);

  @PutMapping("/{id}/edit")
  ResponseEntity<Object> editBookType(@PathVariable Long id, @RequestBody CreateUpdateTypeDto request,
                                      BindingResult bindingResult);

  @DeleteMapping("/{id}/delete")
  ResponseEntity<Object> deleteBookType(@PathVariable Long id);
}
