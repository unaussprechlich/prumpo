package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;


/**
 * Represents one record of the User table.
 */
@Entity(tableName = User.TABLE_NAME)
public final class User implements ModelDB<UserRepository> {

    public static final String TABLE_NAME = "user";

    @Ignore @Inject UserRepository userRepository;
    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    /** The unique ID of the user. */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = BaseColumns._ID)
    long id;

    // Don't change the names without updating the queries, they are used as column name!
    @ColumnInfo(index = true)
    String name;
    @ColumnInfo(index = true)
    String email;
    String password;
    @ColumnInfo(index = true)
    public long ownerID = 0;
    public int profilePicture = 0;

    @ColumnInfo(index = true)
    EnumUserRoles role;

    public enum EnumUserRoles{
        ADMIN, BAUER, GUTACHTER, NULL;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public long getOwnerID() {return ownerID;}

    @Override
    public long save() throws ExecutionException, InterruptedException {
        if(initial) return userRepository.insert(this);
        else if(isChanged) userRepository.update(this);
        isChanged = false;
        return id;
    }

    public User setProfilePicture(int profilePicture) {
        if(profilePicture > 27 || profilePicture < 0) throw new IndexOutOfBoundsException();
        isChanged = true;
        this.profilePicture = profilePicture;
        return this;
    }

    public User setName(String name) {
        isChanged = true;
        this.name = name;
        return this;
    }

    public User setEmail(String email) {
        isChanged = true;
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        isChanged = true;
        this.password = password;
        return this;
    }

    public User setRole(EnumUserRoles role) {
        isChanged = true;
        this.role = role;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public EnumUserRoles getRole() {
        return role;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    @Override
    public boolean isChanged() {
        return isChanged;
    }

    @Override
    public boolean isInitial() {
        return initial;
    }

    @Override
    public String toString() {
        return name + " #" + id;
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

        public User create() {
            return new User(name, password, email, role, true);
        }
    }

    public User(String name, String password, String email, EnumUserRoles role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        SopraApp.getAppComponent().inject(this);
    }

    public User(String name, String password, String email, EnumUserRoles role, boolean initial) {
        this(name, password, email, role);
        this.initial = initial;
    }
}