package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.area;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * Represents one record of the User table.
 */
@Entity(tableName = AreaDB.TABLE_NAME)
public class AreaDB{


    public static final String TABLE_NAME = "area";

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    public long id;

    private ArrayList<LatLng> positions = new ArrayList<>();

}