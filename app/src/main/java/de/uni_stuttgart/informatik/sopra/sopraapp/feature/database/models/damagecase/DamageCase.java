package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.ModelDB;


/**
 * Represents one record of the DamageCase table.
 */
@Entity(tableName = DamageCase.TABLE_NAME)
public class DamageCase implements ModelDB {

    public static final String TABLE_NAME = "damagecase";
    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    @Ignore
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
            DateTime date,
            boolean intial) {
        this(nameDamageCase, namePolicyholder, nameExpert, areaCode, areaSize, ownerID, coordinates, date);
        this.initial = intial;
    }

    public DamageCase(
            String nameDamageCase,
            String namePolicyholder,
            String nameExpert,
            String areaCode,
            double areaSize,
            long ownerID,
            List<LatLng> coordinates,
            DateTime date) {
        SopraApp.getAppComponent().inject(this);
        this.nameDamageCase = nameDamageCase;
        this.namePolicyholder = namePolicyholder;
        this.nameExpert = nameExpert;
        this.areaCode = areaCode;
        this.areaSize = areaSize;
        this.ownerID = ownerID;
        this.coordinates = coordinates;
        this.date = date;
    }

    //GETTER #######################################################################################

    public long save() throws ExecutionException, InterruptedException {
        if(initial) return damageCaseRepository.insert(this);
        else if(isChanged) damageCaseRepository.update(this);
        isChanged = false;
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

    @Override
    public long getID() {
        return id;
    }

    @Override
    public long getOwnerID() {
        return ownerID;
    }

    // SETTER ######################################################################################

    public DamageCase setNameDamageCase(String nameDamageCase) {
        isChanged = true;
        this.nameDamageCase = nameDamageCase;
        return this;
    }

    public DamageCase setNamePolicyholder(String namePolicyholder) {
        isChanged = true;
        this.namePolicyholder = namePolicyholder;
        return this;
    }

    public DamageCase setNameExpert(String nameExpert) {
        isChanged = true;
        this.nameExpert = nameExpert;
        return this;
    }

    public DamageCase setCoordinates(List<LatLng> coordinates) {
        isChanged = true;
        this.coordinates = coordinates;
        return this;
    }

    public DamageCase setAreaCode(String areaCode) {
        isChanged = true;
        this.areaCode = areaCode;
        return this;
    }

    public DamageCase setDate(DateTime date) {
        isChanged = true;
        this.date = date;
        return this;
    }

    public DamageCase setAreaSize(double areaSize) {
        isChanged = true;
        this.areaSize = areaSize;
        return this;
    }


}