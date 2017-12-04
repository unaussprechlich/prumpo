package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;

public class Converters {

    @Inject
    Gson gson;


    public Converters() {
        SopraApp.getAppComponent().inject(this);
    }

    @TypeConverter
    public User.EnumUserRoles convertEnumRoles(String string){
        return User.EnumUserRoles.valueOf(string);
    }

    @TypeConverter
    public String convertEnumRoles(User.EnumUserRoles role){
        return role.toString();
    }

    @TypeConverter
    public String converteLocalDateTime(Date date){
        return date.toString();
    }

    @TypeConverter
    public Date converteLocalDateTime(String date){
        return new Date(date);
    }

    @TypeConverter
    public List<LatLng> convertArrayList(String value){
        ArrayList<String> pointsAsString = gson.fromJson(value, new TypeToken<ArrayList<String>>(){}.getType());
        ArrayList<LatLng> points = new ArrayList<>();
        for(String str : pointsAsString){
            String[] spl = str.split("-");
            points.add(new LatLng(Double.valueOf(spl[0]), Double.valueOf(spl[1])));
        }
        return points;
    }

    @TypeConverter
    public String convertArrayList(List<LatLng> value){
        ArrayList<String> asString = new ArrayList<>();
        for(LatLng latLng : value){
            asString.add(latLng.latitude + "-" + latLng.latitude);
        }
        return gson.toJson(asString);
    }

}
