package de.uni_stuttgart.informatik.sopra.sopraapp.damagecases;

/**
 * FireDamageCase Created by Alexander on 10.11.2017.
 * Description:
 **/
public class FloodDamageCase extends AbstractDamageCase {
    private double FloodLevel;

    public FloodDamageCase(String name, CaseID caseID, Area area, DamageType damageType, DamageLevel damageLevel, InsuranceContract insuranceContract) {
        super(name, caseID, area, damageType, damageLevel, insuranceContract);
    }
}
