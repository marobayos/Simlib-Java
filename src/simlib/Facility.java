package simlib;

public class Facility {
    private static final byte IDLE = 0, BUSSY = 1;
    private byte status;
    private int request;
    private double area;
    private Timer timer;
    private float start;
    private String name;
    private float lastUpdate;

    public Facility(String name, Timer timer){
        this.area = request = 0;
        this.status = 0;
        this.lastUpdate = 0;
        this.timer = timer;
        this.start = timer.getTime();
        this.name = name;
    }

    public void setIdle(){
        this.update();
        status = IDLE;
    }

    public void setBussy(){
        this.update();
        status = BUSSY;
        request ++;
    }

    public boolean isIdle(){
        return this.status == IDLE;
    }

    public boolean isBussy(){
        return this.status == BUSSY;
    }

    public double getAverage(){
        update();
        return (this.area)/(timer.getTime() - start);
    }

    private void update(){
        area += (timer.getTime() - lastUpdate)*status;
        lastUpdate = timer.getTime();
    }
}
