package simlib.collection;

import simlib.io.SimWriter;

import java.io.IOException;
import static simlib.SimLib.*;

public abstract class Collection {
    private double area;
    private float lastUpdate;
    protected String name;
    private float start;
    protected int size;
    protected int maxSize;
    protected int total;

    public Collection(String name){
        area = lastUpdate = start = size = total = maxSize = 0;
        this.name = name;
    };

    public double getAvgSize() {
        this.update();
        return this.area/simTime;
    }

    protected void update(){
        if( this.size > maxSize )
            maxSize = this.size;
        area += this.size * (simTime - lastUpdate);
        this.lastUpdate = simTime;
    }

    protected void report( SimWriter out, String kind ) throws IOException {
        out.write("************************************************************\n");
        out.write(this.completeLine("*  "+kind+" "+name));
        out.write("************************************************************\n");
        out.write(this.completeLine("*  Time interval = "+start+" - "+simTime));
        out.write(this.completeLine("*  Incoming = "+total));
        out.write(this.completeLine("*  Outcoming = "+(total-this.size)));
        out.write(this.completeLine("*  Current length = "+this.size));
        out.write(this.completeLine("*  Maximal length = "+maxSize));
        out.write(this.completeLine("*  Average length = "+this.getAvgSize()));
        out.write("************************************************************\n\n");
    }

    public abstract void report( SimWriter out ) throws IOException;

    private String completeLine( String line ){
        while (line.length()<59){
            line += " ";
        }
        return line + "*\n";
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }
}