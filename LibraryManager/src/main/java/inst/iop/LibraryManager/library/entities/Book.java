package inst.iop.LibraryManager.library.entities;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inst.iop.LibraryManager.library.entities.constrains.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
@JsonIgnoreProperties({"borrowEntries"})
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
  @TitleConstrain
  private String title;

  @AuthorsConstrain
  private String authors;

  @PublisherConstrain
  private String publisher;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "type", referencedColumnName = "name")
  @JsonIdentityReference(alwaysAsId = true)
  private BookType type;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "field", referencedColumnName = "name")
  @JsonIdentityReference(alwaysAsId = true)
  private BookField field;

  @YearConstrain
  private Integer year;

  @EditionConstrain
  private Integer edition;

  @IsbnConstrain
  private String isbn;

  @InventoryNumberConstrain(isNotNullConstrain = true)
  private String inventoryNumber;

  @QuantityConstrain
  private Integer quantity;

  private Integer available;

  @UrlConstrain
  private String coverImage;

  @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
  @Transient
  private Set<BorrowEntry> borrowEntries;

  public boolean isIssuable() {
    return available > 0;
  }
}
