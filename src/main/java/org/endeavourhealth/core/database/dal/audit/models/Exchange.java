    package org.endeavourhealth.core.database.dal.audit.models;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.endeavourhealth.common.cache.ObjectMapperPool;
import org.endeavourhealth.core.database.rdbms.audit.models.RdbmsExchange;

import java.text.SimpleDateFormat;
import java.util.*;

public class Exchange {

    private UUID id = null;
    private Date timestamp = null;
    private Map<String, String> headers = null;
    private UUID serviceId = null;
    private UUID systemId = null;
    private String body = null;
    private Exception exception; //not persisted, but used as a holding variable for logback

    public Exchange() {}

    /*public Exchange(CassandraExchange proxy) throws Exception {
        this.id = proxy.getExchangeId();
        this.timestamp = proxy.getTimestamp();

        String headersJson = proxy.getHeaders();
        this.headers = ObjectMapperPool.getInstance().readValue(headersJson, HashMap.class);
        //this.serviceId = proxy.s; //field not present in this proxy
        this.body = proxy.getBody();
    }

    public Exchange(CassandraExchangeByService proxy) throws Exception {
        this.id = proxy.getExchangeId();
        this.timestamp = proxy.getTimestamp();
        //String headersJson = proxy.getHeaders(); //field not present in proxy
        //this.headers = ObjectMapperPool.getInstance().readValue(headersJson, HashMap.class);
        this.serviceId = proxy.getServiceId();
        //this.body = proxy.getBody(); //field not present in proxy
    }*/

    public Exchange(RdbmsExchange proxy) throws Exception {
        this.id = UUID.fromString(proxy.getId());
        this.timestamp = proxy.getTimestamp();

        String headersJson = proxy.getHeaders();
        this.headers = ObjectMapperPool.getInstance().readValue(headersJson, HashMap.class);

        if (!Strings.isNullOrEmpty(proxy.getServiceId())) {
            this.serviceId = UUID.fromString(proxy.getServiceId());
        }

        if (!Strings.isNullOrEmpty(proxy.getSystemId())) {
            this.systemId = UUID.fromString(proxy.getSystemId());
        }

        this.body = proxy.getBody();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID systemId) {
        this.systemId = systemId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * utility fns to cut down duplicated code all over
     */
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key);
    }

    public UUID getHeaderAsUuid(String key) {
        String s = getHeader(key);
        if (Strings.isNullOrEmpty(s)) {
            return null;
        } else {
            return UUID.fromString(s);
        }
    }

    public void setHeaderAsUuid(String key, UUID uuid) {
        if (uuid == null) {
            setHeader(key, null);
        } else {
            setHeader(key, uuid.toString());
        }
    }

    public String[] getHeaderAsStringArray(String headerKey) throws Exception {
        String json = getHeader(headerKey);
        if (Strings.isNullOrEmpty(json)) {
            return null;
        } else {
            return ObjectMapperPool.getInstance().readValue(json, String[].class);
        }
    }

    public void setHeaderAsStringArray(String key, String[] arr) throws Exception {
        if (arr == null) {
            setHeader(key, null);
        } else {
            String json = ObjectMapperPool.getInstance().writeValueAsString(arr);
            setHeader(key, json);
        }
    }

    public Date getHeaderAsDate(String key) throws Exception {
        String s = getHeader(key);
        if (Strings.isNullOrEmpty(s)) {
            return null;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HeaderKeys.DATE_FORMAT);
            return simpleDateFormat.parse(s);
        }
    }



    public void setHeaderAsDate(String key, Date d) throws Exception {
        if (d == null) {
            setHeader(key, null);
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HeaderKeys.DATE_FORMAT);
            setHeader(key, simpleDateFormat.format(d));
        }
    }

    public Boolean getHeaderAsBoolean(String key) {
        String s = getHeader(key);
        if (Strings.isNullOrEmpty(s)) {
            return null;
        } else {
            return Boolean.valueOf(s);
        }
    }

    public void setHeaderAsBoolean(String key, Boolean b) {
        if (b == null) {
            setHeader(key, null);
        } else {
            setHeader(key, b.toString());
        }
    }

    public List<String> getHeaderAsStringList(String headerKey) throws Exception {
        String[] arr = getHeaderAsStringArray(headerKey);
        if (arr == null) {
            return null;
        } else {
            return Lists.newArrayList(arr);
        }
    }

    public void setHeaderAsStringList(String key, List<String> list) throws Exception {
        if (list == null) {
            setHeader(key, null);
        } else {
            setHeaderAsStringArray(key, list.toArray(new String[]{}));
        }
    }


    public Long getHeaderAsLong(String key) {
        String s = getHeader(key);
        if (Strings.isNullOrEmpty(s)) {
            return null;
        } else {
            return Long.valueOf(s);
        }
    }

    public void setHeaderAsLong(String key, Long l) {
        if (l == null) {
            setHeader(key, null);
        } else {
            setHeader(key, l.toString());
        }
    }
}
