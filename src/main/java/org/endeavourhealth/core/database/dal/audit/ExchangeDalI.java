package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.*;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonDDSOrganisationStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ExchangeDalI {

    void save(Exchange exchange) throws Exception;
    Exchange getExchange(UUID exchangeId) throws Exception;
    List<Exchange> getExchangesByService(UUID serviceId, UUID systemId, int maxRows, Date dateFrom, Date dateTo) throws Exception;
    List<Exchange> getExchangesByService(UUID serviceId, UUID systemId, int maxRows) throws Exception;
    List<UUID> getExchangeIdsForService(UUID serviceId, UUID systemId) throws Exception;

    void save(ExchangeEvent event) throws Exception;
    List<ExchangeEvent> getExchangeEvents(UUID exchangeId) throws Exception;

    void save(ExchangeTransformAudit exchangeTransformAudit) throws Exception;
    ExchangeTransformAudit getMostRecentExchangeTransform(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception;
    List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId) throws Exception;
    List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception;
    ExchangeTransformAudit getLatestExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId) throws Exception;
    ExchangeTransformAudit getExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId, UUID id) throws Exception;
    //List<ExchangeTransformAudit> getAllExchangeTransformAuditsForService(UUID serviceId, UUID systemId) throws Exception;

    void save(ExchangeTransformErrorState errorState) throws Exception;
    void delete(ExchangeTransformErrorState errorState) throws Exception;
    ExchangeTransformErrorState getErrorState(UUID serviceId, UUID systemId) throws Exception;
    List<ExchangeTransformErrorState> getErrorStatesForService(UUID serviceId, UUID systemId) throws Exception;
    List<ExchangeTransformErrorState> getAllErrorStates() throws Exception;

    UUID getFirstExchangeId(UUID serviceId, UUID systemId) throws Exception;

    void save(ExchangeSubscriberTransformAudit subscriberTransformAudit) throws Exception;
    List<ExchangeSubscriberTransformAudit> getSubscriberTransformAudits(UUID exchangeId) throws Exception;
    List<ExchangeSubscriberTransformAudit> getSubscriberTransformAudits(UUID exchangeId, UUID exchangeBatchId) throws Exception;

    void save(LastDataReceived dataReceived) throws Exception;
    List<LastDataReceived> getLastDataReceived() throws Exception;
    List<LastDataReceived> getLastDataReceived(UUID serviceId) throws Exception;

    void save(LastDataProcessed dataProcessed) throws Exception;
    List<LastDataProcessed> getLastDataProcessed() throws Exception;
    List<LastDataProcessed> getLastDataProcessed(UUID serviceId) throws Exception;

    void save(LastDataToSubscriber dataSent) throws Exception;
    List<LastDataToSubscriber> getLastDataToSubscriber() throws Exception;
    List<LastDataToSubscriber> getLastDataToSubscriber(UUID serviceId) throws Exception;
    List<LastDataToSubscriber> getLastDataToSubscriber(String subscriberConfigName) throws Exception;

    void save(ExchangeSubscriberSendAudit subscriberSendAudit) throws Exception;
    List<ExchangeSubscriberSendAudit> getSubscriberSendAudits(UUID exchangeId, UUID batchId, String subscriberConfigName) throws Exception;

    public List<JsonDDSOrganisationStatus> getOrganisationStatus(List<String> odsCodes, String agreementName) throws Exception;
}
