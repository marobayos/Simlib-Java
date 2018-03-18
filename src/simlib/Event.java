package simlib;

public class Event implements Comparable<Event>{
    private byte eventType, origen;
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
        if(atributes.length != 0){
            return atributes[atribute];
        }
        return (float) -10E30;
    }

    public float getTime(){
        return this.time;
    }

    @Override
    public int compareTo(Event event) {
        return (this.time < event.getTime()) ? -1 : ((this.time == event.getTime()) ? 0 : 1);
    }
}