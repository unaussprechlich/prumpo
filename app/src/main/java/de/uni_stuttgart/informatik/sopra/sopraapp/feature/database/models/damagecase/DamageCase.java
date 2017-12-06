package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.ModelDB;


/**
 * Represents one record of the DamageCase table.
 */
@Entity(tableName = DamageCase.TABLE_NAME)
public class DamageCase implements ModelDB {

    public static final String TABLE_NAME = "damagecase";

    @Inject DamageCaseRepository damageCaseRepository;

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    long id;

    @ColumnInfo(index = true)
    long ownerID;

    @ColumnInfo(index = true)
    String nameDamageCase;
    String namePolicyholder;
    String nameExpert;

    List<LatLng> coordinates = new ArrayList<>();
    String areaCode;
    @ColumnInfo(index = true)
    DateTime date;
    double areaSize;


    public DamageCase(
            String nameDamageCase,
            String namePolicyholder,
            String nameExpert,
            String areaCode,
            double areaSize,
            long ownerID,
            List<LatLng> coordinates,
            DateTime date) {

        this.nameDamageCase = nameDamageCase;
        this.namePolicyholder = namePolicyholder;
        this.nameExpert = nameExpert;
        this.areaCode = areaCode;
        this.areaSize = areaSize;
        this.ownerID = ownerID;
        this.coordinates = coordinates;
        this.date = date;
    }

    public void save(){
        damageCaseRepository.update(this);
    }

    public long getId() {
        return id;
    }

    public String getNameDamageCase() {
        return nameDamageCase;
    }

    public String getNamePolicyholder() {
        return namePolicyholder;
    }

    public String getNameExpert() {
        return nameExpert;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public DateTime getDate() {
        return date;
    }

    public double getAreaSize() {
        return areaSize;
    }

    public void setNameDamageCase(String nameDamageCase) {
        this.nameDamageCase = nameDamageCase;
    }

    public void setNamePolicyholder(String namePolicyholder) {
        this.namePolicyholder = namePolicyholder;
    }

    public void setNameExpert(String nameExpert) {
        this.nameExpert = nameExpert;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public void setAreaSize(double areaSize) {
        this.areaSize = areaSize;
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