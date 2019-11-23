package tadeas_musil.ticketing_system.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tadeas_musil.ticketing_system.entity.User.Registration;
import tadeas_musil.ticketing_system.validation.PasswordMatch;
import tadeas_musil.ticketing_system.validation.UniqueUsername;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
@PasswordMatch(groups = { Registration.class }, first = "password", second = "confirmPassword")
public class User {
    
    public interface Registration {
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Enter email", groups = { Registration.class })
    @Email(message = "Invalid email format", groups = { Registration.class })
    @UniqueUsername(groups = { Registration.class })
    private String username;

    @NotBlank(message = "Enter first name", groups = { Registration.class })
    private String firstName;
    
    @NotBlank(message = "Enter last name", groups = { Registration.class })
    private String lastName;

    @NotBlank(message = "Enter password", groups = { Registration.class })
    @Size(min = 8, message = "Password must have at least 8 characters", groups = { Registration.class })
    private String password;

    @Transient
    private String confirmPassword;
                    
    @ManyToMany(fetch = FetchType.LAZY ,cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "app_user_show",
                joinColumns = @JoinColumn(name = "app_user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    
}