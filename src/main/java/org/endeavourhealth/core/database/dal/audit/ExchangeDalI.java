package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ExchangeDalI {

    void save(Exchange exchange) throws Exception;
    Exchange getExchange(UUID exchangeId) throws Exception;
    List<Exchange> getExchangesByService(UUID serviceId, int maxRows, Date dateFrom, Date dateTo) throws Exception;
    List<Exchange> getExchangesByService(UUID serviceId, int maxRows) throws Exception;
    List<UUID> getExchangeIdsForService(UUID serviceId) throws Exception;

    void save(ExchangeEvent event) throws Exception;
    List<ExchangeEvent> getExchangeEvents(UUID exchangeId) throws Exception;

    void save(ExchangeTransformAudit exchangeTransformAudit) throws Exception;
    ExchangeTransformAudit getMostRecentExchangeTransform(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception;
    List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId) throws Exception;
    List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception;
    ExchangeTransformAudit getExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId, UUID id) throws Exception;
    List<ExchangeTransformAudit> getAllExchangeTransformAuditsForService(UUID serviceId, UUID systemId) throws Exception;

    void save(ExchangeTransformErrorState errorState) throws Exception;
    void delete(ExchangeTransformErrorState errorState) throws Exception;
    ExchangeTransformErrorState getErrorState(UUID serviceId, UUID systemId) throws Exception;
    List<ExchangeTransformErrorState> getAllErrorStates() throws Exception;

    boolean isServiceStarted(UUID serviceId, UUID systemId) throws Exception;

    void save(ExchangeSubscriberTransformAudit subscriberTransformAudit) throws Exception;
    List<ExchangeSubscriberTransformAudit> getSubscriberTransformAudits(UUID exchangeId) throws Exception;
    List<ExchangeSubscriberTransformAudit> getSubscriberTransformAudits(UUID exchangeId, UUID exchangeBatchId) throws Exception;
}
