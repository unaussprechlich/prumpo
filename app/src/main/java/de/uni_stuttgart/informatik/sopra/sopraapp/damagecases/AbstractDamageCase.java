package de.uni_stuttgart.informatik.sopra.sopraapp.damagecases;

/**
 * AbstractDamageCase Created by Alexander on 10.11.2017.
 * Description:
 **/
public abstract class AbstractDamageCase extends AbstractDataModelExportable{
    private String name;
    private final CaseID caseID;
    private final DamageType damageType;
    private DamageLevel damageLevel;
    private final InsuranceContract insuranceContract;
    private Date date;
    private java.util.ArrayList<Evidence> evidences;

    public AbstractDamageCase(String name, CaseID caseID, Area area, DamageType damageType, DamageLevel damageLevel, InsuranceContract insuranceContract) {
        this.name = name;
        this.caseID = caseID;
        this.area = area;
        this.damageType = damageType;
        this.damageLevel = damageLevel;
        this.insuranceContract = insuranceContract;
    }


}
