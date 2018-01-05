package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;

public final class DamageCaseBuilder {
    private String name = "";
    private long contractID = -1;
    private long expertID = -1;
    private String areaCode = "";
    private double areaSize = -1;
    private List<LatLng> coordinates = new ArrayList<>();
    private DateTime date = DateTime.now();

    @Inject UserManager userManager;

    public DamageCaseBuilder() {
        SopraApp.getAppComponent().inject(this);
    }

    public DamageCaseBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public DamageCaseBuilder setContractID(long contractID) {
        this.contractID = contractID;
        return this;
    }

    public DamageCaseBuilder setExpertID(long expertID) {
        this.expertID = expertID;
        return this;
    }

    public DamageCaseBuilder setAreaCode(String areaCode) {
        this.areaCode = areaCode;
        return this;
    }

    public DamageCaseBuilder setAreaSize(double areaSize) {
        this.areaSize = areaSize;
        return this;
    }

    public DamageCaseBuilder setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public DamageCaseBuilder setDate(DateTime date) {
        this.date = date;
        return this;
    }

    public DamageCase create() throws UserManager.NoUserException {
        long ownerID = userManager.getCurrentUser().getID();
        return new DamageCase(name, expertID, contractID, areaCode, areaSize, ownerID, coordinates, date, true);
    }
}