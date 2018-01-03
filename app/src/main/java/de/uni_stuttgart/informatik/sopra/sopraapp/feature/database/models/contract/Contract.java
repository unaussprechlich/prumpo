package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract;

import android.arch.lifecycle.LiveData;
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
import java.util.stream.Collectors;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseRepository;


/**
 * Represents one record of the Contract table.
 */
@Entity(tableName = Contract.TABLE_NAME)
public class Contract implements ModelDB {

    public static final String TABLE_NAME = "contract";
    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    @Ignore @Inject ContractRepository contractRepository;
    @Ignore @Inject DamageCaseRepository damageCaseRepository;

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    long id;

    @ColumnInfo(index = true)
    long ownerID;

    @ColumnInfo(index = true)
    String name;

    List<Long> damageCases = new ArrayList<>();

    List<LatLng> coordinates = new ArrayList<>();
    String areaCode;
    double areaSize;

    @ColumnInfo(index = true)
    DateTime date;

    public Contract(
            String name,
            String areaCode,
            double areaSize,
            long ownerID,
            List<LatLng> coordinates,
            DateTime date,
            boolean intial) {
        this(name, areaCode, areaSize, ownerID, coordinates, date);
        this.initial = intial;
    }

    public Contract(
            String name,
            String areaCode,
            double areaSize,
            long ownerID,
            List<LatLng> coordinates,
            DateTime date) {
        SopraApp.getAppComponent().inject(this);
        this.name = name;
        this.areaCode = areaCode;
        this.areaSize = areaSize;
        this.ownerID = ownerID;
        this.coordinates = coordinates;
        this.date = date;
    }

    public boolean isChanged() {
        return isChanged;
    }

    //GETTER #######################################################################################

    public long save() throws ExecutionException, InterruptedException {
        if(initial) return contractRepository.insert(this);
        else if(isChanged) contractRepository.update(this);
        isChanged = false;
        return id;
    }

    public String getName() {
        return name;
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

    public void addDamageCase(DamageCase damageCase) throws ExecutionException, InterruptedException {
        damageCase.setContractID(this.id).save();
        this.damageCases.add(damageCase.getID());
    }

    public void removeDamageCase(DamageCase damageCase) throws ExecutionException, InterruptedException {
        damageCase.setContractID(-1).save();
        this.damageCases.remove(damageCase.getID());
    }

    public List<LiveData<DamageCase>> getAllLiveDataDamageCases(){
        return damageCases.stream().map(damageCaseRepository::getById).collect(Collectors.toList());
    }

    public List<DamageCase> getAllDamageCases(){
        return getAllLiveDataDamageCases().stream().map(LiveData::getValue).collect(Collectors.toList());
    }

    // SETTER ######################################################################################

    public Contract setName(String name) {
        isChanged = true;
        this.name = name;
        return this;
    }

    public Contract setCoordinates(List<LatLng> coordinates) {
        isChanged = true;
        this.coordinates = coordinates;
        return this;
    }

    public Contract setAreaCode(String areaCode) {
        isChanged = true;
        this.areaCode = areaCode;
        return this;
    }

    public Contract setDate(DateTime date) {
        isChanged = true;
        this.date = date;
        return this;
    }

    public Contract setAreaSize(double areaSize) {
        isChanged = true;
        this.areaSize = areaSize;
        return this;
    }
}