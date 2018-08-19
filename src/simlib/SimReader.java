package simlib;

import java.io.*;

public class SimReader {
    private BufferedReader reader;
    private String name;
    private char delimiter;

    public SimReader(String name, char delimiter) throws FileNotFoundException {
        this.name = name;
        this.reader = new BufferedReader(new FileReader(name));
        this.delimiter = delimiter;
    }

    public SimReader(String name) throws FileNotFoundException {
        this(name, ' ');
    }

    public void useDelimiter(char delimit){
        delimiter = delimit;
    }

    private String nextToken() throws IOException {
        String token = "";
        char last = (char)reader.read();
        while (last == delimiter || last == '\n'){
            last = (char)reader.read();
        }
        while (last != delimiter && last != '\n'){
            token = token + last;
            last = (char)reader.read();
        }
        return token;
    }

    private int readInt() throws IOException {
        return Integer.parseInt(this.nextToken());
    }

    private float readFloat() throws IOException {
        return Float.parseFloat(this.nextToken());
    }

    private double readDouble() throws IOException {
        return Double.parseDouble(this.nextToken());
    }

    private String read() throws IOException {
        return this.nextToken();
    }

    private String readLine() throws IOException {
        return this.reader.readLine();
    }
}