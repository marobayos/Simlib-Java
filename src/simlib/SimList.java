package simlib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class SimList <E> extends LinkedList<E> {
    private double area;
    private float lastUpdate;
    private String name;
    private boolean sorted;
    private int maxSize;
    private Timer timer;

    public SimList(String name, Timer timer, boolean sorted){
        this.area = 0;
        this.lastUpdate  = timer.getTime();
        this.timer = timer;
        this.sorted = sorted;
        this.name = name.toUpperCase();
        this.maxSize = 0;
    }

    public SimList(Timer timer, boolean sorted){
        this("Lista", timer, sorted);
    }

    public SimList(Timer timer){
        this("List", timer, false);
    }

    public void update(){
        area += this.size() * (timer.getTime() - lastUpdate);
        this.lastUpdate = timer.getTime();
    }

    public double getAvgSize() {
        this.update();
        return this.area/timer.getTime();
    }

    public void report(SimWriter out, float time) throws IOException {
        out.write("REPORTE DE LA LISTA \""+name+"\":\n");
        out.write("\tLongitud promedio: "+this.getAvgSize()+"\n");
        out.write("\tLongitud máxima: "+this.maxSize+"\n\n");
    }

    public void sort(){
        this.sorted=true;
        super.sort(null);
    }

    public void addFirst(E element){
        super.addFirst(element);
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
    }

    public void addLast(E element){
        super.addLast(element);
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
    }

    public boolean add(E element){
        boolean res = super.add(element);
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
        return res;
    }

    public boolean addAll(Collection<? extends E> c){
        boolean res = super.addAll(c);
        if(this.size()>maxSize)
            maxSize = this.size();
        if (this.sorted)
            sort();
        return res;
    }

    public boolean addAll(int index, Collection<? extends E> c){
        boolean res = super.addAll(index, c);
        if(this.size()>maxSize)
            maxSize = this.size();
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
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
    }

    public boolean offer(E element){
        boolean res = this.add(element);
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
        return res;
    }

    public boolean offerFirst(E element){
        boolean res = super.offerFirst(element);
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
        return res;
    }
    public boolean offerLast(E element){
        boolean res = super.offerLast(element);
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
        return res;
    }

    public void push(E element){
        super.push(element);
        if(this.size()>maxSize)
            maxSize = this.size();
        if(sorted)
            this.sort();
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
        if(sorted)
            this.sort();
    }

    public boolean isSorted() {
        return sorted;
    }

    public String toString(){
        String str = name + ": [";
        for(int i = 0; i<this.size() ; i++){
            str += this.get(i);
            if(i!=this.size()-1)
                str+=", ";
        }
        return str+"]";
    }
}
