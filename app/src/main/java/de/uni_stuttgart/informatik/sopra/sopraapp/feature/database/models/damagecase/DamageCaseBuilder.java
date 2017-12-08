package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.damagecase;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.authentication.UserManager;

public class DamageCaseBuilder {
    private String nameDamageCase = "";
    private String namePolicyholder = "";
    private String nameExpert = "";
    private String areaCode = "";
    private double areaSize = 0;
    private long ownerID = 0;
    private List<LatLng> coordinates = new ArrayList<>();
    private DateTime date = DateTime.now();

    @Inject UserManager userManager;

    public DamageCaseBuilder() {
        SopraApp.getAppComponent().inject(this);
    }

    public DamageCaseBuilder setNameDamageCase(String nameDamageCase) {
        this.nameDamageCase = nameDamageCase;
        return this;
    }

    public DamageCaseBuilder setNamePolicyholder(String namePolicyholder) {
        this.namePolicyholder = namePolicyholder;
        return this;
    }

    public DamageCaseBuilder setNameExpert(String nameExpert) {
        this.nameExpert = nameExpert;
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

    public DamageCaseBuilder setOwnerID(long ownerID) {
        this.ownerID = ownerID;
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
        this.ownerID = userManager.getCurrentUser().getID();
        return new DamageCase(nameDamageCase, namePolicyholder, nameExpert, areaCode, areaSize, ownerID, coordinates, date, true);
    }
}