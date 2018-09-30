package simlib.elements;

import simlib.io.SimWriter;

import java.io.IOException;

import static simlib.SimLib.*;

public abstract class Element {
    Float start;
    float lastUpdate;
    float area;
    String name;

    public Element( String name ){
        this.name = name;
        start = simTime;
        area = 0;
    }

     String completeHalfLine(String line){
        while (line.length()<30){
            line += " ";
        }
        return line;
    }

     String completeLine(String line){
        while (line.length()<59){
            line += " ";
        }
        return line + "*\n";
    }

    public abstract void report( SimWriter out ) throws IOException;
    abstract void update();
}