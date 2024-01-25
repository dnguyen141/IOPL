package inst.iop.LibraryManager.authentication.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @SequenceGenerator(
      name = "user_id_sequence",
      sequenceName = "user_id_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "user_id_sequence"
  )
  private long id;

  @Column(unique = true)
  private String email;

  private String password;

  private String firstName;

  private String lastName;

  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  @JsonIgnore
  @OneToMany(mappedBy = "user")
  private Set<BorrowEntry> borrowEntries;

  @JsonIgnore
  @OneToMany(mappedBy = "user")
  private List<JwtToken> tokens;

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @JsonIgnore
  @Override
  public String getUsername() {
    return email;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
