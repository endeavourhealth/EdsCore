package org.endeavourhealth.core.database.rdbms.subscriberTransform.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "pcr_person_update_history")
public class RdbmsPcrPersonUpdateHistory implements Serializable {

    private Date dateRun = null;

    public RdbmsPcrPersonUpdateHistory() {}

    @Id
    @Column(name = "date_run", nullable = false)
    public Date getDateRun() {
        return dateRun;
    }

    public void setDateRun(Date dateRun) {
        this.dateRun = dateRun;
    }


}
