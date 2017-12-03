package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff.AbstractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;

@ApplicationScope
public class UserRepository  extends AbstractRepository<User, UserDao>{

    @Inject
    public UserRepository(@NonNull UserDao dao, @NonNull UserManager userManager) {
        super(dao, userManager);
        try {
            this.insert(
                    new User.Builder()
                            .setName("Test test")
                            .setPassword("test")
                            .setEmail("test@test.test")
                            .setRole(User.EnumUserRoles.ADMIN)
                            .build());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<User> getByEmail(String email){
        return getDao().getByEmail(email);
    }
}
