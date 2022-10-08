package de.bitsandbooks.finance.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bitsandbooks.finance.model.dtos.PositionType;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
  indexes = {
    @Index(name = "external_identifier_index", columnList = "externalIdentifier", unique = true)
  }
)
public class FinancePositionEntity {

  @JsonIgnore
  @Id
  @GeneratedValue(generator = "uuid-generator")
  @GenericGenerator(
    name = "uuid-generator",
    strategy = "de.bitsandbooks.finance.repositories.UUIDIdentifier"
  )
  private String id;

  private String externalIdentifier;

  @NotEmpty private String identifier;

  @NotEmpty private String name;

  @NotNull private BigDecimal amount;

  @NotNull private String currency;

  @NotNull private PositionType type;

  @Transient private BigDecimal price;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private UserAccountEntity userAccount;
}
