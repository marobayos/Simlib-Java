package simlib.elements;

import simlib.io.SimWriter;

import java.io.IOException;

import static simlib.SimLib.*;

public class Facility extends Element{
    private static final byte IDLE = 0, BUSSY = 1;
    private byte status;
    private int request;

    public Facility(String name){
        super( name );
        this.request = 0;
        this.status = 0;
    }

    @Override
    public void report(SimWriter out) throws IOException {
        this.update();
        out.write("************************************************************\n");
        out.write(this.completeLine("*  FACILITY "+name));
        out.write("************************************************************\n");
        out.write(completeLine("*  Status = "+(this.isIdle()?"IDLE":"BUSSY")));
        out.write(this.completeLine("*  Average = "+this.getAverage()));
        out.write(this.completeLine("*  Time interval = "+start+" - "+simTime));
        out.write(this.completeLine("*  Request = "+request));
        out.write("************************************************************\n\n");
    }

    public void setIdle(){
        this.update();
        status = IDLE;
    }

    public void setBussy(){
        this.update();
        status = BUSSY;
        request ++;
    }

    public boolean isIdle(){
        return this.status == IDLE;
    }

    public boolean isBussy(){
        return this.status == BUSSY;
    }

    public double getAverage(){
        update();
        return (this.area)/(simTime - start);
    }

     void update(){
        area += (simTime - lastUpdate)*status;
        lastUpdate = simTime;
    }
}
