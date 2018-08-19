package simlib;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class SimWriter {
    private BufferedWriter writer;
    private String name;

    public SimWriter(String file) throws Exception {
        this.name = file;
        this.writer = new BufferedWriter(new FileWriter(name));
    }

    private void write(int value) throws IOException {
        this.writer.write(value);
    }

    private void write(float value) throws IOException {
        this.writer.write(Float.toString(value));
    }

    private void write(double value) throws IOException {
        this.writer.write(Double.toString(value));
    }

    private void write(String value) throws IOException {
        this.writer.write(value);
    }
}
