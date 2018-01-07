package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;

@ApplicationScope
public class UserRepository  extends AbstractRepository<User, UserDao>{

    @Inject
    public UserRepository(@NonNull UserDao dao) {
        super(dao);
    }

    public LiveData<User> getByEmail(String email){
        return getDao().getByEmail(email);
    }

    @Override
    protected long getUserId() {
        return 0;
    }
}
