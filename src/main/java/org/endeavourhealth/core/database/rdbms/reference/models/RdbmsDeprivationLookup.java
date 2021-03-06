package org.endeavourhealth.core.database.rdbms.reference.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "deprivation_lookup")
public class RdbmsDeprivationLookup implements Serializable {

    private String lsoaCode;
    private Double imdScore;
    private Integer imdRank;
    private Integer imdDecile;
    private Double incomeScore;
    private Integer incomeRank;
    private Integer incomeDecile;
    private Double employmentScore;
    private Integer employmentRank;
    private Integer employmentDecile;
    private Double educationScore;
    private Integer educationRank;
    private Integer educationDecile;
    private Double healthScore;
    private Integer healthRank;
    private Integer healthDecile;
    private Double crimeScore;
    private Integer crimeRank;
    private Integer crimeDecile;
    private Double housingAndServicesBarriersScore;
    private Integer housingAndServicesBarriersRank;
    private Integer housingAndServicesBarriersDecile;
    private Double livingEnvironmentScore;
    private Integer livingEnvironmentRank;
    private Integer livingEnvironmentDecile;
    private Double idaciScore;
    private Integer idaciRank;
    private Integer idaciDecile;
    private Double idaopiScore;
    private Integer idaopiRank;
    private Integer idaopiDecile;
    private Double childrenAndYoungSubDomainScore;
    private Integer childrenAndYoungSubDomainRank;
    private Integer childrenAndYoungSubDomainDecile;
    private Double adultSkillsSubDomainScore;
    private Integer adultSkillsSubDomainRank;
    private Integer adultSkillsSubDomainDecile;
    private Double geographicalBarriersSubDomainScore;
    private Integer geographicalBarriersSubDomainRank;
    private Integer geographicalBarriersSubDomainDecile;
    private Double widerBarriersSubDomainScore;
    private Integer widerBarriersSubDomainRank;
    private Integer widerBarriersSubDomainDecile;
    private Double indoorsSubDomainScore;
    private Integer indoorsSubDomainRank;
    private Integer indoorsSubDomainDecile;
    private Double outdoorsSubDomainScore;
    private Integer outdoorsSubDomainRank;
    private Integer outdoorsSubDomainDecile;
    private Integer totalPopulation;
    private Integer dependentChildren0To15;
    private Integer population16To59;
    private Integer olderPopulation60AndOver;
    //private Integer workingAgePopulation;

    public RdbmsDeprivationLookup() {}

    @Id
    @Column(name = "lsoa_code", nullable = false)
    public String getLsoaCode() {
        return lsoaCode;
    }

