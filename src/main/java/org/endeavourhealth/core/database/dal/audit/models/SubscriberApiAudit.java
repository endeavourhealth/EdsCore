package org.endeavourhealth.core.database.dal.audit.models;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SubscriberApiAudit {

    private Date timestmp;
    private UUID userUuid;
    private String remoteAddress;
    private String requestPath;
    private String requestHeaders;
    private Integer responseCode;
    private String responseBody;
    private Long durationMs;

    public SubscriberApiAudit() {}

    public Date getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(Date timestmp) {
        this.timestmp = timestmp;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public void updateAudit(Response response) {
        int statusCode = response.getStatus();
        setResponseCode(new Integer(statusCode));

        if (response.hasEntity()) {
            String json = (String)response.getEntity();
            setResponseBody(json);
        }
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
