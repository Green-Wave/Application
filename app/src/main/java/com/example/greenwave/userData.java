package com.example.greenwave;

public class userData {
    private Double oldLat,oldLon,oldEl;
    private double newLat,newLon,newEl;
    private double speed;

    public userData(double lat1, double lat2, double lon1,
                    double lon2, double el1, double el2, double tlLat, double tlLon, double tlEl){
        if(oldLat == null){
            oldLat = lat1;
        }else{
            oldLat = lat2;
        }
        if(oldLon == null){
            oldLon = lon1;
        }else{
            oldLon = lon2;
        }
        if(oldEl == null){
            oldEl = el1;
        }else{
            oldEl =el2;
        }
        newLat = lat1;
        newLon = lon1;
        newEl = el1;
        speed = speed(distance(newLat,oldLat, newLon,oldLon,newEl,oldEl),2);
        double timeToTL = speed / distance(newLat,tlLat, newLon,tlLat,newEl,tlEl);
    }

    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
    private static double speed(double distance, double time){
        return distance/time;
    }
}
