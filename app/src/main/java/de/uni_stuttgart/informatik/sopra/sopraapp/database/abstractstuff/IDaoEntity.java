package de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

@Dao
public interface IDaoEntity<T extends ModelEntityDB> extends IDao<T>{

    /**
     * Update the userDB. The userDB is identified by the row ID.
     *
     * @param t         The userDB to update.
     */
    @Update
    void update(T t);

    /**
     * Creates a new table entry.
     *
     * @param t     The value to insert.
     *
     * @return      The row ID of the newly inserted entry.
     */
    @Insert
    long insert(T t);

    @Delete
    void delete(T t);

}
