package inst.iop.LibraryManager.library.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import inst.iop.LibraryManager.library.entities.constraints.TypeConstraint;
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
@Table(name = "book_type")
@JsonIgnoreProperties({"books"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class BookType {

  @Column(unique = true)
  @TypeConstraint
  public String name;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @Transient
  private Set<Book> books;

  @Override
  public String toString() {
    return name;
  }
}
