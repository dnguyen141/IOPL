package inst.iop.LibraryManager.library.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inst.iop.LibraryManager.library.entities.constraints.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
@JsonIgnoreProperties({"borrowEntries", "coverImage"})
public class Book {

  @Id
  @SequenceGenerator(
      name = "book_id_sequence",
      sequenceName = "book_id_sequence",
      allocationSize = 1,
      initialValue = 100
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "book_id_sequence"
  )
  private long id;

  @Column(unique = true)
  @TitleConstraint
  private String title;

  @AuthorsConstraint
  private String authors;

  @PublisherConstraint
  private String publisher;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "type", referencedColumnName = "name")
  @JsonIdentityReference(alwaysAsId = true)
  private BookType type;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "field", referencedColumnName = "name")
  @JsonIdentityReference(alwaysAsId = true)
  private BookField field;

  @YearConstraint
  private Integer year;

  @EditionConstraint
  private Integer edition;

  @IsbnConstraint
  private String isbn;

  @InventoryNumberConstraint
  private String inventoryNumber;

  @QuantityConstraint
  private Integer quantity;

  @Nullable
  private String coverImage;

  @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
  @Transient
  private Set<BorrowEntry> borrowEntries;
}
