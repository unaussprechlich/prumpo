package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

@ApplicationScope
public class UserRepository  extends AbstractRepository<User, UserDao>{

    @Inject
    public UserRepository(@NonNull UserDao dao) {
        super(dao);
    }

    public void insertDummyIfNotExist(){
        try{
            //TODO REMOVE THIS

            if(this.getByEmailAsync("dummy@dummy.net") == null){
                Long test = this.insert(new User.Builder()
                        .setEmail("dummy@dummy.net")
                        .setPassword("dummy")
                        .setName("Mister Dummy")
                        .setRole(User.EnumUserRoles.ADMIN)
                        .build());
                Log.e("UserRepository", "Dummy inserted!  " + test);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    // GET ASYNC ###################################################################################

    @SuppressWarnings("unchecked")
    //@SafeVarargs is present in the actual async task .... so I don't care!
    public User getByEmailAsync(String email) throws ExecutionException, InterruptedException{
        return new GetAsyncTaskEmail(dao, getUserId()).execute(email).get();
    }

    private static class GetAsyncTaskEmail extends AbstractAsyncTaskT<String, UserDao, User> {

        public GetAsyncTaskEmail(UserDao dao, Long userID) {
            super(dao, userID);
        }

        protected final User doInBackground(final String... params) {
            return dao.getByEmailDirect(params[0]);
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
