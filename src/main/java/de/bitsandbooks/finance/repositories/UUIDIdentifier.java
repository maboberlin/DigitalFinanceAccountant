package de.bitsandbooks.finance.repositories;

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class UUIDIdentifier implements IdentifierGenerator {

  @Override
  public Serializable generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object object)
      throws HibernateException {
    return UUID.randomUUID().toString();
  }
}
