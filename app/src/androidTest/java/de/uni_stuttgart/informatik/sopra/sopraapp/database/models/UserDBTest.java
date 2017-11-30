package de.uni_stuttgart.informatik.sopra.sopraapp.database.models;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UserDBTest {
    private DatabaseManager db;

    @Before
    public void createDatabase() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(),
                DatabaseManager.class).build();
    }

    @After
    public void closeDatabase() throws IOException {
        db.close();
    }

    @Test
    public void insertAndCount() {
        assertThat(db.userDao().count(), is(0));
        UserDB user = new UserDB();
        user.name = "abc";
        db.userDao().insert(user);
        assertThat(db.userDao().count(), is(1));
    }

    @Test
    public void insertAnd() {
        UserDB user = new UserDB();
        user.name = "test";
        long id = db.userDao().insert(user);
        UserDB userDB = db.userDao().getById(id);
        assertThat(userDB.name, is(user.name));
    }

}