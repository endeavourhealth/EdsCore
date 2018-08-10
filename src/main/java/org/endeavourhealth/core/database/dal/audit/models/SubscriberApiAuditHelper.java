package org.endeavourhealth.core.database.dal.audit.models;

import org.endeavourhealth.core.database.dal.DalProvider;
import org.endeavourhealth.core.database.dal.audit.SubscriberApiAuditDalI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SubscriberApiAuditHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriberApiAuditHelper.class);

    private static SubscriberApiAuditDalI apiAuditDal = DalProvider.factorySubscriberAuditApiDal();

    public static void save(SubscriberApiAudit audit) {
        try {
            apiAuditDal.saveSubscriberApiAudit(audit);
        } catch (Exception ex) {
            LOG.error("Error saving audit", ex);
        }
    }

    public static void updateAudit(SubscriberApiAudit audit, Response response, boolean includeResponse) {
        int statusCode = response.getStatus();
        audit.setResponseCode(new Integer(statusCode));

        if (includeResponse && response.hasEntity()) {
            String json = (String)response.getEntity();
            audit.setResponseBody(json);
        }
    }

    public static void updateAuditAndSave(SubscriberApiAudit audit, Response response, boolean includeResponse) {
        updateAudit(audit, response, includeResponse);
        save(audit);
    }

    public static SubscriberApiAudit factory(UUID userUuid, HttpServletRequest request, UriInfo uriInfo) {

        SubscriberApiAudit audit = new SubscriberApiAudit();
        audit.setTimestmp(new Date());

        audit.setUserUuid(userUuid);

        String requestPath = uriInfo.getRequestUri().toString();
        audit.setRequestPath(requestPath);

        String requestAddress = request.getRemoteAddr();
        audit.setRemoteAddress(requestAddress);

        List<String> headerTokens = new ArrayList<>();
        java.util.Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();

            //ignore these two headers, as they don't give us anything useful
            if (header.equals("host")
                    || header.equals("authorization")) {
                continue;
            }
            String headerVal = request.getHeader(header);
            headerTokens.add(header + "=" + headerVal);
        }
        String headerStr = String.join(";", headerTokens);
        audit.setRequestHeaders(headerStr);

        return audit;
    }


}
