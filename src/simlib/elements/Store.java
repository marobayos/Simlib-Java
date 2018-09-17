package simlib.elements;

public class Store {
    private long capacity;
    private long used;
    private String name;
    private float start;
    private Timer timer;
    private float area;
    private float lastUpdate;

    public Store(String name, long capacity, Timer timer){
        this.name = name;
        this.capacity = capacity;
        this.timer = timer;
        this.start = timer.getTime();
        this.area = this.lastUpdate = (float)0;
    }

    public void setCapacity(long capacity){
        this.capacity = capacity;
    }

    public void enter(){
        this.update();
        if ( this.used + 1 > this.capacity ){
            System.out.println("There is not enough in the store "+name);
            System.exit(1);
        }
        this.used++;
    }

    public void enter(long amount){
        this.update();
        if ( this.used + amount > this.capacity ){
            System.out.println("There is not enough in the store "+name);
            System.exit(1);
        }
        this.used += amount;
    }

    public long avaliable(){
        return this.capacity - this.used;
    }

    public void leave(){
        this.update();
        if ( this.used - 1 < 0){
            System.out.println("The store "+name+" is empty");
            System.exit(2);
        }
        this.used --;
    }

    public void leave(long amount){
        this.update();
        if ( this.used - amount < 0){
            System.out.println("The store "+name+" is empty");
            System.exit(2);
        }
        this.used -= amount;
    }

    private void update(){
        area += ( timer.getTime() - lastUpdate )*used;
        lastUpdate = timer.getTime();
    }
}
