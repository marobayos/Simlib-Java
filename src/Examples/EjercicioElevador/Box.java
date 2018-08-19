package Examples.EjercicioElevador;

public class Box implements Comparable<Box> {
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

    public int compareTo(Box box) {
        return (this.arriveTime > box.getArriveTime()) ? -1 : ((this.arriveTime == box.arriveTime) ? 0 : 1);
    }

    public String toString(){
        return this.boxType+"";
    }
}
