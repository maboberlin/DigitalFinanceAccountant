package de.bitsandbooks.finance.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"mailAddress"}),
    @UniqueConstraint(columnNames = {"externalIdentifier"})
  }
)
public class UserEntity {

  @JsonIgnore
  @Id
  @GeneratedValue(generator = "uuid-generator")
  @GenericGenerator(
    name = "uuid-generator",
    strategy = "de.bitsandbooks.finance.repositories.UUIDIdentifier"
  )
  @EqualsAndHashCode.Include
  private String id;

  private String externalIdentifier;

  @NotEmpty private String foreName;

  @NotEmpty private String surName;

  @Email private String mailAddress;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotEmpty
  private String password;

  @JsonIgnore
  @ElementCollection
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserAccountEntity> userAccountEntityList = new ArrayList<>();

  @PrePersist
  private void setExternalIdentifier() {
    this.setExternalIdentifier(UUID.randomUUID().toString());
  }
}
