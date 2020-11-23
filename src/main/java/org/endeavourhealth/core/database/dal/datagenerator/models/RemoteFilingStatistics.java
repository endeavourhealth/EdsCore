package org.endeavourhealth.core.database.dal.datagenerator.models;

public class RemoteFilingStatistics {

    private Integer subscriberId;
    private String statisticsText = null;
    private String statisticsValue = null;

    public Integer getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Integer subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getStatisticsText() {
        return statisticsText;
    }

    public void setStatisticsText(String statisticsText) {
        this.statisticsText = statisticsText;
    }

    public String getStatisticsValue() {
        return statisticsValue;
    }

    public void setStatisticsValue(String statisticsValue) {
        this.statisticsValue = statisticsValue;
    }
}
