package de.uni_stuttgart.informatik.sopra.sopraapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.UserDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.UserDao;

@Database(entities = {UserDB.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {


    public abstract UserDao userDao();

    /** The only instance */
    private static DatabaseManager INSTANCE;

    /**
     * Gets the singleton instance of DatabaseManager.
     *
     * @param context The context.
     * @return The singleton instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                        .databaseBuilder(context, DatabaseManager.class, "SopaApp.db")
                        .build();
            INSTANCE.populateInitialData();
        }

        return INSTANCE;
    }

    /**
     * Switches the internal implementation with an empty in-memory database.
     *
     * Only use it for testing, data is not stored permanently !!!!!!
     *
     * @param context The context.
     */
    @VisibleForTesting
    public static void switchToInMemory(Context context) {
        INSTANCE = Room
                    .inMemoryDatabaseBuilder(context.getApplicationContext(),
                            DatabaseManager.class)
                    .build();
    }

    /**
     * Inserts the dummy data into the database if it is currently empty.
     */
    private void populateInitialData() {
        if (userDao().count() == 0) {
            UserDB user = new UserDB();

            try { // some fancy wrapper stuff :3
                beginTransaction();

                for (String s : UserDB.CREDENTIALS) {
                    String[] split = s.split(":");
                    user.name = split[0];
                    user.password = split[1];
                    userDao().insert(user);
                }

                setTransactionSuccessful();

            } finally {
                // If the transaction wasn't successful, changes will be reverted!
                endTransaction();
            }
        }
    }

}
