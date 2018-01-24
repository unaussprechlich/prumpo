package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase;

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
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelEntityDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserHandler;


/**
 * Represents one record of the DamageCaseEntity table.
 */
@Entity(tableName = DamageCaseEntity.TABLE_NAME)
public final class DamageCaseEntity implements ModelEntityDB<DamageCaseEntityRepository> {

    public static final String TABLE_NAME = "damagecase";
    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    @Ignore @Inject DamageCaseEntityRepository damageCaseRepository;

    //##############################################################################################

    /** The unique ID of the userEntity. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    public long id;

    @ColumnInfo(index = true, name = "contract_id")
    long contractID;

    @ColumnInfo(index = true, name = "holder_id")
    long holderID;

    @ColumnInfo(index = true,  name = "created_by_id")
    long createdByID;

    List<LatLng> coordinates = new ArrayList<>();

    /**When the damage occurred*/
    @ColumnInfo(index = true)
    DateTime date;

    /**Size of the damaged area*/
    double areaSize;

    //##############################################################################################

    public DamageCaseEntity(
            long holderID,
            long contractID,
            long createdByID,
            double areaSize,
            List<LatLng> coordinates,
            DateTime date,
            boolean intial) {
        this(holderID, contractID, createdByID,areaSize, coordinates, date);
        this.initial = intial;
    }

    public DamageCaseEntity(
            long holderID,
            long contractID,
            long createdByID,
            double areaSize,
            List<LatLng> coordinates,
            DateTime date) {

        //Inject repository
        SopraApp.getAppComponent().inject(this);

        this.holderID = holderID;
        this.contractID = contractID;
        this.createdByID = createdByID;
        this.areaSize = areaSize;
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

    public void delete() throws ExecutionException, InterruptedException {
        damageCaseRepository.delete(this);
    }

    public long save() throws ExecutionException, InterruptedException {
        if(initial) return damageCaseRepository.insert(this);
        else if(isChanged) damageCaseRepository.update(this);
        isChanged = false;
        return id;
    }

    @Override
    public DamageCaseEntityRepository getRepository() {
        return damageCaseRepository;
    }

    //GETTER #######################################################################################

    public long getContractID() {
        return contractID;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public DateTime getDate() {
        return date;
    }

    public double getAreaSize() {
        return areaSize;
    }

    public long getCreatedByID() {
        return createdByID;
    }

    public long getHolderID() {
        return holderID;
    }

    @Override
    public long getID() {
        return id;
    }



    // SETTER ######################################################################################


    public DamageCaseEntity setHolderID(long holderID) {
        if(holderID == -1 || this.holderID == holderID) return this;

        isChanged = true;
        this.holderID = holderID;

        return this;
    }

    public DamageCaseEntity setContractID(long contractID) throws ExecutionException, InterruptedException {
        if(contractID == -1 || this.contractID == contractID) return this;

        isChanged = true;
        this.contractID = contractID;

        return this;
    }

    public DamageCaseEntity setCreatedByID(long createdByID) throws ExecutionException, InterruptedException {
        if(createdByID == -1 || this.createdByID == createdByID) return this;

        isChanged = true;
        this.contractID = contractID;

        return this;
    }

    public DamageCaseEntity setCoordinates(List<LatLng> coordinates) {
        if(this.coordinates.equals(coordinates)) return this;

        isChanged = true;
        this.coordinates = coordinates;
        return this;
    }

    public DamageCaseEntity setDate(DateTime date) {
        if(this.date.equals(date)) return this;

        isChanged = true;
        this.date = date;
        return this;
    }

    public DamageCaseEntity setAreaSize(double areaSize) {
        if(this.areaSize == areaSize) return this;

        isChanged = true;
        this.areaSize = areaSize;
        return this;
    }

    //##############################################################################################

    @Override
    public int hashCode() {
        return ("DAMAGECASE_" + id).hashCode();
    }

    @Override
    public String toString() {
        if(isInitial()) return "";
        return "#" + Math.abs(hashCode());
    }

    //##############################################################################################

    public static final class Builder {
        private long contractID = -1;
        private long holderID = -1;
        private double areaSize = 0;
        private List<LatLng> coordinates = new ArrayList<>();
        private DateTime date = DateTime.now();

        @Inject UserHandler userHandler;

        Builder(){
            SopraApp.getAppComponent().inject(this);
        }

        public Builder setContractID(long contractID) {
            this.contractID = contractID;
            return this;
        }

        public Builder setHolderID(long holderID) {
            this.holderID = holderID;
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

        public DamageCaseEntity create() throws NoUserException {
            long createdBy = userHandler.getCurrentUser().getID();
            return new DamageCaseEntity(holderID, contractID, createdBy, areaSize, coordinates, date, true);
        }
    }
}