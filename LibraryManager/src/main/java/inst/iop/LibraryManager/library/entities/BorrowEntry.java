package inst.iop.LibraryManager.library.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.library.dtos.BorrowEntryDto;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "borrow_entries")
public class BorrowEntry {
  @Id
  @SequenceGenerator(
      name = "borrow_id_sequence",
      sequenceName = "borrow_id_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "borrow_id_sequence"
  )
  private long id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "borrower", referencedColumnName = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "book", referencedColumnName = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private Book book;

  private LocalDate borrowDate;

  private LocalDate returnDate;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private BorrowStatus status = BorrowStatus.Requested;

  public BorrowEntryDto mapToDto() {
    return BorrowEntryDto.builder()
        .id(id)
        .userId(user.getId())
        .bookId(book.getId())
        .borrowDate(borrowDate.toString())
        .returnDate(returnDate.toString())
        .borrowStatus(status.name())
        .build();
  }
}
