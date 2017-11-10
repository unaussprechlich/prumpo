package de.uni_stuttgart.informatik.sopra.sopraapp.damagecases;

/**
 * FireDamageCase Created by Alexander on 10.11.2017.
 * Description:
 **/
public class WindDamageCase extends AbstractDamageCase {
    private WindStrength windStrength;

    public WindDamageCase(String name, CaseID caseID, Area area, DamageType damageType, DamageLevel damageLevel, InsuranceContract insuranceContract) {
        super(name, caseID, area, damageType, damageLevel, insuranceContract);
    }
}
