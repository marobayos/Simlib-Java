package simlib;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class SimList <E> extends LinkedList<E> {
    protected double area;

    protected float lastUpdate;

    protected String name;

    protected boolean sorted;

    public SimList(String name, float timer, boolean sorted){
        this.area = 0;
        this.lastUpdate  = timer;
        this.sorted = sorted;
    }

    public SimList(){
        this("List", 0, false);
    }

    public void update(float time){
        area += this.size() * (time - lastUpdate);
        this.lastUpdate = time;
    }

    public double getAvgSize(float time) {
        this.update(time);
        return this.area/time;
    }

    public void report(){}

    public void sort(){
        Arrays.sort(this.toArray());
    }

    public void addFirst(E element){
        super.addFirst(element);
        if(sorted)
            this.sort();
    }

    public void addLast(E element){
        super.addLast(element);
        if(sorted)
            this.sort();
    }

    public boolean add(E element){
        boolean res = super.add(element);
        if(sorted)
            this.sort();
        return res;
    }

    public boolean addAll(Collection<? extends E> c){
        boolean res = super.addAll(c);
        if (this.sorted)
            sort();
        return res;
    }

    public boolean addAll(int index, Collection<? extends E> c){
        boolean res = super.addAll(index, c);
        if (this.sorted)
            sort();
        return res;
    }

    public E set(int index, E element){
        E res = super.set(index, element);
        if(sorted)
            this.sort();
        return res;
    }

    public void add(int index, E element){
        this.add(index, element);
        if(sorted)
            this.sort();
    }

    public boolean offer(E element){
        boolean res = this.add(element);
        if(sorted)
            this.sort();
        return res;
    }

    public boolean offerFirst(E element){
        boolean res = super.offerFirst(element);
        if(sorted)
            this.sort();
        return res;
    }
    public boolean offerLast(E element){
        boolean res = super.offerLast(element);
        if(sorted)
            this.sort();
        return res;
    }

    public void push(E element){
        super.push(element);
        if(sorted)
            this.sort();
    }

    public boolean isSorted() {
        return sorted;
    }
}
