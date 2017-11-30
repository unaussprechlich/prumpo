package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff.ModelDB;


/**
 * Represents one record of the User table.
 */
@Entity(tableName = User.TABLE_NAME)
public class User implements ModelDB {

    public static final String TABLE_NAME = "user";

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    public long id;

    // Don't change the names without updating the queries, they are used as column name!
    @ColumnInfo(index = true)
    public String name;

    public String password;

    public User(long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }


    @Override
    public long getID() {
        return id;
    }
}