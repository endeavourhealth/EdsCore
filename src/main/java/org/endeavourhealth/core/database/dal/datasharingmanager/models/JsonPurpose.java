package org.endeavourhealth.core.database.dal.datasharingmanager.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonPurpose extends JsonItem {
    private String title = null;
    private String detail = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return this.getTitle() + " (" + this.getDetail() + ", " + this.getUuid() + ")";
    }
}