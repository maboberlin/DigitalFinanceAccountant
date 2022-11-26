package de.bitsandbooks.finance.model.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSnapshotsDto {

  private Map<String, List<SnapshotEntity>> snapshotMap;
}
