package simlib.collection;

import simlib.elements.Event;
import simlib.elements.Timer;

public class EventsList extends Collection{
    private PriorityQueue<Event> data;

    public EventsList () {
        super("Events list");
    }

    public void add( Event event ){
        data.offer(event);
    }

    public void add( byte type, double time, float... atributes ){
        data.offer( new Event( type, time, atributes ) );
    }

    public Event removeFirst(){
        return data.poll();
    }

    public Event getFirst(){
        return data.peek();
    }

    public int size(){
        return data.size;
    }
}