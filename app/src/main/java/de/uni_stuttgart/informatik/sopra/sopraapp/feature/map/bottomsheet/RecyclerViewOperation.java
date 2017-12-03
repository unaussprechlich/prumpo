package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

/**
 * Interface for basic operation on the recycler view.
 *
 * @param <T>
 */
public interface RecyclerViewOperation<T> {

    /**
     * Adds an item to the recycler view.
     *
     * @param t The item to add
     */
    void add(T t);

    /**
     * Removes an item from the recycler view.
     *
     * @param t The item to remove
     */
    void remove(T t);

    /**
     * Grab the item behind the recycler view
     *
     * @return The item which is responsible for the displayed data.
     */
    T getItem();
}
