package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import javax.inject.Inject;

import de.uni_stuttgart.informatik.sopra.sopraapp.app.SopraApp;
import de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.area.Point;
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
    public ArrayList<Point> convertArrayList(String value){
        ArrayList<String> pointsAsString = gson.fromJson(value, new TypeToken<ArrayList<String>>(){}.getType());
        ArrayList<Point> points = new ArrayList<>();
        for(String str : pointsAsString){
            points.add(Point.fromString(str));
        }
        return points;
    }

    @TypeConverter
    public String convertArrayList(ArrayList<Point> value){
        ArrayList<String> pointsAsString = new ArrayList<>();
        for(Point point : value){
            pointsAsString.add(point.toString());
        }
        return gson.toJson(pointsAsString);
    }

}
