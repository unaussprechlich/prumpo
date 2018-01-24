package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

@ApplicationScope
public class UserRepository  extends AbstractRepository<User, UserDao>{


    @Inject
    public UserRepository(@NonNull UserDao dao) {
        super(dao);
    }


    public LiveData<List<User>> getAll(){
        return dao.getAllBypass();
    }

    public LiveData<User> getById(long id){
        return dao.getByIdBypass(id);
    }
}
