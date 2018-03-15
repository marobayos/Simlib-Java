package simlib;

import java.util.Collection;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class OrderedList<E> extends PriorityQueue<E> implements List {
    protected double area;

    protected float lastUpdate;

    protected String name;

    public OrderedList(String name, float timer){
        this.area = 0;
        this.lastUpdate  = timer;
    }

    @Override
    public void update(float time){
        area += this.size() * (time - lastUpdate);
        this.lastUpdate = time;
    }

    @Override
    public double getAvgSize(float time) {
        this.update(time);
        return this.area/time;
    }

}
