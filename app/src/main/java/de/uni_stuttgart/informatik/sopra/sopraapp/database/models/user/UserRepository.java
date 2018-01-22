package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractRepository;

public class UserRepository  extends AbstractRepository<User, UserDao>{


    public UserRepository(@NonNull UserDao dao) {
        super(dao);
    }

    public void insertDummyIfNotExist(){
        try{

            if(this.getByEmailAsync("dummy@dummy.net") == null){
                Long test = this.insert(new User.Builder()
                        .setEmail("dummy@dummy.net")
                        .setPassword("dummy")
                        .setName("Mister Dummy")
                        .setRole(User.EnumUserRoles.ADMIN)
                        .create());
                Log.e("UserRepository", "Dummy inserted!  " + test);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    // GET ASYNC ###################################################################################

    @SuppressWarnings("unchecked")
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
