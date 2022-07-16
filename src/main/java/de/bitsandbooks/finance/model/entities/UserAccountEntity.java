package de.bitsandbooks.finance.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
    @UniqueConstraint(columnNames = {"accountIdentifier"}),
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

  @NotBlank private String accountIdentifier;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private UserEntity user;

  @JsonIgnore
  @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FinancePositionEntity> financePositionEntityList = new ArrayList<>();

  @PrePersist
  private void setExternalIdentifier() {
    this.setExternalIdentifier(UUID.randomUUID().toString());
  }
}
