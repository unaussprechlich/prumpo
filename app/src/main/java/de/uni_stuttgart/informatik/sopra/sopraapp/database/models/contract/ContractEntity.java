package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelEntityDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserHandler;


/**
 * Represents one record of the ContractEntity table.
 * <p>
 * All fields annotated with @Expose will be used when exporting to file.
 */
@Entity(tableName = ContractEntity.TABLE_NAME)
public final class ContractEntity implements ModelEntityDB<ContractEntityRepository> {

    public static final String TABLE_NAME = "contract";
    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    @Ignore @Inject
    ContractEntityRepository contractEntityRepository;

    //##############################################################################################

    /** The unique ID of the userEntity. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    @Expose
    public long id;

    @ColumnInfo(index = true, name = "holder_id")
    @Expose
    long holderID;

    @ColumnInfo(index = true, name = "expert_id")
    @Expose
    long expertID;

    @ColumnInfo(index = true, name = "created_by_id")
    @Expose
    long createdByID;

    @Expose
    String damageType;

    @Expose
    List<LatLng> coordinates = new ArrayList<>();

    @Expose
    String areaCode;

    @Expose
    double areaSize;

    @ColumnInfo(index = true)
    @Expose
    DateTime date;

    //##############################################################################################

    public ContractEntity(
            long createdByID,
            String areaCode,
            double areaSize,
            long holderID,
            long expertID,
            List<LatLng> coordinates,
            DateTime date,
            String damageType,
            boolean intial) {
        this(createdByID,areaCode, areaSize, holderID, expertID, coordinates, date, damageType);
        this.initial = intial;
    }

    public ContractEntity(
            long createdByID,
            String areaCode,
            double areaSize,
            long holderID,
            long expertID,
            List<LatLng> coordinates,
            DateTime date,
            String damageType) {
        SopraApp.getAppComponent().inject(this);
        this.areaCode = areaCode;
        this.areaSize = areaSize;
        this.holderID = holderID;
        this.expertID = expertID;
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
        if(initial) return contractEntityRepository.insert(this);
        else if(isChanged) contractEntityRepository.update(this);
        isChanged = false;
        return id;
    }

    @Override
    public void delete() throws ExecutionException, InterruptedException {
        contractEntityRepository.delete(this);
    }

    //##############################################################################################

    @Override
    public ContractEntityRepository getRepository() {
        return contractEntityRepository;
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

    public long getHolderID() {
        return holderID;
    }

    // SETTER ######################################################################################

    public ContractEntity setCoordinates(List<LatLng> coordinates) {
        if(coordinates.equals(this.coordinates)) return this;

        isChanged = true;
        this.coordinates = coordinates;
        return this;
    }

    public ContractEntity setAreaCode(String areaCode) {
        if(areaCode.equals(this.areaCode)) return this;

        isChanged = true;
        this.areaCode = areaCode;
        return this;
    }

    public ContractEntity setDate(DateTime date) {
        if(date.equals(this.date)) return this;

        isChanged = true;
        this.date = date;
        return this;
    }

    public ContractEntity setAreaSize(double date) {
        if(areaSize == this.areaSize) return this;

        isChanged = true;
        this.areaSize = areaSize;
        return this;
    }

    public ContractEntity setHolderID(long holderID) {
        if(holderID == this.holderID) return this;

        isChanged = true;
        this.holderID = holderID;

        return this;
    }

    public ContractEntity setDamageType(String damageType) {
        if(damageType.equals(this.damageType)) return this;

        isChanged = true;
        this.damageType = damageType;
        return this;
    }

    //##############################################################################################

    /**
     * Item holds the state whether selected in the list view (multi-selection).
     */
    @Ignore
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int hashCode() {
        return ("CONTRACT_" + id).hashCode();
    }

    @Override
    public String toString() {
        if(isInitial()) return "";
        return "#" + Math.abs(hashCode());
    }

    //##############################################################################################

    public static final class Builder {
        private String areaCode = "";
        private double areaSize = 0;
        private long holderID = -1;
        private long expertID = -1;

        private List<LatLng> coordinates = new ArrayList<>();
        private DateTime date = DateTime.now();
        private String damageType = "";

        @Inject
        UserHandler userHandler;

        Builder(){
            SopraApp.getAppComponent().inject(this);
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

        public Builder setHolder(UserEntity holder) {
            this.holderID = holder.getID();
            return this;
        }

        public Builder setHolder(long holder) {
            this.holderID = holder;
            return this;
        }

        public Builder setExpert(UserEntity expert) {
            this.expertID = expert.getID();
            return this;
        }

        public Builder setExpert(long expert) {
            this.expertID = expert;
            return this;
        }

        public ContractEntity create() throws NoUserException {
            long createdByID = userHandler.getCurrentUser().getID();
            return new ContractEntity(createdByID, areaCode, areaSize, holderID, expertID, coordinates, date, damageType, true);
        }
    }
}