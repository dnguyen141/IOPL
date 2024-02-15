package inst.iop.LibraryManager.authentication.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"borrowEntries", "tokens", "username", "confirmationCode",  "authorities", "accountNonExpired",
    "credentialsNonExpired", "accountNonLocked"})
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

  @OneToMany(mappedBy = "borrower")
  @Transient
  private Set<BorrowEntry> borrowEntries;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
  private List<JwtToken> tokens;

  private boolean enabled;

  private LocalDate created;

  @Nullable
  private String confirmationCode;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
