package simlib.elements;

import simlib.io.SimWriter;
import java.io.IOException;
import static simlib.SimLib.*;

public class Store<E> extends Element{
    private long capacity;
    private long used;

    public Store(String name, long capacity){
        super( name );
        this.capacity = capacity;
        this.used = 0;
    }

    public void setCapacity(long capacity){
        this.capacity = capacity;
    }

    public long getCapacity() {
        return capacity;
    }

    public boolean hasSpace(){
        return capacity!=used;
    }

    public boolean isFull(){
        return capacity!=used;
    }

    public void enter(){
        this.update();
        if ( this.used + 1 > this.capacity ){
            System.out.println("There is not enough in the store "+name);
            System.exit(1);
        }
        this.used++;
    }

    public void enter(long amount){
        this.update();
        if ( this.used + amount > this.capacity ){
            System.out.println("There is not enough in the store "+name);
            System.exit(1);
        }
        this.used += amount;
    }

    public long avaliable(){
        return this.capacity - this.used;
    }

    public void leave(){
        this.update();
        if ( this.used - 1 < 0){
            System.out.println("The store "+name+" is empty");
            System.exit(2);
        }
        this.used --;
    }

    public void leave(long amount){
        this.update();
        if ( this.used - amount < 0){
            System.out.println("The store "+name+" is empty");
            System.exit(2);
        }
        this.used -= amount;
    }

     void update(){
        area += ( simTime - lastUpdate )*used;
        lastUpdate = simTime;
    }

    public double getAverage(){
        update();
        return area/( simTime-start );
    }

    @Override
    public void report(SimWriter out) throws IOException {
        this.update();
        out.write("************************************************************\n");
        out.write(this.completeLine("*  STORE "+name));
        out.write("************************************************************\n");
        out.write(this.completeLine("*  Capacity = "+capacity));
        out.write(this.completeLine("*  Status = "+ used +" used, "+ (capacity-used) +" avaliable"));
        out.write(this.completeLine("*  Average = "+this.getAverage()));
        out.write(this.completeLine("*  Time interval = "+start+" - "+simTime));
        out.write("************************************************************\n\n");
    }
}
