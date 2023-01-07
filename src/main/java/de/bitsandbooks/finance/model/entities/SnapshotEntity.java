package de.bitsandbooks.finance.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
@Table
public class SnapshotEntity {

  @JsonIgnore
  @Id
  @GeneratedValue(generator = "uuid-generator")
  @GenericGenerator(
    name = "uuid-generator",
    strategy = "de.bitsandbooks.finance.repositories.UUIDIdentifier"
  )
  private String id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @NotNull
  private OffsetDateTime snapshotTimestamp;

  private BigDecimal currencyValue;

  private BigDecimal bondsValue;

  private BigDecimal stocksValue;

  private BigDecimal kryptoValue;

  private BigDecimal realEstateValue;

  private BigDecimal resourceValue;

  @Transient private BigDecimal total;

  @NotEmpty private String currency;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private UserAccountEntity userAccount;
}
