package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.abstractstuff.AbstractRepository;

@Singleton
public class UserRepository  extends AbstractRepository<User, UserDao>{

    @Inject
    public UserRepository(@NonNull UserDao dao) {
        super(dao);
    }
}
