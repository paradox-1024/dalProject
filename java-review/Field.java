package com.phoebe;
//some code from  lab1
public class Field {
    //instance variables
    public double xpos, ypos, width, height;

    public Field() {

    }
    //constructor
    public Field(double xpos, double ypos, double width, double height) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.width = width;
        this.height = height;
    }
    //getter and setter
    public double getX() {
        return xpos;
    }

    public void setX(double xpos) {
        this.xpos = xpos;
    }

    public double getY() {
        return ypos;
    }

    public void setY(double ypos) {
        this.ypos = ypos;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    //toString method
    public String toString(){
        return "[xpos= " +xpos+","+"ypos= " + ypos+"] width: " +
                width+",height: "+height;
    }



}
