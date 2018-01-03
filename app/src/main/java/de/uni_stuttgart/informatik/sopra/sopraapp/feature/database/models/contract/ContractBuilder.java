package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.contract;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;

public final class ContractBuilder {
    private String name = "";
    private String areaCode = "";
    private double areaSize = -1;
    private List<LatLng> coordinates = new ArrayList<>();
    private DateTime date = DateTime.now();

    @Inject UserManager userManager;

    public ContractBuilder() {
        SopraApp.getAppComponent().inject(this);
    }

    public ContractBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ContractBuilder setAreaCode(String areaCode) {
        this.areaCode = areaCode;
        return this;
    }

    public ContractBuilder setAreaSize(double areaSize) {
        this.areaSize = areaSize;
        return this;
    }

    public ContractBuilder setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public ContractBuilder setDate(DateTime date) {
        this.date = date;
        return this;
    }

    public Contract createContract() throws UserManager.NoUserException {
        long ownerID = userManager.getCurrentUser().getID();
        return new Contract(name, areaCode, areaSize, ownerID, coordinates, date, true);
    }
}