package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.abstractstuff;


import java.util.concurrent.ExecutionException;

public interface   ModelDB<Repository extends AbstractRepository> {
    long getID();
    long getOwnerID();
    long save() throws ExecutionException, InterruptedException;
    Repository getRepository();
    boolean isChanged();
}
