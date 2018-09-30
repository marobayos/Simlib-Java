package simlib.elements;

import simlib.exception.OutOfRangeException;

public class Event implements Comparable<Event>{
    private byte eventType;
    private float time;
    private float[] attributes;

    public Event(byte type, float time, float... attributes) {
        this.eventType = type;
        this.time = time;
        this.attributes = attributes;
    }

    public Event(byte type, int time, float... attributes) {
        this(type, (double)time, attributes);
    }

    public Event(byte type, double time, float... attributes) {
        this(type, (float)time, attributes);
    }

    public Event(byte type, float time) {
        this.eventType = type;
        this.time = time;
        this.attributes = new float[0];
    }

    public byte getType() {
        return this.eventType;
    }

    public float getTime(){
        return this.time;
    }

    public float getAttribute( int index ){
        if( index >= attributes.length )
            throw new OutOfRangeException( "Event", attributes.length, index );
        return attributes[index];
    }

    public float[] getAttributes(){
        return attributes;
    }

    @Override
    public int compareTo(Event event) {
        return (this.time < event.getTime()) ? -1 : ((this.time == event.getTime()) ? 0 : 1);
    }

    public String toString(){
        return eventType+" "+time;
    }

}