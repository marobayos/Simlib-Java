package simlib;

public class Client implements Comparable<Client>{
    private Float arrivalTime;
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

    public int compareTo(Client client){
        return this.arrivalTime.compareTo(client.getArrivalTime());
    }
}
