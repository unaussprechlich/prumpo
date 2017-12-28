package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;

public interface BottomSheet extends BottomSheetListAdapter.ItemCountListener {

    enum TYPE {
        DAMAGE_CASE, DAMAGE_CASE_NEW
    }

    void show();

    void close();

    TYPE getType();

}