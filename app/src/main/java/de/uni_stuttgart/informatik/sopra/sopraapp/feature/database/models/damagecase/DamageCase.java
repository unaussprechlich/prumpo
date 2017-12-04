package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
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

    public List<LatLng> coordinates; // TODO! LatitudeItem
    public String areaCode;
    @ColumnInfo(index = true)
    public Date date;
    public double areaSize;

    public DamageCase(
            String nameDamageCase,
            String namePolicyholder,
            String nameExpert,
            String areaCode,
            double areaSize,
            long ownerID,
            List<LatLng> coordinates,
            Date date) {

        this.nameDamageCase = nameDamageCase;
        this.namePolicyholder = namePolicyholder;
        this.nameExpert = nameExpert;
        this.areaCode = areaCode;
        this.areaSize = areaSize;
        this.ownerID = ownerID;
        this.coordinates = coordinates;
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