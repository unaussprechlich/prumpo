package de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff;


public interface ModelDB<T extends ModelEntityDB> {

    long getID();
    public T getEntity();
}
