package org.endeavourhealth.core.database.dal.publisherTransform.models;

import java.util.UUID;

public class CernerClinicalEventMappingState {

    private UUID serviceId;
    private Long eventId;
    private String eventCd;
    private String eventCdTerm;
    private String eventClassCd;
    private String eventClassCdTerm;
    private String eventResultUnitsCd;
    private String eventResultUnitsCdTerm;
    private String eventResultText;
    private String eventTitleText;
    private String eventTagText;
    private String mappedSnomedId;

    public CernerClinicalEventMappingState() {}

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventCd() {
        return eventCd;
    }

    public void setEventCd(String eventCd) {
        this.eventCd = eventCd;
    }

    public String getEventCdTerm() {
        return eventCdTerm;
    }

    public void setEventCdTerm(String eventCdTerm) {
        this.eventCdTerm = eventCdTerm;
    }

    public String getEventClassCd() {
        return eventClassCd;
    }

    public void setEventClassCd(String eventClassCd) {
        this.eventClassCd = eventClassCd;
    }

    public String getEventClassCdTerm() {
        return eventClassCdTerm;
    }

    public void setEventClassCdTerm(String eventClassCdTerm) {
        this.eventClassCdTerm = eventClassCdTerm;
    }

    public String getEventResultUnitsCd() {
        return eventResultUnitsCd;
    }

    public void setEventResultUnitsCd(String eventResultUnitsCd) {
        this.eventResultUnitsCd = eventResultUnitsCd;
    }

    public String getEventResultUnitsCdTerm() {
        return eventResultUnitsCdTerm;
    }

    public void setEventResultUnitsCdTerm(String eventResultUnitsCdTerm) {
        this.eventResultUnitsCdTerm = eventResultUnitsCdTerm;
    }

    public String getEventResultText() {
        return eventResultText;
    }

    public void setEventResultText(String eventResultText) {
        this.eventResultText = eventResultText;
    }

    public String getEventTitleText() {
        return eventTitleText;
    }

    public void setEventTitleText(String eventTitleText) {
        this.eventTitleText = eventTitleText;
    }

    public String getEventTagText() {
        return eventTagText;
    }

    public void setEventTagText(String eventTagText) {
        this.eventTagText = eventTagText;
    }

    public String getMappedSnomedId() {
        return mappedSnomedId;
    }

    public void setMappedSnomedId(String mappedSnomedId) {
        this.mappedSnomedId = mappedSnomedId;
    }

}


