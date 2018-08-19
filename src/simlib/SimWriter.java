package simlib;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class SimWriter {
    private BufferedWriter writer;
    private String name;

    public SimWriter(String file) throws IOException {
        this.name = file;
        this.writer = new BufferedWriter(new FileWriter(name));
    }

    public void write(int value) throws IOException {
        this.writer.write(value);
    }

    public void write(float value) throws IOException {
        this.writer.write(Float.toString(value));
    }

    public void write(double value) throws IOException {
        this.writer.write(Double.toString(value));
    }

    public void write(String value) throws IOException {
        this.writer.write(value);
    }

    public void close() throws IOException {
        this.writer.close();
    }

    public void report(ContinStat stat){}

    public void report(DiscreteStat stat){}

    public void report(SimList list){}
}
