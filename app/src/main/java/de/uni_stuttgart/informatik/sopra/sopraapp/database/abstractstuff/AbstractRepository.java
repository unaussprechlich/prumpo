package de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.CurrentUser;
import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.user.NoUserException;


public abstract class AbstractRepository<Model extends ModelDB, Dao extends IDao<Model>> {

    protected final Dao dao;

    public AbstractRepository(@NonNull Dao dao){
        this.dao = dao;
    }

    protected Dao getDao() {
        return dao;
    }

    public LiveData<List<Model>> getAll(){
        return dao.getAll(getUserId());
    }

    public LiveData<Model> getById(long id){
        return dao.getById(id, getUserId());
    }

    public void update(Model model) {
        AsyncTask.execute(() -> dao.update(model));
    }

    protected long getUserId() {

        try {
            return CurrentUser.get().getID();
        } catch ( NoUserException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // GET ASYNC ###################################################################################

    @SuppressWarnings("unchecked")
    //@SafeVarargs is present in the actual async task .... so I don't care!
    public Model getAsync(long id) throws ExecutionException, InterruptedException{
        return new AbstractRepository.GetAsyncTask<>(dao, getUserId()).execute(id).get();
    }

    private static class GetAsyncTask<Model extends ModelDB, Dao extends IDao<Model>> extends AbstractAsyncTaskT<Long, Dao, Model> {


        public GetAsyncTask(Dao dao, Long userID) {
            super(dao, userID);
        }

        @SafeVarargs
        protected final Model doInBackground(final Long... params) {
            return dao.getByIdDirect(params[0], userID);
        }
    }

    // CREATE ######################################################################################

    @SuppressWarnings("unchecked")
    //@SafeVarargs is present in the actual async task .... so I don't care!
    public void delete(@NonNull Model model) {
        new AbstractRepository.DeleteAsyncTask<>(dao, getUserId()).execute(model);
    }

    private static class DeleteAsyncTask<Model extends ModelDB, Dao extends IDao<Model>> extends AbstractAsyncTask<Model, Dao, Void>{


        public DeleteAsyncTask(Dao dao, Long userID) {
            super(dao, userID);
        }

        @SafeVarargs
        protected final Void doInBackground(final Model... params) {
            dao.delete(params[0]);
            return null;
        }
    }


    // DELETE ######################################################################################

    private static class InsertAsyncTask<Model extends ModelDB, Dao extends IDao<Model>> extends AbstractAsyncTask<Model, Dao, Long> {


        public InsertAsyncTask(Dao dao, Long userID) {
            super(dao, userID);
        }

        @SafeVarargs
        protected final Long doInBackground(final Model... params) {
            return dao.insert(params[0]);
        }

    }

    @SuppressWarnings("unchecked") //@SafeVarargs is present in the actual async task .... so I don't care!
    public Long insert(@NonNull Model model) throws ExecutionException, InterruptedException {
        return new InsertAsyncTask<>(dao, getUserId()).execute(model).get();
    }

    // ABSTRACT TASK ###############################################################################

    abstract static class AbstractAsyncTask<
                Model extends ModelDB,
                Dao extends IDao<Model>,
                Return
            >
            extends AsyncTask<Model, Void, Return> {

        protected Dao dao;
        protected Long userID;

        public AbstractAsyncTask(Dao dao, Long userID) {
            this.dao = dao;
            this.userID = userID;
        }
    }

    protected abstract static class AbstractAsyncTaskT<
            T,
            Dao extends IDao<Return>,
            Return extends ModelDB
            >
            extends AsyncTask<T, Void, Return> {

        protected Dao dao;
        protected Long userID;

        public AbstractAsyncTaskT(Dao dao, Long userID) {
            this.dao = dao;
            this.userID = userID;
        }
    }
}

