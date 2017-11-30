package de.uni_stuttgart.informatik.sopra.sopraapp.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.uni_stuttgart.informatik.sopra.sopraapp.database.models.area.Point;

class Converters {


    @TypeConverter
    public static ArrayList<Point> toArrayListPoints(String value){
        ArrayList<String> pointsAsString = new Gson().fromJson(value, new TypeToken<ArrayList<String>>(){}.getType());
        ArrayList<Point> points = new ArrayList<>();
        for(String str : pointsAsString){
            points.add(Point.fromString(str));
        }
        return points;
    }

    @TypeConverter
    public static String fromArrayListPoints(ArrayList<Point> value){
        ArrayList<String> pointsAsString = new ArrayList<>();
        for(Point point : value){
            pointsAsString.add(point.toString());
        }

        return new Gson().toJson(pointsAsString);
    }



}
