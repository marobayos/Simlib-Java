package simlib.collection;

import simlib.io.SimWriter;
import simlib.exception.EmptyListException;
import simlib.exception.OutOfRangeException;

import java.io.IOException;

public class Stack<E> extends Collection {
    public Stack(String name) {
        super(name);
    }

    private class Node<E>{
        Node next;
        E data;

        Node(Node next, E data){
            this.next = next;
            this.data = data;
        }
    }

    private Node<E> head;

    public E peek(){
        return head.data;
    }

    public void push( E data ){
        update();
        Node newNode = new Node( head, data );
        head = newNode;
        size ++;
        total ++;
    }

    public E pop( ){
        update();
        if ( size == 0 )
            throw new EmptyListException(this.name);
        E data = head.data;
        head = head.next;
        size --;
        return data;
    }

    public boolean remove( E element ){
        for( Node node = head; node!= null; node = node.next ){
            if ( node.data.equals(element) ){
                node = node.next;
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
        this.head = null;
        size = 0;
    }

    public int search( E data ){
        int pos = 0;
        for(  Node node = head; node!= null; node = node.next ){
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
        super.report(out, "STACK");
    }

}
