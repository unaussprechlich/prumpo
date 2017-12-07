package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

/**
 * This class represents the Database. If you add a new model provide it
 * as entity to the {@link Database} annotation and add it's {@link Dao}
 * as a abstract method.
 * Don't forget to increase the version number in the {@link Database}
 * annotation if you change any of the models.
 */
@ApplicationScope
@Database(entities = {User.class, DamageCase.class}, version = 9)
@TypeConverters({Converters.class})
public abstract class DatabaseManager extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract DamageCaseDao damageCaseDao();

}
