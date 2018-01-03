package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.user.User;

/**
 * Typeconverters for Room.
 */
public class Converters {

    @Inject
    Gson gson;

    public Converters() {
        SopraApp.getAppComponent().inject(this);
    }

    //EnumRoles ####################################################################################
    @TypeConverter
    public User.EnumUserRoles convertEnumRoles(String string){
        return User.EnumUserRoles.valueOf(string);
    }

    @TypeConverter
    public String convertEnumRoles(User.EnumUserRoles role){
        return role.toString();
    }

    //DateTime #####################################################################################

    @TypeConverter
    public String converteLocalDateTime(DateTime date) {
        return date.toString(DateTimeFormat.fullDate());
    }

    @TypeConverter
    public DateTime converteLocalDateTime(String date) {
        return DateTime.parse(date, DateTimeFormat.fullDate());
    }

    //List<LatLng> #################################################################################

    @TypeConverter
    public List<LatLng> convertListOfLatLng(String value) {
        if (value.equals("")) return new ArrayList<>();
        ArrayList<String> pointsAsString = gson.fromJson(value, new TypeToken<ArrayList<String>>(){}.getType());
        ArrayList<LatLng> points = new ArrayList<>();
        for(String str : pointsAsString){
            String[] spl = str.split("-");
            points.add(new LatLng(Double.valueOf(spl[0]), Double.valueOf(spl[1])));
        }
        return points;
    }

    @TypeConverter
    public String convertListOfLatLng(List<LatLng> value) {
        if(value == null) return "";
        ArrayList<String> asString = new ArrayList<>();
        for(LatLng latLng : value){
            asString.add(latLng.latitude + "-" + latLng.longitude);
        }
        return gson.toJson(asString);
    }

    //List<Long> ###################################################################################

    @TypeConverter
    public List<Long> convertListOfLong(String value) {
        if (value.equals("")) return new ArrayList<>();
        ArrayList<String> asString = gson.fromJson(value, new TypeToken<ArrayList<String>>(){}.getType());
        return asString.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @TypeConverter
    public String convertListOfLong(List<Long> value) {
        if(value == null) return "";
        return gson.toJson(value.stream()
                .map(Object::toString)
                .toArray());
    }

}
