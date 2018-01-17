package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;

@ApplicationScope
public class UserRepository  extends AbstractRepository<User, UserDao>{

    @Inject
    public UserRepository(@NonNull UserDao dao) {
        super(dao);
        try{
            //TODO REMOVE THIS
            if(this.getAsync(1) == null)
                this.insert(new User.Builder()
                        .setEmail("dummy@dummy.net")
                        .setPassword("dummy")
                        .setName("Mister Dummy")
                        .setRole(User.EnumUserRoles.ADMIN)
                        .build());
            Log.e("UserRepository", "Dummy inserted!");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public LiveData<User> getByEmail(String email){
        return getDao().getByEmail(email);
    }

    @Override
    protected long getUserId() {
        return 0;
    }
}
