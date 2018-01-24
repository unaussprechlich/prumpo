package de.uni_stuttgart.informatik.sopra.sopraapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.contract.ContractEntityDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseEntityDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntity;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserEntityDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

/**
 * This class represents the Database. If you add a new model provide it
 * as entity to the {@link Database} annotation and add it's {@link Dao}
 * as a abstract method.
 * Don't forget to increase the version number in the {@link Database}
 * annotation if you change any of the models.
 */
@ApplicationScope
@Database(entities = {UserEntity.class, DamageCaseEntity.class, ContractEntity.class}, version = 19)
@TypeConverters({Converters.class})
public abstract class DatabaseManager extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract DamageCaseDao damageCaseDao();
    public abstract ContractDao contractDao();

    public abstract UserEntityDao userEntityDao();
    public abstract DamageCaseEntityDao damageCaseEntityDao();
    public abstract ContractEntityDao contractEntityDao();

}
