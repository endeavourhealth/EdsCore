package org.endeavourhealth.core.database.dal.audit;

import org.endeavourhealth.core.database.dal.audit.models.ExchangeGeneralError;
//import org.endeavourhealth.dashboardinformation.json.JsonGraphOptions;
//import org.endeavourhealth.dashboardinformation.json.JsonGraphResults;

import java.util.List;
import java.util.UUID;

public interface ExchangeGeneralErrorDalI {

    public void save(UUID exchangeId, String errorMessage) throws Exception;
    public List<ExchangeGeneralError> getGeneralErrors() throws Exception;
    public void deleteExchangeGeneralError(UUID exchangeId) throws Exception;
//    public List<JsonGraphResults> getGraphData(JsonGraphOptions options) throws Exception;
}
