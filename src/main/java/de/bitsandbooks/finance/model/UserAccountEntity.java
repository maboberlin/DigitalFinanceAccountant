package de.bitsandbooks.finance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
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
    @UniqueConstraint(columnNames = {"mailAddress", "accountIdentifier"}),
    @UniqueConstraint(columnNames = {"externalIdentifier"})
  }
)
public class UserAccountEntity {

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

  @NotEmpty private String accountIdentifier;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FinancePositionEntity> financePositionEntityList = new ArrayList<>();

  @PrePersist
  private void setExternalIdentifier() {
    this.setExternalIdentifier(UUID.randomUUID().toString());
  }
}
