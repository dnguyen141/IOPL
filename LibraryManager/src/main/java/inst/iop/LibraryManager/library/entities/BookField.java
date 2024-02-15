package inst.iop.LibraryManager.library.entities;

import com.fasterxml.jackson.annotation.*;
import inst.iop.LibraryManager.library.entities.constraints.FieldConstraint;
import jakarta.persistence.*;
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
@Table(name = "book_field")
@JsonIgnoreProperties({"books"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class BookField {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(unique = true)
  @FieldConstraint
  public String name;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @Transient
  private Set<Book> books;

  @Override
  public String toString() {
    return name;
  }
}
