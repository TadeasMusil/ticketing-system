package tadeas_musil.ticketing_system.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.NaturalId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import tadeas_musil.ticketing_system.validation.UniqueResponseName;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CannedResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
   
    @NaturalId(mutable = true)
    @NotBlank(message = "Cannot be empty")
    @UniqueResponseName
    @EqualsAndHashCode.Include
    private String name;

    @NotBlank
    private String content;

}
