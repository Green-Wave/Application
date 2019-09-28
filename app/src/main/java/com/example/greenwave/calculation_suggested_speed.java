package com.example.greenwave;

public class calculation_suggested_speed {
    private double current_speed;
    private double theoretical_speed;
    private double suggested_speed;
    private boolean green;
    private double distance;
    private double time;
    private String suggestion;

    public calculation_suggested_speed(double dist, double current_speed, double tim){
        this.current_speed = current_speed;
        setDist(dist);
        setTime(tim);
        theoretical_speed = distance/time;
        this.evaluateSpeed();
        if(time>0){
            this.green =true;
        }
    }

    public void setSpeed(double speed){
        current_speed =speed;
    }
    public void setDist(double dist){
        distance =dist;
    }
    public void setTime(double tim){
        time =tim;
        if(time >0) {
            this.green = true;
        }else{
            this.green = false;
            time = time * (-1);
        }
    }

    public void evaluateSpeed(){
        theoretical_speed = distance/time;
        System.out.println(theoretical_speed);
        if((theoretical_speed> 2 && !this.green) || (theoretical_speed <=8.3333 && this.green)) {
            // green
            if(this.green){
                if (theoretical_speed > current_speed) {
                    this.suggestion = "speed up!";
                    this.suggested_speed = theoretical_speed;
                } else {
                    this.suggestion = "keep the pace!";
                }
            }else {
                if (theoretical_speed < current_speed) {
                    this.suggestion = "slow down!";
                    this.suggested_speed = theoretical_speed;
                } else {
                    this.suggestion = "keep the pace!";
                }
            }
    }else {
            this.suggestion= "Hopeless";
            this.suggested_speed = current_speed;
        }
    }

    public double getSuggested_speed(){
        return this.suggested_speed;
    }

    public String getSuggestion(){
        return this.suggestion;
    }
}
