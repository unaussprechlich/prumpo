package de.uni_stuttgart.informatik.sopra.sopraapp.damagecases;

import java.util.ArrayList;

/**
 * Area Created by Alexander on 10.11.2017.
 * Description:
 **/
public class Area extends AbstractDataModelExportable{

    private String name;
    private ArrayList<GeoJsonPoint> geoJsonPoints = new ArrayList<>();
    private SurfaceArea surfaceArea;
    private ArrayList<InsuranceContract> insuranceContracts = new ArrayList<>();


}
