package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import java.util.GregorianCalendar;
import java.util.List;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.ModelDB;


/**
 * Represents one record of the User table.
 */
@Entity(tableName = DamageCase.TABLE_NAME)
public class DamageCase implements ModelDB {

    public static final String TABLE_NAME = "damagecase";

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    public long id;

    @ColumnInfo(index = true)
    public long ownerID;

    @ColumnInfo(index = true)
    public String nameDamageCase;
    public String namePolicyholder;
    public String nameExpert; // Name Gutachter
    public float area;


    @Ignore
    private List<Float> coordinates; // TODO! LatitudeItem

    @Ignore private String region;
    @Ignore private float damageArea;
    @Ignore
    private float damagePosition;
    @Ignore private GregorianCalendar date;


    public DamageCase(String nameDamageCase, String namePolicyholder, String nameExpert, float area, long ownerID) {
        this.nameDamageCase = nameDamageCase;
        this.namePolicyholder = namePolicyholder;
        this.nameExpert = nameExpert;
        this.area = area;
        this.ownerID = ownerID;
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

    public List<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Float> coordinates) {
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

    public float getDamagePosition() {
        return damagePosition;
    }

    public void setDamagePosition(float damagePosition) {
        this.damagePosition = damagePosition;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public long getOwnerID() {
        return ownerID;
    }
}