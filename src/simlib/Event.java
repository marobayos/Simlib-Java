package simlib;

import Exception.*;

public class Event implements Comparable<Event>{
    private byte eventType;
    private float time;
    private float atributes[];

    public Event(byte type, float time, float... atributes) {
        this.eventType = type;
        this.time = time;
        this.atributes = atributes;
    }

    public byte getType() {
        return this.eventType;
    }

    public float getAtribute(int atribute){
        if(atribute < atributes.length){
            return atributes[atribute];
        } else if(atributes.length == 0)
            throw new EmptyListException("Lista de eventos");
        else
            throw new OutOfRangeException("Lista de eventos", atributes.length, atribute);
    }

    public float getTime(){
        return this.time;
    }

    @Override
    public int compareTo(Event event) {
        return (this.time < event.getTime()) ? -1 : ((this.time == event.getTime()) ? 0 : 1);
    }
}