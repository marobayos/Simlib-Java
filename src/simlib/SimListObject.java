package simlib;

public class SimListObject extends SimObject implements Comparable<SimListObject>{

    int sorting_atribute_based;

    public SimListObject(int sorting_atribute_based,float... atributes){
        this.sorting_atribute_based = sorting_atribute_based;
        this.atributes = atributes;
    }

    public SimListObject(float... atributes){
        this(0,atributes);
    }

    public Float getIndex(){
        return this.getAtribute(sorting_atribute_based);
    }

    @Override
    public int compareTo(SimListObject simListObject) {
        return this.getIndex().compareTo(simListObject.getIndex());
    }
}
