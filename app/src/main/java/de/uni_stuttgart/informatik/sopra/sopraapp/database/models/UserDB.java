package de.uni_stuttgart.informatik.sopra.sopraapp.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;


/**
 * Represents one record of the UserDB table.
 */
@Entity(tableName = UserDB.TABLE_NAME)
public class UserDB{

    //TODO: remove thizzzzzz!
    public static final String[] CREDENTIALS = {
            "user1@stuttgart.de:pw1",
            "user2@stuttgart.de:pw2"
    };

    public static final String TABLE_NAME = "user";

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    public long id;

    //PLZZZ don't change the names without updating the querys, they are used as column name
    @ColumnInfo(index = true)
    public String name;

    public String password;

    @Ignore
    public String willBeIgnoredByDatabase;

}