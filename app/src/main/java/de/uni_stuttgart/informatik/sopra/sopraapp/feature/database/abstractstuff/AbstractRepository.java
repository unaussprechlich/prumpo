package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;


public abstract class AbstractRepository<Model extends ModelDB, Dao extends IDao<Model>> {

    private final Dao dao;
    private final UserManager userManager;

    protected Dao getDao() {
        return dao;
    }

    public AbstractRepository(@NonNull Dao dao, @NonNull UserManager userManager){
        this.dao = dao;
        this.userManager = userManager;
    }

    public LiveData<List<Model>> getAll(){
        return dao.getAll();
    }

    public LiveData<Model> getById(long id){
        return dao.getById(id);
    }

    // COUNT #######################################################################################

    public boolean isEmpty() throws ExecutionException, InterruptedException {
        return count() > 0;
    }

    @SuppressWarnings("unchecked") //@SafeVarargs is present in the actual async task .... so I don't care!
    public int count() throws ExecutionException, InterruptedException {
        return new CountAsyncTask<>(dao).execute().get();
    }

    private static class CountAsyncTask<Model extends ModelDB, Dao extends IDao<Model>> extends AbstractAsyncTask<Model, Dao, Integer> {

        public CountAsyncTask(@NonNull Dao dao) {
            super(dao);
        }

        @SafeVarargs
        protected final Integer doInBackground(final Model... params) {
            return dao.count();
        }

    }


    // CREATE ######################################################################################


    @SuppressWarnings("unchecked") //@SafeVarargs is present in the actual async task .... so I don't care!
    public Long insert(@NonNull Model model) throws ExecutionException, InterruptedException {
        return new InsertAsyncTask<>(dao).execute(model).get();

        //I WOULD LIKE TO ... BUT INTELLIJ DOES NOT LIKE THIS .... MEMORY LEAKS ... BLABLABLA
        //        return new AbstractAsyncTask<Model, Dao, Long>(dao) {
        //            protected final Long doInBackground(Model[] params) {
        //                return dao.insert(params[0]);
        //            }
        //        }.execute(model).get();
    }

    private static class InsertAsyncTask<Model extends ModelDB, Dao extends IDao<Model>> extends AbstractAsyncTask<Model, Dao, Long> {

        //WHYYYYYYYY?!?!?!??!?!?!?! f*cking java ... use your brain
        public InsertAsyncTask(@NonNull Dao dao) {
            super(dao);
        }

        @SafeVarargs
        protected final Long doInBackground(final Model... params) {
            return dao.insert(params[0]);
        }

    }

    // DELETE ######################################################################################

    @SuppressWarnings("unchecked") //@SafeVarargs is present in the actual async task .... so I don't care!
    public void delete(@NonNull Model model){
        new AbstractRepository.DeleteAsyncTask<>(dao).execute(model);
    }

    private static class DeleteAsyncTask<Model extends ModelDB, Dao extends IDao<Model>> extends AbstractAsyncTask<Model, Dao, Void>{

        public DeleteAsyncTask(@NonNull Dao dao) {
            super(dao);
        }

        @SafeVarargs
        protected final Void doInBackground(final Model... params) {
            dao.delete(params[0]);
            return null;
        }

    }

    // ABSTRACT TASK ###############################################################################

    abstract static class AbstractAsyncTask<
                Model extends ModelDB,
                Dao extends IDao<Model>,
                Return
            >
            extends AsyncTask<Model, Void, Return> {

        protected Dao dao;

        public AbstractAsyncTask(Dao dao) {
            this.dao = dao;
        }
    }
}

