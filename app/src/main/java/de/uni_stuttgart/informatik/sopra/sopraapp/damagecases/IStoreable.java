package de.uni_stuttgart.informatik.sopra.sopraapp.damagecases;

/**
 * IStoreable Created by Alexander on 10.11.2017.
 * Description:
 **/
public interface IStoreable<T extends IStoreable> {

    DBEntry serialize();
    T deserialize(DBEntry data);

}
