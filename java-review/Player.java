package com.phoebe;
import java.util.*;
public class Player {
    //instance variables
    private String name;


    //constructor

    public Player() {
    }

    ;

    public Player(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public void kick(Field f, Ball b, Double d, double r) {

        double x1 = b.getX();
        double y1 = b.getY();
        double rr = Math.toRadians(r);
        double dx = Math.cos(rr) * d;
        double dy = Math.sin(rr) * d;
        double x2 = x1 + dx;
        double y2 = y1 + dy;
//out of top bound
        if (f.getX() <= x2 + 0.00000001 && x2 <= f.getX() + f.getWidth() + 0.00000001 && y2 < f.getY() + 0.00000001) {
            y2 = y1;
            b.setX(x2);
            b.setY(y2);
            System.out.println(this.name + " kicks the ball for a distance of " + d + " pixels and direction " + r + " degree." +  " Ball reflected." + " Ball is at (" + b.getX() + "," + b.getY() + ") \n");
        }//out of bottom bound
        else if (f.getX() <= x2 + 0.00000001 && x2 <= f.getX() + f.getWidth() + 0.00000001 && y2 > f.getY() + f.getHeight() + 0.00000001) {
            y2 = y1;
            b.setX(x2);
            b.setY(y2);
            System.out.println(this.name + " kicks the ball for a distance of " + d + " pixels and direction " + r + " degree." + " Ball reflected."
                    + " Ball is at (" + b.getX() + "," + b.getY() + ") \n");
        }
        //out of right bound
        else if (f.getY() <= y2 + 0.00000001 && y2 <= f.getY() + f.getHeight() + 0.00000001 && x2 > f.getX() + f.getWidth() + 0.00000001) {
            x2 = x1;
            b.setX(x2);
            b.setY(y2);
            System.out.println(this.name + " kicks the ball for a distance of " + d + " pixels and direction " + r + " degree." + " Ball reflected."
                    + " Ball is at (" + b.getX() + "," + b.getY() + ") \n");
        }////out of left bound
        else if (f.getY() <= y2 + 0.00000001 && y2 <= f.getY() + f.getHeight() + 0.00000001 && x2 < f.getX() + 0.00000001) {
            x2 = x1;
            b.setX(x2);
            b.setY(y2);
            System.out.println(this.name + " kicks the ball for a distance of " + d + " pixels and direction " + r + " degree." + " Ball reflected." + " Ball is at (" + b.getX() + "," + b.getY() + ") \n");
        } else if (f.getY() <= y2 + 0.00000001 && y2 <= f.getY() + f.getHeight() + 0.00000001 && f.getX() <= x2 + 0.00000001 && x2 <= f.getX() + f.getWidth() + 0.00000001) {
            // within bounds
            b.setX(x2);
            b.setY(y2);
            System.out.println(this.name + " kicks the ball for a distance of " + d + " pixels and direction " + r + " degree." + " Ball not reflected." + " Ball is at (" + b.getX() + "," + b.getY() + ") \n");
        } else {// both out of bounds
            // do nothing
            System.out.println(this.name + " kicks the ball for a distance of " + d + " pixels and direction " + r + " degree." +" Ball reflected." + " Ball is at (" + b.getX() + "," + b.getY() + ") \n");

        }
    }
}




