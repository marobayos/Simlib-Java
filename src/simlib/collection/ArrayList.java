package simlib.collection;

import simlib.io.SimWriter;
import simlib.exception.OutOfRangeException;

import java.io.IOException;

public class ArrayList<E> extends Collection{
    Object[] data;

    public ArrayList(String name) {
        super(name);
        this.data = new Object[15];
    }

    public void add(E value){
        update();
        if (size == data.length)
            growUp();
        data[size] = value;
        size ++;
        total ++;
    }

    public void add(int index, E value){
        update();
        if( index>data.length )
            throw new OutOfRangeException( name, size, index );
        if( size == data.length )
            growUp();
        moveRight(index);
        data[index] = value;
        size ++;
        total ++;
    }

    public void clear(){
        update();
        data = new Object[10];
        size = 0;
    }

    public E get( int index ){
        return (E) data[index];
    }

    public boolean contains( E element ){
        for (int i = 0; i < size; i++) {
            if ( data[i].equals(element) )
                return true;
        }
        return false;
    }

    public int indexOf( E element ){
        for (int i = 0; i < size; i++) {
            if ( data[i].equals(element) )
                return i;
        }
        return -1;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public E remove( int index ){
        if( data.length <= index )
            throw new OutOfRangeException( name, size, index );
        update();
        E value = (E) data[index];
        moveLeft(index);
        size --;
        return value;
    }

    public void set( int index, E element ){
        data[index] = element;
    }

    private void moveRight( int index ){
        Object value = data[index];
        for (int i = size ; i > index ; i++) {
            data[i] = data[i-1];
        }
    }

    private void moveLeft( int index ){
        Object value = data[index];
        for (int i = index ; i <= size ; i++) {
            data[i] = data[i-1];
        }
    }

    private void growUp(){
        Object[] memory = this.data;
        data = new Object[(memory.length<<1)+1];
    }

    public void report(SimWriter out) throws IOException {
        super.report(out, "ARRAY LIST");
    }
}