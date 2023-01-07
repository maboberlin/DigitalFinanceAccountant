package de.bitsandbooks.finance.model.dtos;

import de.bitsandbooks.finance.model.entities.SnapshotEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SnapshotListDto {
  String accountName;
  List<SnapshotEntity> snapshots;
}
