package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;

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

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.Contract;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;


/**
 * Represents one record of the DamageCase table.
 */
@Entity(tableName = DamageCase.TABLE_NAME)
public final class DamageCase implements ModelDB<DamageCaseRepository> {

    public static final String TABLE_NAME = "damagecase";
    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    @Ignore @Inject DamageCaseRepository damageCaseRepository;
    @Ignore @Inject UserRepository userRepository;
    @Ignore @Inject ContractRepository contractRepository;
    @Ignore @Inject UserManager userManager;

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    long id;

    @ColumnInfo(index = true)
    long ownerID;

    @ColumnInfo(index = true)
    String name;

    long expertID;
    @Ignore LiveData<User> expert;
    long contractID;
    @Ignore LiveData<Contract> contract;

    List<LatLng> coordinates = new ArrayList<>();
    String areaCode;

    @ColumnInfo(index = true)
    DateTime date;
    double areaSize;

    public DamageCase(
            String name,
            long expertID,
            long contractID,
            String areaCode,
            double areaSize,
            long ownerID,
            List<LatLng> coordinates,
            DateTime date,
            boolean intial) {
        this(name, expertID, contractID, areaCode, areaSize, ownerID, coordinates, date);
        this.initial = intial;
    }

    public DamageCase(
            String name,
            long expertID,
            long contractID,
            String areaCode,
            double areaSize,
            long ownerID,
            List<LatLng> coordinates,
            DateTime date) {
        SopraApp.getAppComponent().inject(this);
        this.name = name;
        setExpertID(expertID);
        setContractID(contractID);
        this.areaCode = areaCode;
        this.areaSize = areaSize;
        this.ownerID = ownerID;
        this.coordinates = coordinates;
        this.date = date;
    }

    public boolean isChanged() {
        return isChanged;
    }

    @Override
    public boolean isInitial() {
        return initial;
    }

    //FUN  #########################################################################################

    public long save() throws ExecutionException, InterruptedException {
        if(initial) return damageCaseRepository.insert(this);
        else if(isChanged) damageCaseRepository.update(this);
        isChanged = false;
        return id;
    }

    @Override
    public DamageCaseRepository getRepository() {
        return damageCaseRepository;
    }

    //GETTER #######################################################################################

    public String getName() {
        return name;
    }

    public long getContractID() {
        return contractID;
    }

    public long getExpertID() {
        return expertID;
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

    public LiveData<Contract> getContract(){
        return contract;
    }

    public LiveData<User> getExpert() {
        return expert;
    }


    // SETTER ######################################################################################

    public DamageCase setName(String name) {
        isChanged = true;
        this.name = name;
        return this;
    }

    public DamageCase setContractID(long contractID) {
        isChanged = true;
        this.contractID = contractID;
        this.contract = contractRepository.getById(contractID);
        return this;
    }

    public DamageCase setExpertID(long expertID) {
        isChanged = true;
        this.expertID = expertID;
        this.expert = userRepository.getById(expertID);
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

    public static final class Builder {
        private String name = "";
        private long contractID = -1;
        private long expertID = -1;
        private String areaCode = "";
        private double areaSize = 0;
        private List<LatLng> coordinates = new ArrayList<>();
        private DateTime date = DateTime.now();

        @Inject UserManager userManager;

        Builder(){
            SopraApp.getAppComponent().inject(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setContractID(long contractID) {
            this.contractID = contractID;
            return this;
        }

        public Builder setExpertID(long expertID) {
            this.expertID = expertID;
            return this;
        }

        public Builder setAreaCode(String areaCode) {
            this.areaCode = areaCode;
            return this;
        }

        public Builder setAreaSize(double areaSize) {
            this.areaSize = areaSize;
            return this;
        }

        public Builder setCoordinates(List<LatLng> coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public Builder setDate(DateTime date) {
            this.date = date;
            return this;
        }

        public DamageCase create() throws UserManager.NoUserException {
            long ownerID = userManager.getCurrentUser().getID();
            return new DamageCase(name, expertID, contractID, areaCode, areaSize, ownerID, coordinates, date, true);
        }
    }
}