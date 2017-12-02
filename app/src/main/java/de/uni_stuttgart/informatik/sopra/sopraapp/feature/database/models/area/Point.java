package de.uni_stuttgart.informatik.sopra.sopraapp.feature.database.models.area;

public class Point {
    private double latitude  = 0f;
    private double longitude = 0f;

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude + ":" + longitude;
    }

    public static Point fromString(String str){
        return new Point(Double.valueOf(str.substring(0, str.indexOf(':'))), Double.valueOf(str.substring(str.indexOf(':') + 1)));
    }

    public double getLatitude() {return latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;}
    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}

}
