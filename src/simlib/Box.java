package simlib;

public class Box {
    private float arriveTime;
    private char boxType;
    private int weight;

    public Box(float arriveTime, char boxType){
        this.boxType=boxType;
        this.arriveTime=arriveTime;
        switch( boxType ){
            case 'A':
                this.weight = 200;
                break;
            case 'B':
                this.weight = 100;
                break;
            case 'C':
                this.weight = 50;
                break;
        }
    }

    public float getArriveTime() {
        return arriveTime;
    }

    public char getBoxType() {
        return this.boxType;
    }

    public int getWeight() {
        return this.weight;
    }

}//sebas estuvo aqui muajajajajajajajaja
