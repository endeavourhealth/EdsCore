package org.endeavourhealth.core.database.cassandra.audit;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cassandra.CassandraConnector;
import org.endeavourhealth.common.cassandra.Repository;
import org.endeavourhealth.core.database.cassandra.audit.accessors.AuditAccessor;
import org.endeavourhealth.core.database.cassandra.audit.models.*;
import org.endeavourhealth.core.database.dal.audit.ExchangeDalI;
import org.endeavourhealth.core.database.dal.audit.models.Exchange;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeEvent;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformAudit;
import org.endeavourhealth.core.database.dal.audit.models.ExchangeTransformErrorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CassandraExchangeRespository extends Repository implements ExchangeDalI {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraExchangeRespository.class);

    public void save(Exchange exchange) throws Exception {

        CassandraExchange dbObj = new CassandraExchange(exchange);

        Mapper<CassandraExchange> mapperExchange = getMappingManager().mapper(CassandraExchange.class);
        mapperExchange.save(dbObj);

        //if the service ID has been set, then write to the other table too
        if (exchange.getServiceId() != null) {
            CassandraExchangeByService dbObj2 = new CassandraExchangeByService(exchange);

            Mapper<CassandraExchangeByService> mapper = getMappingManager().mapper(CassandraExchangeByService.class);
            mapper.save(dbObj2);
        }
    }

    public void save(ExchangeEvent event) {

        CassandraExchangeEvent dbObj = new CassandraExchangeEvent(event);

        Mapper<CassandraExchangeEvent> mapperEvent = getMappingManager().mapper(CassandraExchangeEvent.class);
        mapperEvent.save(dbObj);
    }

    /*public void save(Exchange exchange, ExchangeEvent event) {

        if (exchange != null) {
            //NOTE: the exchanges can be large, so don't use a batch to save it, as it will fail
            Mapper<Exchange> mapperExchange = getMappingManager().mapper(Exchange.class);
            mapperExchange.save(exchange);
        }

        if (event != null) {
            Mapper<ExchangeEvent> mapperEvent = getMappingManager().mapper(ExchangeEvent.class);
            mapperEvent.save(event);
        }
    }*/

    public Exchange getExchange(UUID exchangeId) throws Exception {
        Mapper<CassandraExchange> mapper = getMappingManager().mapper(CassandraExchange.class);
        CassandraExchange result = mapper.get(exchangeId);
        if (result != null) {
            return new Exchange(result);

        } else {
            return null;
        }
    }

    public void save(ExchangeTransformAudit exchangeTransformAudit) {

        CassandraExchangeTransformAudit dbObj = new CassandraExchangeTransformAudit(exchangeTransformAudit);

        Mapper<CassandraExchangeTransformAudit> mapper = getMappingManager().mapper(CassandraExchangeTransformAudit.class);
        mapper.save(dbObj);
    }

    public void save(ExchangeTransformErrorState errorState) {

        CassandraExchangeTransformErrorState dbObj = new CassandraExchangeTransformErrorState(errorState);

        Mapper<CassandraExchangeTransformErrorState> mapper = getMappingManager().mapper(CassandraExchangeTransformErrorState.class);
        mapper.save(dbObj);
    }

    public void delete(ExchangeTransformErrorState errorState) {

        CassandraExchangeTransformErrorState dbObj = new CassandraExchangeTransformErrorState(errorState);

        Mapper<CassandraExchangeTransformErrorState> mapper = getMappingManager().mapper(CassandraExchangeTransformErrorState.class);
        mapper.delete(dbObj);
    }

    public ExchangeTransformAudit getMostRecentExchangeTransform(UUID serviceId, UUID systemId, UUID exchangeId) {

        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        Iterator<CassandraExchangeTransformAudit> iterator = accessor.getMostRecentExchangeTransform(serviceId, systemId, exchangeId).iterator();
        if (iterator.hasNext()) {
            CassandraExchangeTransformAudit result = iterator.next();
            return new ExchangeTransformAudit(result);
        } else {
            return null;
        }
    }

    public List<CassandraExchangeTransformAudit> getAllExchangeTransform(UUID serviceId, UUID systemId, UUID exchangeId) {

        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        return Lists.newArrayList(accessor.getAllExchangeTransform(serviceId, systemId, exchangeId));
    }

    public ExchangeTransformErrorState getErrorState(UUID serviceId, UUID systemId) {

        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        Iterator<CassandraExchangeTransformErrorState> iterator = accessor.getErrorState(serviceId, systemId).iterator();
        if (iterator.hasNext()) {
            CassandraExchangeTransformErrorState result = iterator.next();
            return new ExchangeTransformErrorState(result);
        } else {
            return null;
        }
    }

    public List<ExchangeTransformErrorState> getAllErrorStates() {

        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        return Lists.newArrayList(accessor.getAllErrorStates())
                .stream()
                .map(T -> new ExchangeTransformErrorState(T))
                .collect(Collectors.toList());
    }

    public boolean isServiceStarted(UUID serviceId, UUID systemId) {

        //find the FIRST exchange we received for the parameters
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        Iterator<CassandraExchangeTransformAudit> iterator = accessor.getFirstExchangeTransformAudit(serviceId, systemId).iterator();

        //if we've never transformed an exchange for this service/system before, then it's definitely not started
        if (!iterator.hasNext()) {
            return false;
        }

        CassandraExchangeTransformAudit firstExchangeAudit = iterator.next();

        //if the first exchange subscriber has been deleted (i.e. the cassandra was deleted from the EHR DB), we need
        //to find the first non-deleted one to see if the cassandra has been re-played
        if (firstExchangeAudit.getDeleted() != null) {
            List<CassandraExchangeTransformAudit> audits = Lists.newArrayList(accessor.getAllExchangeTransformAudits(serviceId, systemId));
            for (CassandraExchangeTransformAudit audit: audits) {
                if (audit.getDeleted() == null) {
                    firstExchangeAudit = audit;
                    break;
                }
            }

            //if we've not got a non-deleted one, then we've not restarted the service
            if (firstExchangeAudit.getDeleted() != null) {
                return false;
            }
        }

        //if we have processed an exchange for the service/system, then make sure it was processed ok
        if (firstExchangeAudit.getErrorXml() == null) {
            return true;
        }

        //if it wasn't processed ok, then make sure there was a subsequent audit of that same exchange being processed ok
        iterator = accessor.getMostRecentExchangeTransform(serviceId, systemId, firstExchangeAudit.getExchangeId()).iterator();
        CassandraExchangeTransformAudit subsequentExchangeAudit = iterator.next();
        if (subsequentExchangeAudit.getErrorXml() == null) {
            return true;
        }

        //if the first exchange for our service/system was never processed OK, we've not properly started receiving for this service
        return false;
    }

    public List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId) {
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        return Lists.newArrayList(accessor.getAllExchangeTransformAudits(serviceId, systemId))
                .stream()
                .map(T -> new ExchangeTransformAudit(T))
                .collect(Collectors.toList());
    }

    public List<ExchangeTransformAudit> getAllExchangeTransformAudits(UUID serviceId, UUID systemId, UUID exchangeId) {
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        return Lists.newArrayList(accessor.getAllExchangeTransformAudits(serviceId, systemId, exchangeId))
                .stream()
                .map(T -> new ExchangeTransformAudit(T))
                .collect(Collectors.toList());
    }

    /*public List<Exchange> getAllExchanges() {
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        return Lists.newArrayList(accessor.getAllExchanges());
    }*/

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows, Date dateFrom, Date dateTo) throws Exception {
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        List<CassandraExchangeByService> results = Lists.newArrayList(accessor.getExchangesByService(serviceId, maxRows, dateFrom, dateTo));

        //can't use streams as the constructor can throw an exception
        List<Exchange> ret = new ArrayList<>();
        for (CassandraExchangeByService result: results) {
            ret.add(new Exchange(result));
        }
        return ret;
    }

    public List<Exchange> getExchangesByService(UUID serviceId, int maxRows) throws Exception {
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        List<CassandraExchangeByService> results = Lists.newArrayList(accessor.getExchangesByService(serviceId, maxRows));

        //can't use streams as the constructor can throw an exception
        List<Exchange> ret = new ArrayList<>();
        for (CassandraExchangeByService result: results) {
            ret.add(new Exchange(result));
        }
        return ret;
    }

    public List<ExchangeEvent> getExchangeEvents(UUID exchangeId) {
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        return Lists.newArrayList(accessor.getExchangeEvents(exchangeId))
                .stream()
                .map(T -> new ExchangeEvent(T))
                .collect(Collectors.toList());
    }

    public ExchangeTransformAudit getExchangeTransformAudit(UUID serviceId, UUID systemId, UUID exchangeId, UUID id) {
        Mapper<CassandraExchangeTransformAudit> mapper = getMappingManager().mapper(CassandraExchangeTransformAudit.class);
        CassandraExchangeTransformAudit result = mapper.get(serviceId, systemId, exchangeId, id);
        if (result != null) {
            return new ExchangeTransformAudit(result);

        } else {
            return null;
        }
    }

    @Override
    public List<ExchangeTransformAudit> getAllExchangeTransformAuditsForService(UUID serviceId, UUID systemId) throws Exception {

        List<ExchangeTransformAudit> ret = new ArrayList<>();

        Session session = CassandraConnector.getInstance().getSession();

        Statement stmt = new SimpleStatement("SELECT exchange_id, number_batches_created FROM audit.exchange_transform_audit WHERE service_id = " + serviceId + " AND system_id = " + systemId);
        stmt.setFetchSize(100);

        ResultSet rs = session.execute(stmt);
        while (!rs.isExhausted()) {
            Row row = rs.one();
            UUID exchangeId = row.get(0, UUID.class);
            Integer batchesCreated = row.get(1, Integer.class);

            //we only populate these two fields on the audit
            ExchangeTransformAudit obj = new ExchangeTransformAudit();
            obj.setExchangeId(exchangeId);
            obj.setNumberBatchesCreated(batchesCreated);
        }

        return ret;
    }

    public List<UUID> getExchangeIdsForService(UUID serviceId) {
        AuditAccessor accessor = getMappingManager().createAccessor(AuditAccessor.class);
        ResultSet resultSet = accessor.getExchangeIdsForService(serviceId);

        List<UUID> ret = new ArrayList<>();
        while (!resultSet.isExhausted()) {
            Row row = resultSet.one();
            UUID uuid = row.getUUID(0);
            ret.add(uuid);
        }

        //the accessor returns them most recent first, so reverse it so they're most recent last
        return Lists.reverse(ret);
    }
}
