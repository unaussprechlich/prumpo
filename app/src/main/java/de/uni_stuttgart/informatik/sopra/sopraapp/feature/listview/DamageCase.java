package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview;

import java.util.Collection;
import java.util.GregorianCalendar;

public class DamageCase {

    /*
     * TODO! Make a data base / persistence object
     * Schadensfälle können mit der Angabe des
     *
     * Versicherungsobjekts
     * - Name des Versicherungsnehmers,
     * - Fläche und Koordinaten des Objekts,
     * - Region (mind. Landkreis),
     *
     * Schadensinformationen
     * - Schadensfläche,
     * - Schadensposition,
     * - Schadens-Koordinaten/-Polygon
     * - Datum
     *
     * und Name des Gutachters erfasst werden.
     */

    private String nameDamageCase;
    private String namePolicyholder; // Name Versicherungsnehmer
    private String nameExpert; // Name Gutachter
    private float area;
    private Collection<Float> coordinates;
    private String region;
    private float damageArea;
    private float damagePositon;
    //    private Polygon damagePolygon;
    private GregorianCalendar date;

    public DamageCase(String nameDamageCase,
                      String namePolicyholder,
                      String nameExpert,
                      float area,
                      Collection<Float> coordinates,
                      String region,
                      float damageArea,
                      float damagePositon,
                      GregorianCalendar date) {

        this.nameDamageCase = nameDamageCase;
        this.namePolicyholder = namePolicyholder;
        this.nameExpert = nameExpert;
        this.area = area;
        this.coordinates = coordinates;
        this.region = region;
        this.damageArea = damageArea;
        this.damagePositon = damagePositon;
        this.date = date;
    }

    /**
     * For demonstrating purposes.
     */
    public DamageCase(String nameDamageCase, String namePolicyholder, float area) {
        this.nameDamageCase = nameDamageCase;
        this.namePolicyholder = namePolicyholder;
        this.area = area;
    }

    public String getNameDamageCase() {
        return nameDamageCase;
    }

    public void setNameDamageCase(String nameDamageCase) {
        this.nameDamageCase = nameDamageCase;
    }

    public String getNamePolicyholder() {
        return namePolicyholder;
    }

    public void setNamePolicyholder(String namePolicyholder) {
        this.namePolicyholder = namePolicyholder;
    }

    public String getNameExpert() {
        return nameExpert;
    }

    public void setNameExpert(String nameExpert) {
        this.nameExpert = nameExpert;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public Collection<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Collection<Float> coordinates) {
        this.coordinates = coordinates;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public float getDamageArea() {
        return damageArea;
    }

    public void setDamageArea(float damageArea) {
        this.damageArea = damageArea;
    }

    public float getDamagePositon() {
        return damagePositon;
    }

    public void setDamagePositon(float damagePositon) {
        this.damagePositon = damagePositon;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }
}
