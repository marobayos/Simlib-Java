package simlib.collection;

import simlib.Event;
import simlib.Timer;

public class EventsList extends Collection{
    private PriorityQueue<Event> data;

    public EventsList(String name, Timer timer) {
        super(name, timer);
    }

    public void add( Event event ){
        data.offer(event);
    }

    public void add( byte type, double time, float... atributes ){
        data.offer( new Event( type, time, atributes ) );
    }

    public Event remove(){
        return data.poll();
    }

    public Event first(){
        return data.peek();
    }

    public int size(){
        return data.size;
    }
}