    public void setLsoaCode(String lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    @Column(name = "imd_score", nullable = false)
    public Double getImdScore() {
        return imdScore;
    }

    public void setImdScore(Double imdScore) {
        this.imdScore = imdScore;
    }

    @Column(name = "imd_rank", nullable = false)
    public Integer getImdRank() {
        return imdRank;
    }

    public void setImdRank(Integer imdRank) {
        this.imdRank = imdRank;
    }

    @Column(name = "imd_decile", nullable = false)
    public Integer getImdDecile() {
        return imdDecile;
    }

    public void setImdDecile(Integer imdDecile) {
        this.imdDecile = imdDecile;
    }

    @Column(name = "income_score", nullable = false)
    public Double getIncomeScore() {
        return incomeScore;
    }

    public void setIncomeScore(Double incomeScore) {
        this.incomeScore = incomeScore;
    }

    @Column(name = "income_rank", nullable = false)
    public Integer getIncomeRank() {
        return incomeRank;
    }

    public void setIncomeRank(Integer incomeRank) {
        this.incomeRank = incomeRank;
    }

    @Column(name = "income_decile", nullable = false)
    public Integer getIncomeDecile() {
        return incomeDecile;
    }

    public void setIncomeDecile(Integer incomeDecile) {
        this.incomeDecile = incomeDecile;
    }

    @Column(name = "employment_score", nullable = false)
    public Double getEmploymentScore() {
        return employmentScore;
    }

    public void setEmploymentScore(Double employmentScore) {
        this.employmentScore = employmentScore;
    }

    @Column(name = "employment_rank", nullable = false)
    public Integer getEmploymentRank() {
        return employmentRank;
    }

    public void setEmploymentRank(Integer employmentRank) {
        this.employmentRank = employmentRank;
    }

    @Column(name = "employment_decile", nullable = false)
    public Integer getEmploymentDecile() {
        return employmentDecile;
    }

    public void setEmploymentDecile(Integer employmentDecile) {
        this.employmentDecile = employmentDecile;
    }

    @Column(name = "education_score", nullable = false)
    public Double getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(Double educationScore) {
        this.educationScore = educationScore;
    }

    @Column(name = "education_rank", nullable = false)
    public Integer getEducationRank() {
        return educationRank;
    }

    public void setEducationRank(Integer educationRank) {
        this.educationRank = educationRank;
    }

    @Column(name = "education_decile", nullable = false)
    public Integer getEducationDecile() {
        return educationDecile;
    }

    public void setEducationDecile(Integer educationDecile) {
        this.educationDecile = educationDecile;
    }

    @Column(name = "health_score", nullable = false)
    public Double getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(Double healthScore) {
        this.healthScore = healthScore;
    }

    @Column(name = "health_rank", nullable = false)
    public Integer getHealthRank() {
        return healthRank;
    }

    public void setHealthRank(Integer healthRank) {
        this.healthRank = healthRank;
    }

    @Column(name = "health_decile", nullable = false)
    public Integer getHealthDecile() {
        return healthDecile;
    }

    public void setHealthDecile(Integer healthDecile) {
        this.healthDecile = healthDecile;
    }

    @Column(name = "crime_score", nullable = false)
    public Double getCrimeScore() {
        return crimeScore;
    }

    public void setCrimeScore(Double crimeScore) {
        this.crimeScore = crimeScore;
    }

    @Column(name = "crime_rank", nullable = false)
    public Integer getCrimeRank() {
        return crimeRank;
    }

    public void setCrimeRank(Integer crimeRank) {
        this.crimeRank = crimeRank;
    }

    @Column(name = "crime_decile", nullable = false)
    public Integer getCrimeDecile() {
        return crimeDecile;
    }

    public void setCrimeDecile(Integer crimeDecile) {
        this.crimeDecile = crimeDecile;
    }

    @Column(name = "housing_and_services_barriers_score", nullable = false)
    public Double getHousingAndServicesBarriersScore() {
        return housingAndServicesBarriersScore;
    }

    public void setHousingAndServicesBarriersScore(Double housingAndServicesBarriersScore) {
        this.housingAndServicesBarriersScore = housingAndServicesBarriersScore;
    }

    @Column(name = "housing_and_services_barriers_rank", nullable = false)
    public Integer getHousingAndServicesBarriersRank() {
        return housingAndServicesBarriersRank;
    }

    public void setHousingAndServicesBarriersRank(Integer housingAndServicesBarriersRank) {
        this.housingAndServicesBarriersRank = housingAndServicesBarriersRank;
    }

    @Column(name = "housing_and_services_barriers_decile", nullable = false)
    public Integer getHousingAndServicesBarriersDecile() {
        return housingAndServicesBarriersDecile;
    }

    public void setHousingAndServicesBarriersDecile(Integer housingAndServicesBarriersDecile) {
        this.housingAndServicesBarriersDecile = housingAndServicesBarriersDecile;
    }

    @Column(name = "living_environment_score", nullable = false)
    public Double getLivingEnvironmentScore() {
        return livingEnvironmentScore;
    }

    public void setLivingEnvironmentScore(Double livingEnvironmentScore) {
        this.livingEnvironmentScore = livingEnvironmentScore;
    }

    @Column(name = "living_environment_rank", nullable = false)
    public Integer getLivingEnvironmentRank() {
        return livingEnvironmentRank;
    }

    public void setLivingEnvironmentRank(Integer livingEnvironmentRank) {
        this.livingEnvironmentRank = livingEnvironmentRank;
    }

    @Column(name = "living_environment_decile", nullable = false)
    public Integer getLivingEnvironmentDecile() {
        return livingEnvironmentDecile;
    }

    public void setLivingEnvironmentDecile(Integer livingEnvironmentDecile) {
        this.livingEnvironmentDecile = livingEnvironmentDecile;
    }

    @Column(name = "idaci_score", nullable = false)
    public Double getIdaciScore() {
        return idaciScore;
    }

    public void setIdaciScore(Double idaciScore) {
        this.idaciScore = idaciScore;
    }

    @Column(name = "idaci_rank", nullable = false)
    public Integer getIdaciRank() {
        return idaciRank;
    }

    public void setIdaciRank(Integer idaciRank) {
        this.idaciRank = idaciRank;
    }

    @Column(name = "idaci_decile", nullable = false)
    public Integer getIdaciDecile() {
        return idaciDecile;
    }

    public void setIdaciDecile(Integer idaciDecile) {
        this.idaciDecile = idaciDecile;
    }

    @Column(name = "idaopi_score", nullable = false)
    public Double getIdaopiScore() {
        return idaopiScore;
    }

    public void setIdaopiScore(Double idaopiScore) {
        this.idaopiScore = idaopiScore;
    }

    @Column(name = "idaopi_rank", nullable = false)
    public Integer getIdaopiRank() {
        return idaopiRank;
    }

    public void setIdaopiRank(Integer idaopiRank) {
        this.idaopiRank = idaopiRank;
    }

    @Column(name = "idaopi_decile", nullable = false)
    public Integer getIdaopiDecile() {
        return idaopiDecile;
    }

    public void setIdaopiDecile(Integer idaopiDecile) {
        this.idaopiDecile = idaopiDecile;
    }

    @Column(name = "children_and_young_sub_domain_score", nullable = false)
    public Double getChildrenAndYoungSubDomainScore() {
        return childrenAndYoungSubDomainScore;
    }

    public void setChildrenAndYoungSubDomainScore(Double childrenAndYoungSubDomainScore) {
        this.childrenAndYoungSubDomainScore = childrenAndYoungSubDomainScore;
    }

    @Column(name = "children_and_young_sub_domain_rank", nullable = false)
    public Integer getChildrenAndYoungSubDomainRank() {
        return childrenAndYoungSubDomainRank;
    }

    public void setChildrenAndYoungSubDomainRank(Integer childrenAndYoungSubDomainRank) {
        this.childrenAndYoungSubDomainRank = childrenAndYoungSubDomainRank;
    }

    @Column(name = "children_and_young_sub_domain_decile", nullable = false)
    public Integer getChildrenAndYoungSubDomainDecile() {
        return childrenAndYoungSubDomainDecile;
    }

    public void setChildrenAndYoungSubDomainDecile(Integer childrenAndYoungSubDomainDecile) {
        this.childrenAndYoungSubDomainDecile = childrenAndYoungSubDomainDecile;
    }

    @Column(name = "adult_skills_sub_somain_score", nullable = false)
    public Double getAdultSkillsSubDomainScore() {
        return adultSkillsSubDomainScore;
    }

    public void setAdultSkillsSubDomainScore(Double adultSkillsSubDomainScore) {
        this.adultSkillsSubDomainScore = adultSkillsSubDomainScore;
    }

    @Column(name = "adult_skills_sub_somain_rank", nullable = false)
    public Integer getAdultSkillsSubDomainRank() {
        return adultSkillsSubDomainRank;
    }

    public void setAdultSkillsSubDomainRank(Integer adultSkillsSubDomainRank) {
        this.adultSkillsSubDomainRank = adultSkillsSubDomainRank;
    }

    @Column(name = "adult_skills_sub_somain_decile", nullable = false)
    public Integer getAdultSkillsSubDomainDecile() {
        return adultSkillsSubDomainDecile;
    }

    public void setAdultSkillsSubDomainDecile(Integer adultSkillsSubDomainDecile) {
        this.adultSkillsSubDomainDecile = adultSkillsSubDomainDecile;
    }

    @Column(name = "grographical_barriers_sub_domain_score", nullable = false)
    public Double getGeographicalBarriersSubDomainScore() {
        return geographicalBarriersSubDomainScore;
    }

    public void setGeographicalBarriersSubDomainScore(Double geographicalBarriersSubDomainScore) {
        this.geographicalBarriersSubDomainScore = geographicalBarriersSubDomainScore;
    }

    @Column(name = "grographical_barriers_sub_domain_rank", nullable = false)
    public Integer getGeographicalBarriersSubDomainRank() {
        return geographicalBarriersSubDomainRank;
    }

    public void setGeographicalBarriersSubDomainRank(Integer geographicalBarriersSubDomainRank) {
        this.geographicalBarriersSubDomainRank = geographicalBarriersSubDomainRank;
    }

    @Column(name = "grographical_barriers_sub_domain_decile", nullable = false)
    public Integer getGeographicalBarriersSubDomainDecile() {
        return geographicalBarriersSubDomainDecile;
    }

    public void setGeographicalBarriersSubDomainDecile(Integer geographicalBarriersSubDomainDecile) {
        this.geographicalBarriersSubDomainDecile = geographicalBarriersSubDomainDecile;
    }

    @Column(name = "wider_barriers_sub_domain_score", nullable = false)
    public Double getWiderBarriersSubDomainScore() {
        return widerBarriersSubDomainScore;
    }

    public void setWiderBarriersSubDomainScore(Double widerBarriersSubDomainScore) {
        this.widerBarriersSubDomainScore = widerBarriersSubDomainScore;
    }

    @Column(name = "wider_barriers_sub_domain_rank", nullable = false)
    public Integer getWiderBarriersSubDomainRank() {
        return widerBarriersSubDomainRank;
    }

    public void setWiderBarriersSubDomainRank(Integer widerBarriersSubDomainRank) {
        this.widerBarriersSubDomainRank = widerBarriersSubDomainRank;
    }

    @Column(name = "wider_barriers_sub_domain_decile", nullable = false)
    public Integer getWiderBarriersSubDomainDecile() {
        return widerBarriersSubDomainDecile;
    }

    public void setWiderBarriersSubDomainDecile(Integer widerBarriersSubDomainDecile) {
        this.widerBarriersSubDomainDecile = widerBarriersSubDomainDecile;
    }

    @Column(name = "indoors_sub_domain_score", nullable = false)
    public Double getIndoorsSubDomainScore() {
        return indoorsSubDomainScore;
    }

    public void setIndoorsSubDomainScore(Double indoorsSubDomainScore) {
        this.indoorsSubDomainScore = indoorsSubDomainScore;
    }

    @Column(name = "indoors_sub_domain_rank", nullable = false)
    public Integer getIndoorsSubDomainRank() {
        return indoorsSubDomainRank;
    }

    public void setIndoorsSubDomainRank(Integer indoorsSubDomainRank) {
        this.indoorsSubDomainRank = indoorsSubDomainRank;
    }

    @Column(name = "indoors_sub_domain_decile", nullable = false)
    public Integer getIndoorsSubDomainDecile() {
        return indoorsSubDomainDecile;
    }

    public void setIndoorsSubDomainDecile(Integer indoorsSubDomainDecile) {
        this.indoorsSubDomainDecile = indoorsSubDomainDecile;
    }

    @Column(name = "outdoors_sub_domain_score", nullable = false)
    public Double getOutdoorsSubDomainScore() {
        return outdoorsSubDomainScore;
    }

    public void setOutdoorsSubDomainScore(Double outdoorsSubDomainScore) {
        this.outdoorsSubDomainScore = outdoorsSubDomainScore;
    }

    @Column(name = "outdoors_sub_domain_rank", nullable = false)
    public Integer getOutdoorsSubDomainRank() {
        return outdoorsSubDomainRank;
    }

    public void setOutdoorsSubDomainRank(Integer outdoorsSubDomainRank) {
        this.outdoorsSubDomainRank = outdoorsSubDomainRank;
    }

    @Column(name = "outdoors_sub_domain_decile", nullable = false)
    public Integer getOutdoorsSubDomainDecile() {
        return outdoorsSubDomainDecile;
    }

    public void setOutdoorsSubDomainDecile(Integer outdoorsSubDomainDecile) {
        this.outdoorsSubDomainDecile = outdoorsSubDomainDecile;
    }

    @Column(name = "total_population", nullable = false)
    public Integer getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(Integer totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    @Column(name = "dependent_children_0_to_15", nullable = false)
    public Integer getDependentChildren0To15() {
        return dependentChildren0To15;
    }

    public void setDependentChildren0To15(Integer dependentChildren0To15) {
        this.dependentChildren0To15 = dependentChildren0To15;
    }

    @Column(name = "population_16_to_59", nullable = false)
    public Integer getPopulation16To59() {
        return population16To59;
    }

    public void setPopulation16To59(Integer population16To59) {
        this.population16To59 = population16To59;
    }

    @Column(name = "older_population_60_and_over", nullable = false)
    public Integer getOlderPopulation60AndOver() {
        return olderPopulation60AndOver;
    }

    public void setOlderPopulation60AndOver(Integer olderPopulation60AndOver) {
        this.olderPopulation60AndOver = olderPopulation60AndOver;
    }

    /*@Column(name = "working_age_population", nullable = false)
    public Integer getWorkingAgePopulation() {
        return workingAgePopulation;
    }

    public void setWorkingAgePopulation(Integer workingAgePopulation) {
        this.workingAgePopulation = workingAgePopulation;
    }*/
}
