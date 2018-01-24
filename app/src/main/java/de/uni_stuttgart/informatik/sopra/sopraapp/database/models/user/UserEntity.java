package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import com.google.gson.annotations.Expose;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelEntityDB;


/**
 * Represents one record of the UserEntity table.
 */
@Entity(tableName = UserEntity.TABLE_NAME)
public final class UserEntity implements ModelEntityDB<UserEntityRepository> {

    public static final String TABLE_NAME = "userEntity";

    @Ignore @Inject UserEntityRepository userRepository;

    @Ignore private boolean isChanged = false;
    @Ignore private boolean initial = false;

    //##############################################################################################

    /** The unique ID of the userEntity. */
    @PrimaryKey(autoGenerate = true)
    @Expose
    @ColumnInfo(index = true, name = BaseColumns._ID)
    public long id;

    // Don't change the names without updating the queries, they are used as column name!
    @Expose
    @ColumnInfo(index = true)
    String name;

    @Expose
    @ColumnInfo(index = true)
    String email;

    String password;

    public int profilePicture = 0;

    @Expose
    @ColumnInfo(index = true)
    EnumUserRoles role;

    //##############################################################################################

    public enum EnumUserRoles{
        ADMIN, BAUER, GUTACHTER, NULL;
    }

    //##############################################################################################

    @Override
    public long save() throws ExecutionException, InterruptedException {
        if(initial) return userRepository.insert(this);
        else if(isChanged) userRepository.update(this);
        isChanged = false;
        return id;
    }

    @Override
    public void delete() throws ExecutionException, InterruptedException {
        userRepository.delete(this);
    }

    //##############################################################################################


    public UserEntity setProfilePicture(int profilePicture) {
        if(profilePicture > 27 || profilePicture < 0) throw new IndexOutOfBoundsException();
        isChanged = true;
        this.profilePicture = profilePicture;
        return this;
    }

    public UserEntity setName(String name) {
        isChanged = true;
        this.name = name;
        return this;
    }

    public UserEntity setEmail(String email) {
        isChanged = true;
        this.email = email;
        return this;
    }

    public UserEntity setPassword(String password) {
        isChanged = true;
        this.password = password;
        return this;
    }

    public UserEntity setRole(EnumUserRoles role) {
        isChanged = true;
        this.role = role;
        return this;
    }

    //##############################################################################################

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
    public long getID() {
        return id;
    }

    //##############################################################################################

    @Override
    public UserEntityRepository getRepository() {
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
    public int hashCode() {
        return ("USER_" + id).hashCode();
    }


    @Override
    public String toString() {
        if(isInitial()) return name;
        return name + " #" + Math.abs(hashCode());
    }

    //##############################################################################################

    public static class Builder {

        private String name;
        private String password;
        private String email;
        private UserEntity.EnumUserRoles role;

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

        public Builder setRole(UserEntity.EnumUserRoles role) {
            this.role = role;
            return this;
        }

        public UserEntity create() {
            return new UserEntity(name, password, email, role, true);
        }
    }

    public UserEntity(String name, String password, String email, EnumUserRoles role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        SopraApp.getAppComponent().inject(this);
    }

    public UserEntity(String name, String password, String email, EnumUserRoles role, boolean initial) {
        this(name, password, email, role);
        this.initial = initial;
    }
}