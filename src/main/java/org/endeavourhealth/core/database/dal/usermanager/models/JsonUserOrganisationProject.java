package org.endeavourhealth.core.database.dal.usermanager.models;


import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.OrganisationEntity;
import org.endeavourhealth.core.database.dal.datasharingmanager.models.JsonProject;

import java.util.ArrayList;
import java.util.List;

public class JsonUserOrganisationProject {

    private OrganisationEntity organisation = null;
    private List<JsonProject> projects = new ArrayList<>();

    public OrganisationEntity getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationEntity organisation) {
        this.organisation = organisation;
    }

    public List<JsonProject> getProjects() {
        return projects;
    }

    public void setProjects(List<JsonProject> projects) {
        this.projects = projects;
    }

    public void addProject(JsonProject project) {
        if (this.projects == null) {
            this.projects = new ArrayList<>();
        }

        this.projects.add(project);
    }
}
