package com.example.greenwave;

public class calculation_suggested_speed {
    private double current_speed;
    private double theoretical_speed;
    private double suggested_speed;
    private boolean green;
    private String suggestion;

    public calculation_suggested_speed(double distance, double current_speed, double time){
        this.current_speed = current_speed;
        theoretical_speed = distance/time;
        this.evaluateSpeed();
        if(time>0){
            this.green =true;
        }
    }

    public void evaluateSpeed(){
        if(theoretical_speed> 7 && theoretical_speed <=30) {
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
