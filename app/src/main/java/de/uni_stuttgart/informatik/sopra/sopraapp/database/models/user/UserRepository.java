package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

@ApplicationScope
public class UserRepository  extends AbstractRepository<User, UserDao>{


    @Inject
    public UserRepository(@NonNull UserDao dao) {
        super(dao);
    }

}
