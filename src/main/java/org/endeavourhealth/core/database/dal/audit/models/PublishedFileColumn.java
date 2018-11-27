package org.endeavourhealth.core.database.dal.audit.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishedFileColumn {
    private static final Logger LOG = LoggerFactory.getLogger(PublishedFileColumn.class);

    private String columnName;
    private Integer fixedColumnStart;
    private Integer fixedColumnLength;

    public PublishedFileColumn() {}

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getFixedColumnStart() {
        return fixedColumnStart;
    }

    public void setFixedColumnStart(Integer fixedColumnStart) {
        this.fixedColumnStart = fixedColumnStart;
    }

    public Integer getFixedColumnLength() {
        return fixedColumnLength;
    }

    public void setFixedColumnLength(Integer fixedColumnLength) {
        this.fixedColumnLength = fixedColumnLength;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PublishedFileColumn)) {
            return false;
        }

        PublishedFileColumn other = (PublishedFileColumn)obj;
        if (!columnName.equalsIgnoreCase(other.getColumnName())) {
            return false;
        }

        if (!intsEquals(fixedColumnStart, other.getFixedColumnStart())) {
            return false;
        }

        if (!intsEquals(fixedColumnLength, other.getFixedColumnLength())) {
            return false;
        }

        return true;
    }

    private static boolean intsEquals(Integer i1, Integer i2) {
        if (i1 == null && i2 == null) {
            return true;
        }

        if (i1 != null
                && i2 != null
                && i1.equals(i2)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Col " + columnName + " start " + fixedColumnStart + " len " + fixedColumnLength;
    }
}
