package simlib;

import Exception.*;

public abstract class SimObject{

    public float atributes[];

    public SimObject(float... atributes){
        this.atributes = atributes;
    }

    public float getAtribute(int atribute){
        if(atribute < atributes.length){
            return atributes[atribute];
        } else if(atributes.length == 0)
            throw new EmptyListException("Lista de eventos");
        else
            throw new OutOfRangeException("Lista de eventos", atributes.length, atribute);
    }
    public void setAtribute(int atribute,float value){
        if(atribute < atributes.length){
            atributes[atribute] = value;
        } else if(atributes.length == 0)
            throw new EmptyListException("Lista de eventos");
        else
            throw new OutOfRangeException("Lista de eventos", atributes.length, atribute);
    }


}
