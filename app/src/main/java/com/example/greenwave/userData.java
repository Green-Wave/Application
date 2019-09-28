package com.example.greenwave;

public class userData {
    private Double oldLat = null;
    private Double oldLon = null;
    private Long oldTime = null;
    private Double nexTLLat = null;
    private Double nextTLLon = null;
    private Double speed = 0.0;

    public userData(Double newLat, Double newLon, Double tlLat, Double tlLon, Long newTime){
        if(oldLat == null){
            oldLat = newLat;
        }
        if(oldLon == null){
            oldLon = newLon;
        }
        if(oldTime == null){
            oldTime = newTime;
        }
        if(nexTLLat == null){
            nexTLLat = tlLat;
        }
        if(nextTLLon == null){
            nextTLLon = tlLon;
        }
        /*speed = speed(distance(newLat,oldLat, newLon,oldLon), (newTime-oldTime)/1000);
        double distanceToTL = distance(newLat,tlLat, nexTLLat,nextTLLon);
        double timeToTL = speed / distanceToTL;*/
        oldLon = newLon;
        oldLat = newLat;
        oldTime = newTime;
    }

    public double getSpeed(){
        return speed;
    }

    public double getDistanceToTL(){
        if(oldLat != null && nexTLLat != null && oldLon != null && nextTLLon != null){
            return distance(oldLat,nexTLLat, oldLon,nextTLLon);
        }
        return 0;
    }

    public void setLocation(Double newLat, Double newLon, Long newTime){
        if(oldTime  != null && oldLon  != null&& oldLat != null && newLon != null && newLon != null && newTime != null) {
            speed = speed(distance(newLat, oldLat, newLon, oldLon), (newTime - oldTime) / 1000);
        }
        oldLon = newLon;
        oldLat = newLat;
        oldTime = newTime;
    }
    public void setTrafficLight(Double tlLat, Double tlLon){
        nexTLLat = tlLat;
        nextTLLon = tlLon;
    }
    //returns meters
    private static double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        return distance;
    }

    private double speed(double distance, double time){
        System.out.println("distance"+distance);
        System.out.println("time"+time);
        if(distance == 0 || distance ==0.0){
            speed = 0.0;
            return 0.0;
        }else{
            speed = distance/time;
            return distance/time;
        }
    }
}
