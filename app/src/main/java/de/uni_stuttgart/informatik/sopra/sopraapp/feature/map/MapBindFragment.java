package de.uni_stuttgart.informatik.sopra.sopraapp.feature.map;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import com.google.android.gms.maps.MapView;
import dagger.android.support.DaggerFragment;
import de.uni_stuttgart.informatik.sopra.sopraapp.R;


public abstract class MapBindFragment extends DaggerFragment {

    @BindView(R.id.bottom_sheet_container)
    NestedScrollView mBottomSheetContainer;

    @BindView(R.id.mapView)
    MapView mMapView;
    @BindView(R.id.fab_locate)
    FloatingActionButton mFabLocate;
    @BindString(R.string.appbar_title_map)
    String strAppbarTitle;
    @BindString(R.string.prompt_enable_localization)
    String strPromptEnableLocation;

    @BindString(R.string.map_fab_no_gps)
    String strNoLocationDatesFound;
    @BindString(R.string.map_fab_messages_latitude)
    String strLatitude;
    @BindString(R.string.map_fab_messages_longitude)
    String strLongitude;

    @BindDrawable(R.drawable.ic_my_location_black_24dp)
    Drawable currentLocationKnownDrawable;
    @BindDrawable(R.drawable.ic_location_disabled_black_24dp)
    Drawable currentLocationUnknownDrawable;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(strAppbarTitle);
    }

}
