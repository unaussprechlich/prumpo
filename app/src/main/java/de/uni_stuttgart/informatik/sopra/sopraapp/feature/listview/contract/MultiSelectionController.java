package de.uni_stuttgart.informatik.sopra.sopraapp.feature.listview.contract;

/**
 * Interface for specifying the methods a multi selection handler must support.
 *
 * @param <Item> the item of the underlying data
 */
public interface MultiSelectionController<Item> {

    /**
     * Will add the given {@code item} to the list of selected items.
     * This method will also call to update the title bar.
     * <p>
     * If no item remains selected this method will invoke the finish action on the action mode.
     *
     * @param item the contract to add
     */
    void selectItem(Item item);

    /**
     * Starts the action mode if it is not already started at the moment.
     *
     * @param firstItem - the item on which the action mode should start
     */
    void startActionMode(Item firstItem);

    /**
     * Gives information whether the action mode is active at the moment.
     *
     * @return true if action mode started, false else
     */
    boolean isActionModeStarted();

}
