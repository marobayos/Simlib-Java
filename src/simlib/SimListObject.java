package simlib;

public class SimListObject extends SimObject implements Comparable<SimListObject>{

    int sorting_atribute_based;

    public SimListObject(int sorting_atribute_based,float... atributes){
        this.sorting_atribute_based = sorting_atribute_based;
        this.atributes = atributes;

    }

    public SimListObject(float... atributes){
        this(-1,atributes);
    }

    @Override
    public int compareTo(SimListObject simListObject) {
        if(simListObject.sorting_atribute_based >
                this.sorting_atribute_based)
            return -1;
        if(simListObject.sorting_atribute_based <
                this.sorting_atribute_based)
            return 1;
        return 0;
    }
}
