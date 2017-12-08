package de.uni_stuttgart.informatik.sopra.sopraapp.app;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseEventBusActivity extends BaseActivity {

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
