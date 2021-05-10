package com.phoebe;
//some code from  lab1
public class Ball {
    //instance variables
    public double bx, by;

    public Ball() {

    }
    //constructor
    public Ball(double bx, double by) {
        this.bx = bx;
        this.by = by;

    }
    //getter and setter
    public double getX() {
        return bx;
    }

    public void setX(double bx) {
        this.bx = bx;
    }

    public double getY() {
        return by;
    }

    public void setY(double by) {
        this.by =by;
    }
    //tostring method
    public String toString(){

        return "[xpos= " +bx+","+"ypos= " + by+"]";
    }


}
