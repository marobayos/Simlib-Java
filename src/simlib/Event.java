package simlib;

import Exception.*;

public class Event extends SimObject implements Comparable<Event>{
    private byte eventType;
    private float time;

    public Event(byte type, float time, Float... atributes) {
        this.eventType = type;
        this.time = time;
        this.atributes = atributes;
    }

    public Event(byte type, float time) {
        this.eventType = type;
        this.time = time;
        this.atributes = new Float[0];
    }

    public byte getType() {
        return this.eventType;
    }

    public float getTime(){
        return this.time;
    }

    @Override
    public int compareTo(Event event) {
        return (this.time < event.getTime()) ? -1 : ((this.time == event.getTime()) ? 0 : 1);
    }

    public String toString(){
        return eventType+" "+time;
    }

}