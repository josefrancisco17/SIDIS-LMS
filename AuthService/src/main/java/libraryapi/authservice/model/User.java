package libraryapi.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "T_USER")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Getter
	@Setter
	private Long id;

	@Version
	@Getter
	@Setter
	private Long version;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	@Getter
	@Setter
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	@Getter
	@Setter
	private LocalDateTime modifiedAt;

	@CreatedBy
	@Column(nullable = false, updatable = false)
	@Getter
	@Setter
	private String createdBy;

	@LastModifiedBy
	@Column(nullable = false)
	@Setter
	private String modifiedBy;

	@Setter
	@Getter
	private boolean enabled = true;

	@Column(unique = true, updatable = false, nullable = false)
	@Email
	@Getter
	@NotNull
	@NotBlank
	private String username;

	@Column(nullable = false)
	@Getter
	@NotNull
	@NotBlank
	private String password;

	@Getter
	@Setter
	private String fullName;

	@ElementCollection
	@Getter
	private Set<Role> authorities = new HashSet<>();

	public User() {

	}

	public User(final Long id, final String username, final Set<Role> authorities) {
		this.id = id;
		this.username = username;
		this.authorities = authorities;
	}

	public User(final String username, final String password) {
		this.username = username;
		setPassword(password);
	}

	public User(final String username, final String password, final String fullName) {
		this.username = username;
		setPassword(password);
		this.fullName = fullName;
	}

	public User(String username, String password, Set<Role> authorities) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public User(Long id, Long version, LocalDateTime createdAt, LocalDateTime modifiedAt, String createdBy, String modifiedBy, boolean enabled, String username, String password, String fullName, Set<Role> authorities) {
		this.id = id;
		this.version = version;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.enabled = enabled;
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.authorities = authorities;
	}

	public static User newUser(final String username, final String password, final String fullName) {
		final var u = new User(username, password);
		u.setFullName(fullName);
		return u;
	}

	public static User newUser(final String username, final String password, final String fullName, final String role) {
        var u = newUser(username, password, fullName);
		u.addAuthority(new Role(role));
		return u;
	}

	public void setPassword(final String password) {
		this.password = Objects.requireNonNull(password);
	}

	public void addAuthority(final Role r) {
		authorities.add(r);
	}

	@Override
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}

	@Override
	public String toString() {
		return "User{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", authorities=" + authorities +
				", id=" + id +
				", version=" + version +
				", createdAt=" + createdAt +
				", createdBy='" + createdBy + '\'' +
				", modifiedAt=" + modifiedAt +
				", modifiedBy='" + modifiedBy + '\'' +
				", enabled=" + enabled +
				", fullName='" + fullName + '\'' +
				'}';
	}
}