package org.endeavourhealth.core.database.dal.reference;

public interface ReferenceUpdaterDalI {

    void updateLosaMap(String lsoaCode, String lsoaName) throws Exception;
    void updateMosaMap(String msoaCode, String msoaName) throws Exception;
    void updatePostcodeMap(String postcode, String lsoaCode, String msoaCode, String ward, String ward1998, String ccgCode) throws Exception;

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
