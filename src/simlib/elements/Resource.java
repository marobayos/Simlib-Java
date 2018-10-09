package simlib.elements;

import simlib.exception.*;
import simlib.io.SimWriter;

import java.io.IOException;

import static simlib.SimLib.*;

public class Resource<E> extends Element{
    private E element;
    private int request;

    public Resource(String name){
        super( name );
        this.request = 0;
        this.element = null;
    }

    @Override
    public void report(SimWriter out) throws IOException {
        this.update();
        out.write("************************************************************\n");
        out.write(this.completeLine("*  RESOURCE "+name));
        out.write("************************************************************\n");
        out.write(completeLine("*  Status = "+ (this.isIdle()?"IDLE":"BUSSY")));
        out.write(this.completeLine("*  Average = "+this.getAverage()));
        out.write(this.completeLine("*  Time interval = "+start+" - "+simTime));
        out.write(this.completeLine("*  Request = "+request));
        out.write("************************************************************\n\n");
    }

    public E remove(){
        update();
        E value = this.element;
        this.element = null;
        return value;
    }

    public void emplace(E element){
        update();
        if( this.element!=null )
            throw new HasAlreadyElements(this.name);
        this.element = element;
        request++;
    }

    public E getElement(){
        return this.element;
    }

    public E replace(E element){
        update();
        E value = this.element;
        this.element = element;
        request++;
        return value;
    }

    public boolean isIdle(){
        return this.element == null;
    }

    public boolean isBussy(){
        return this.element != null;
    }

    public double getAverage(){
        update();
        return (this.area)/(simTime - start);
    }

     void update(){
        if(element != null)
            this.area += (simTime - this.lastUpdate);
        this.lastUpdate = simTime;
    }
}
