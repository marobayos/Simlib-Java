package simlib;

public class Event implements Comparable<Event>{
    private byte eventType;
    private float time;

    public Event(byte type, float time) {
        this.eventType = type;
        this.time = time;
    }

    public byte getType() {
        return this.eventType;
    }

    public float getTime(){
        return this.time;
    }

    @Override
    public int compareTo(Event event) {
        return (this.time > event.getTime()) ? -1 : ((this.time == event.getTime()) ? 0 : 1);
    }
}