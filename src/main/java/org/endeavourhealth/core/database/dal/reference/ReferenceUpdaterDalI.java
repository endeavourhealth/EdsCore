package org.endeavourhealth.core.database.dal.reference;

public interface ReferenceUpdaterDalI {

    void updateLosaMap(String lsoaCode, String lsoaName) throws Exception;
    void updateMosaMap(String msoaCode, String msoaName) throws Exception;
    void updateCcgMap(String ccgCode, String ccgName) throws Exception;
    void updateWardMap(String wardCode, String wardName) throws Exception;
    void updateLocalAuthorityMap(String localAuthorityCode, String localAuthorityName) throws Exception;
    void updatePostcodeMap(String postcode, String lsoaCode, String msoaCode, String ward, String ccgCode, String localAuthority) throws Exception;

    void updateDeprivationMap(String lsoaCode,
                                Integer rank,
                                Integer decile,
                                Integer incomeRank,
                                Integer incomeDecile,
                                Integer employmentRank,
                                Integer employmentDecile,
                                Integer educationRank,
                                Integer educationDecile,
                                Integer healthRank,
                                Integer healthDecile,
                                Integer crimeRank,
                                Integer crimeDecile,
                                Integer housingAndServicesBarriersRank,
                                Integer housingAndServicesBarriersDecile,
                                Integer livingEnvironmentRank,
                                Integer livingEnvironmentDecile) throws Exception;

}
