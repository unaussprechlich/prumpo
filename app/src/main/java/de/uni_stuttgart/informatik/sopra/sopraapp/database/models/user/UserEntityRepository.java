package de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.AbstractEntityRepository;
import de.uni_stuttgart.informatik.sopra.sopraapp.dependencyinjection.scopes.ApplicationScope;

@ApplicationScope
public class UserEntityRepository extends AbstractEntityRepository<UserEntity, UserEntityDao> {

    @Inject
    public UserEntityRepository(@NonNull UserEntityDao dao) {
        super(dao);
    }

    public void insertDummyIfNotExist(){
        try{

            if(this.getByEmailAsync("dummy@dummy.net") == null){
                Long test = this.insert(new UserEntity.Builder()
                        .setEmail("dummy@dummy.net")
                        .setPassword("dummy")
                        .setName("Mister Dummy")
                        .setRole(UserEntity.EnumUserRoles.ADMIN)
                        .create());
                Log.e("UserRepository", "Dummy inserted!  " + test);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    // GET ASYNC ###################################################################################

    @SuppressWarnings("unchecked")
    public UserEntity getByEmailAsync(String email) throws ExecutionException, InterruptedException{
        return new GetAsyncTaskEmail(dao, getUserId()).execute(email).get();
    }

    private static class GetAsyncTaskEmail extends AbstractAsyncTaskT<String, UserEntityDao, UserEntity> {

        public GetAsyncTaskEmail(UserEntityDao dao, Long userID) {
            super(dao, userID);
        }

        protected final UserEntity doInBackground(final String... params) {
            return dao.getByEmailDirect(params[0]);
        }
    }

    public LiveData<UserEntity> getByEmail(String email){
        return getDao().getByEmail(email);
    }

    @Override
    protected long getUserId() {
        return 0;
    }
}
