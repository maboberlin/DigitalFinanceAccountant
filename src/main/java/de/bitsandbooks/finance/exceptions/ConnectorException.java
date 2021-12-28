package de.bitsandbooks.finance.exceptions;

import de.bitsandbooks.finance.connectors.ConnectorType;
import lombok.Getter;

public class ConnectorException extends RuntimeException {

  @Getter private ConnectorType connectorType;

  public ConnectorException(String msg, ConnectorType connectorType) {
    super(msg);
    this.connectorType = connectorType;
  }

  public ConnectorException(String msg, ConnectorType connectorType, Throwable e) {
    super(msg, e);
    this.connectorType = connectorType;
  }

  public ConnectorException(Throwable e) {
    super(e);
  }
}
