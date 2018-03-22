package simlib;

public class Client {
    private float arrivalTime;
    private int standbyTolerance;

    public Client(float arrivalTime, int standbyTolerance){
        this.arrivalTime = arrivalTime;
        this.standbyTolerance = standbyTolerance;
    }

    public float getArrivalTime() {
        return arrivalTime;
    }

    public int getStandbyTolerance() {
        return standbyTolerance;
    }
}
