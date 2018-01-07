package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;

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
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;


/**
 * Represents one record of the Contract table.
 */
@Entity(tableName = Contract.TABLE_NAME)
public class Contract implements ModelDB<ContractRepository> {

    public static final String TABLE_NAME = "contract";
    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    @Ignore @Inject ContractRepository contractRepository;
    @Ignore @Inject DamageCaseRepository damageCaseRepository;
    @Ignore @Inject UserRepository userRepository;

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    long id;

    @ColumnInfo(index = true)
    long ownerID;

    @ColumnInfo(index = true)
    long holderID;
    @Ignore LiveData<User> holder;

    @ColumnInfo(index = true)
    String name;

    String damageType;

    List<Long> damageCaseIDs = new ArrayList<>();
    @Ignore List<LiveData<DamageCase>> damageCases;

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
            long holderID,
            List<LatLng> coordinates,
            DateTime date,
            String damageType,
            boolean intial) {
        this(name, areaCode, areaSize, ownerID, holderID, coordinates, date, damageType);
        this.initial = intial;
    }

    public Contract(
            String name,
            String areaCode,
            double areaSize,
            long ownerID,
            long holderID,
            List<LatLng> coordinates,
            DateTime date,
            String damageType) {
        SopraApp.getAppComponent().inject(this);
        this.name = name;
        this.areaCode = areaCode;
        this.areaSize = areaSize;
        this.ownerID = ownerID;
        setHolderID(holderID);
        this.coordinates = coordinates;
        this.date = date;
        this.damageType = damageType;
    }

    public boolean isChanged() {
        return isChanged;
    }

    @Override
    public boolean isInitial() {
        return initial;
    }

    //GETTER #######################################################################################

    @Override
    public long save() throws ExecutionException, InterruptedException {
        if(initial) return contractRepository.insert(this);
        else if(isChanged) contractRepository.update(this);
        isChanged = false;
        return id;
    }

    @Override
    public ContractRepository getRepository() {
        return contractRepository;
    }

    public String getName() {
        return name;
    }

    public String getDamageType() {
        return damageType;
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

    public long getHolderID() {
        return holderID;
    }

    public void addDamageCase(DamageCase damageCase) throws ExecutionException, InterruptedException {
        damageCase.setContractID(this.id).save();
        this.damageCaseIDs.add(damageCase.getID());
        loadDamageCases();
    }

    public void removeDamageCase(DamageCase damageCase) throws ExecutionException, InterruptedException {
        damageCase.setContractID(-1).save();
        this.damageCaseIDs.remove(damageCase.getID());
        loadDamageCases();
    }

    private void loadDamageCases(){
        this.damageCases = damageCaseIDs.stream().map(damageCaseRepository::getById).collect(Collectors.toList());
    }

    public LiveData<User> getHolder(){
        return holder;
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

    public Contract setHolderID(long holderID) {
        isChanged = true;
        this.holderID = holderID;
        this.holder = userRepository.getById(holderID);
        return this;
    }

    void setDamageCaseIDs(List<Long> IDs){
        this.damageCaseIDs = IDs;
        loadDamageCases();
    }

    public Contract setDamageType(String damageType) {
        isChanged = true;
        this.damageType = damageType;
        return this;
    }

    public static final class Builder {
        private String name = "";
        private String areaCode = "";
        private double areaSize = 0;
        private long holderID = -1;
        private List<LatLng> coordinates = new ArrayList<>();
        private DateTime date = DateTime.now();
        private String damageType = "";

        @Inject UserManager userManager;

        Builder(){
            SopraApp.getAppComponent().inject(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDamageType(String damageType) {
            this.damageType = damageType;
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

        public Builder setHolder(User holder) {
            this.holderID = holder.getID();
            return this;
        }

        public Builder setHolder(long holder) {
            this.holderID = holder;
            return this;
        }

        public Contract create() throws UserManager.NoUserException {
            long ownerID = userManager.getCurrentUser().getID();
            return new Contract(name, areaCode, areaSize, ownerID, holderID, coordinates, date, damageType, true);
        }
    }
}