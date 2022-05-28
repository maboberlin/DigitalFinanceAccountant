package de.bitsandbooks.finance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private UserAccountEntity user;
}
