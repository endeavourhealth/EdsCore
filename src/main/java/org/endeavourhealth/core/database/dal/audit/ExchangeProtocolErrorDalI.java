package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeProtocolError;

import java.util.List;
import java.util.UUID;

public interface ExchangeProtocolErrorDalI {

    public void save(UUID exchangeId) throws Exception;
    public List<ExchangeProtocolError> getProtocolErrors() throws Exception;
    public void deleteExchangeProtocolError(UUID exchangeId) throws Exception;
}
