package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.UserDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;


@ApplicationScope
@Database(entities = {User.class, DamageCase.class}, version = 3)
public abstract class DatabaseManager extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract DamageCaseDao damageCaseDao();

}
