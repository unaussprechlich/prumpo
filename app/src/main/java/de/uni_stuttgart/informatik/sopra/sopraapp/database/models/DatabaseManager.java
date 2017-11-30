package de.uni_stuttgart.informatik.sopra.sopraapp.database.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCase;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.damagecase.DamageCaseDao;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.User;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.UserDao;


@Database(entities = {User.class, DamageCase.class}, version = 2)
public abstract class DatabaseManager extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract DamageCaseDao damageCaseDao();

}
