package simlib.collection;

import simlib.io.SimWriter;
import simlib.exception.*;

import java.io.IOException;

public class LinkedList<E> extends Collection{

    public LinkedList(String name) {
        super(name);
    }

    private class Node<E>{
        Node next;
        Node prev;
        E data;

        Node(Node next, Node prev, E data){
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }

    private Node<E> head;
    private Node <E> tail;

    public E front(){
        return head.data;
    }

    public E back(){
        return tail.data;
    }

    public void insertFront( E data ){
        update();
        Node newNode = new Node( head, null, data );
        if( head != null)
            head.prev = newNode;
        head = newNode;
        tail = ( tail == null )? newNode : tail;
        size ++;
        total ++;
    }

    public void insertBack( E data ){
        update();
        Node newNode = new Node(null, tail, data);
        if( tail != null )
            tail.next = newNode;
        tail = newNode;
        head = ( head == null )? newNode : head;
        size ++;
        total ++;
    }

    public E removeFront( ){
        update();
        if ( size == 0 )
            throw new EmptyListException(this.name);
        E data = head.data;
        head = head.next;
        if( head != null )
            head.prev = null;
        size --;
        return data;
    }

    public E removeBack( ){
        update();
        if ( size == 0 )
            throw new EmptyListException(this.name);
        E data = tail.data;
        tail = tail.prev;
        if( tail != null )
            tail.next = null;
        size --;
        return data;
    }

    public boolean remove( E element ){
        for( Node node = head; node!= null; node = node.next ){
            if ( node.data.equals(element) ){
                node.next.prev = node.prev;
                node.prev.next = node.next;
                update();
                size --;
                return true;
            }
        }
        return false;
    }

    public int size(){
        return this.size;
    }

    public void clear(){
        update();
        this.head = this.tail = null;
        size = 0;
    }

    public int find( E data ){
        int pos = 0;
        for( Node node = head; node!= null; node = node.next ){
            if ( node.data.equals( data ) )
                return pos;
            pos++;
        }
        return -1;
    }

    public E get( int index ){
        if( index >= size )
            throw new OutOfRangeException( name, size, index );
        Node<E> node = head;
        for (int i = 0; i < index; i++)
            node = node.next;
        return node.data;
    }

    public void report(SimWriter out) throws IOException {
        super.report(out, "LINKED LIST");
    }
}