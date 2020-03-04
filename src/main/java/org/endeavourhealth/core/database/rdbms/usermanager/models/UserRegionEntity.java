package org.endeavourhealth.core.database.rdbms.usermanager.models;

import org.endeavourhealth.core.database.dal.usermanager.models.JsonUserRegion;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_region", schema = "user_manager")
public class UserRegionEntity  {
    private String userId;
    private String regionId;

    @Id
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "region_id")
    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegionEntity that = (UserRegionEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(regionId, that.regionId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, regionId);
    }

    public UserRegionEntity() {
    }

    public UserRegionEntity(JsonUserRegion jsonUserRegion) {
        this.userId = jsonUserRegion.getUserId();
        this.regionId = jsonUserRegion.getRegionId();
    }
}
