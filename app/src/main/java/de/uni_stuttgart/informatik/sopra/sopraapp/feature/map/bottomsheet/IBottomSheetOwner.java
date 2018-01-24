package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.bottomsheet;


import android.content.Context;
import android.support.v4.widget.NestedScrollView;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.abstractstuff.ModelDB;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.map.SopraMap;

public interface IBottomSheetOwner{

    NestedScrollView getNestedScrollView();
    SopraMap getSopraMap();
    Context getContext();
    LockableBottomSheetBehaviour getLockableBottomSheetBehaviour();
    <Model extends ModelDB> void openBottomSheet(Class<Model> clazz);

}
