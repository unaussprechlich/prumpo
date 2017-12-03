package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.ModelDB;


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
    @ColumnInfo(index = true)
    public String email;
    public String password;

    @ColumnInfo(index = true)
    public EnumUserRoles role;

    public enum EnumUserRoles{
        ADMIN, BAUER, GUTACHTER, NULL;
    }

    public static class Builder {

        private String name;
        private String password;
        private String email;
        private User.EnumUserRoles role;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setRole(User.EnumUserRoles role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(name, password, email, role);
        }
    }

    public User(String name, String password, String email, EnumUserRoles role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public long getOwnerID() {return getID();}



}