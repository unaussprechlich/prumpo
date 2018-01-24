package de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff;


import java.util.concurrent.ExecutionException;

public interface ModelEntityDB<Repository extends AbstractEntityRepository>  extends ModelDB{

    long save() throws ExecutionException, InterruptedException;
    void delete() throws ExecutionException, InterruptedException;
    Repository getRepository();
    boolean isChanged();
    boolean isInitial();


    default ModelEntityDB getEntity(){
        return null;
    }
}
