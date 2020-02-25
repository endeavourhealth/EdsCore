package org.endeavourhealth.core.database.dal.datasharingmanager;

import org.endeavourhealth.core.database.rdbms.datasharingmanager.models.ExtractTechnicalDetailsEntity;

public interface ExtractTechnicalDetailsDalI {

    public ExtractTechnicalDetailsEntity getExtractTechnicalDetails(String uuid) throws Exception;
}
