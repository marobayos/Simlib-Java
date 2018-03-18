import simlib.*;

import java.util.Random;
import java.util.StringTokenizer;
import java.io.*;

public class EjercicioPartes {

    static final byte a = 0, b = 2,c = 1, NUEVA = 1, NO_NUEVA = 0,
    LLEGADA = 1, TERMINACION = 2, FIN_SIMULACION = 3;

    static int num_total_piezas_procesadas;
    static float total_demoras_piezas_procesadas;
    static Random random;
    static Event evento_actual;
    static float mediaM1[] = new float[3];// Medias Máquina 1
    static float mediaM2[] = new float[3];// Medias Máquina 2
    static float media_llegada_pieza;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("Input8.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("Output8.txt"));
        StringTokenizer medias;// lee y separa cada número de una linea leida

        //lectura de la media de llegada de las piezas
        medias = new StringTokenizer(br.readLine());
        media_llegada_pieza = Float.parseFloat(medias.nextToken());

        //lectrua del tiempo de servicio en la maquina 1

        medias = new StringTokenizer(br.readLine());
        mediaM1[a] = Float.parseFloat(medias.nextToken());
        mediaM1[c] = Float.parseFloat(medias.nextToken());
        mediaM1[b] = Float.parseFloat(medias.nextToken());

        //lectrua del tiempo de servicio en la maquina 2

        medias = new StringTokenizer(br.readLine());
        mediaM2[a] = Float.parseFloat(medias.nextToken());
        mediaM2[c] = Float.parseFloat(medias.nextToken());
        mediaM2[b] = Float.parseFloat(medias.nextToken());

        //Inicializar la Simulación

        inicializar();

        do {
            sincronizar();
            switch (evento_actual.getType())
            {
                case LLEGADA:
                    llegadaPieza(NUEVA);
                    break;
                case TERMINACION:
                    terinacionServicio();
                    break;
                case FIN_SIMULACION:
                    finSimulacion();
                    break;
            }
        }while (evento_actual.getType() != FIN_SIMULACION);

        br.close();
        bw.close();

    }

    public static void inicializar(){

    }

    public static void sincronizar(){

    }

    public static void llegadaPieza(byte nueva){

    }

    public static void terinacionServicio(){

    }

    public static void finSimulacion(){

    }

    static float disTriangular( double a, double c, double b){
        double rand = random.nextDouble();
        double x;
        double aux;
        if( rand < ((c-a)/(b-a)) ){
            aux = Math.sqrt(((c-a)*(b-a)*rand));
            x = a + aux;
        }else{
            aux = Math.sqrt((b-a)*(b-c)*(1-rand));
            x = b - aux;
        }
        return (float)x;
    }
}
