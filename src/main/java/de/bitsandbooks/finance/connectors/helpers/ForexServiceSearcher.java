package de.bitsandbooks.finance.connectors.helpers;

import de.bitsandbooks.finance.connectors.ForexService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForexServiceSearcher {

  @NonNull private final List<ForexService> forexServiceList;

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public ForexService getBestForexService() {
    return forexServiceList.stream().findFirst().get();
  }
}
