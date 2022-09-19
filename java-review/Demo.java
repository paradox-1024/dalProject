package com.phoebe;

import java.util.Random;

public class Demo {

        public static void main(String[] args){

            Field f = new Field();
            Ball b = new Ball();
            Random random = new Random();


            //System.out.println("SOCCER GAME SETUP!");
            Player p1 = new Player("Tom");
            Player p2 = new Player("Bob");
            f = new Field(0.0, 0.0, 200.0, 300.0);
            b = new Ball(100.0, 100.0);
            //repeat 10 kick each
            for (int i = 0; i < 10; i++) {
                double d = random.nextDouble() * f.getWidth();
                double r = random.nextDouble() * 360;
                p1.kick(f, b, d, r);
                System.out.println("Field: "+f.toString());
               System.out.println("Ball is at "+b.toString());

                d = random.nextDouble() * f.getWidth();
                r = random.nextDouble() * 360;
                p2.kick(f, b, d, r);
               System.out.println("Ball is at "+b.toString());


            }


        }
}

