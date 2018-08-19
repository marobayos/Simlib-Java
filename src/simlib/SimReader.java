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
        int last = reader.read();
        while ((char)last == delimiter || (char)last == '\n' || last == -1){
            last = reader.read();
        }
        while ((char)last != delimiter && (char)last != '\n' && last != -1){
            token = token + (char)last;
            last = reader.read();
        }
        return token;
    }

    public int readInt() throws IOException {
        return Integer.parseInt(this.nextToken());
    }

    public float readFloat() throws IOException {
        return Float.parseFloat(this.nextToken());
    }

    public double readDouble() throws IOException {
        return Double.parseDouble(this.nextToken());
    }

    public String read() throws IOException {
        return this.nextToken();
    }

    public String readLine() throws IOException {
        return this.reader.readLine();
    }

    public void close() throws IOException {
        this.reader.close();
    }
}