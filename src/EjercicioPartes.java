import simlib.*;

import java.util.Random;
import java.util.StringTokenizer;
import java.io.*;

public class EjercicioPartes {

    static final byte a = 0, b = 2,c = 1, NUEVA = 1, NO_NUEVA = 0,
    LLEGADA = 1, TERMINACION = 2, FIN_SIMULACION = 3, TAREA = 0,
            MAQUINA_1 = 1, MAQUINA_2 = 2, TIEMPO_LLEGADA = 1;

    static final int ruta[] = {0,1,2,1};
    static final boolean status_maquinas[] = new boolean[3];
    static final SimList<SimListObject> colas[] = new SimList[3]; // Colas de la maquina 1 y 2
    static final float medias[][] = new float[3][3];// Medias Máquina 1
    static final SimListObject maquinas[] = new SimListObject[3];

    static int num_total_piezas_procesadas,tarea;
    static float total_demoras_piezas_procesadas;

    /*variables sobre todas las simulaciones*/
    static int total_piezas_procesadas_simulaciones;
    static float total_demoras_piezas_procesadas_simulaciones;
    static float promedio_piezas_en_cola_maquina_1;
    static float promedio_piezas_en_cola_maquina_2;


    static Random random;
    static Event evento_actual;
    static Timer tiempo_simulacion;
    static SimList<Event> eventos; // lista de eventos
    static SimListObject pieza_actual;

    static float media_llegada_pieza;
    static float tiempo_simulacion_max;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("InputPartes.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("OutputPartes.txt"));
        StringTokenizer medias_r;// lee y separa cada número de una linea leida

        //lectura de la media de llegada de las piezas
        medias_r = new StringTokenizer(br.readLine());
        media_llegada_pieza = Float.parseFloat(medias_r.nextToken());

        //lectrua del tiempo de servicio en la maquina 1

        medias_r = new StringTokenizer(br.readLine());
        medias[MAQUINA_1][a] = Float.parseFloat(medias_r.nextToken());
        medias[MAQUINA_1][c] = Float.parseFloat(medias_r.nextToken());
        medias[MAQUINA_1][b] = Float.parseFloat(medias_r.nextToken());

        //lectrua del tiempo de servicio en la maquina 2

        medias_r = new StringTokenizer(br.readLine());
        medias[MAQUINA_2][a] = Float.parseFloat(medias_r.nextToken());
        medias[MAQUINA_2][c] = Float.parseFloat(medias_r.nextToken());
        medias[MAQUINA_2][b] = Float.parseFloat(medias_r.nextToken());

        //lectura tiempo maximo de la simulación
        tiempo_simulacion_max = Float.parseFloat(br.readLine());

        /*Formato de los parametros*/

        bw.write("PARAMETROS:\n\n");
        bw.write("MEDIA DE LLEGADA DE PIEZAS: " + media_llegada_pieza + " min\n");
        bw.write("MEDIA DE SERVICIO MAQUINA 1: TRIA(" +
                medias[MAQUINA_1][a] + ", " + medias[MAQUINA_1][c]
                + ", " + medias[MAQUINA_1][b] + ") min\n");
        bw.write("MEDIA DE SERVICIO MAQUINA 2: TRIA(" +
                medias[MAQUINA_2][a] + ", " + medias[MAQUINA_2][c]
                + ", " + medias[MAQUINA_2][b]+") min\n");
        bw.write("TIEMPO DE LA SIMULACION: " + tiempo_simulacion_max +" min\n");
        bw.write("\n\n-----------------------------------------------\n" +
                "RESULATADOS:\n\n");

        total_demoras_piezas_procesadas_simulaciones = 0;
        total_piezas_procesadas_simulaciones = 0;
        promedio_piezas_en_cola_maquina_1 = 0;
        promedio_piezas_en_cola_maquina_2 = 0;

        for(int i = 0; i < 20; i++) {
            //Inicializar la Simulación

            bw.write("\nSIMULACIÓN " + (i+1) + "\n\n");

            inicializar();

            do {
                sincronizar();
                //System.out.print(tiempo_simulacion.getTime() + " ");
                switch (evento_actual.getType()) {
                    case LLEGADA:
                        //System.out.println("LLEGADA");
                        llegadaPieza(NUEVA);
                        break;
                    case TERMINACION:
                        //System.out.println("TERMINACION");
                        terminacionServicio();
                        break;
                    case FIN_SIMULACION:
                        finSimulacion(bw);
                        break;
                }
            } while (evento_actual.getType() != FIN_SIMULACION);
            total_piezas_procesadas_simulaciones += num_total_piezas_procesadas;
            total_demoras_piezas_procesadas_simulaciones += total_demoras_piezas_procesadas;
            promedio_piezas_en_cola_maquina_1 += colas[MAQUINA_1].getAvgSize(tiempo_simulacion.getTime());
            promedio_piezas_en_cola_maquina_2 += colas[MAQUINA_2].getAvgSize(tiempo_simulacion.getTime());
        }
        bw.write("\n\n----------------------------------------" +
                "\nPROMEDIOS GENERALES\n\n");
        bw.write("\nPROMEDIO GENERAL DE PIEZAS EN LA COLA DE LA MÁQUINA " +
                "1: " + promedio_piezas_en_cola_maquina_1/20);
        bw.write("\nPROMEDIO GENERAL DE PIEZAS EN LA COLA DE LA MÁQUINA " +
                "2: " + promedio_piezas_en_cola_maquina_2/20);
        bw.write("\nPROMEDIO DEMORAS DE LAS PIEZAS EN EL SISTEMA" +
                ": " + total_demoras_piezas_procesadas_simulaciones/total_piezas_procesadas_simulaciones);
        br.close();
        bw.close();

    }

    public static void inicializar(){

        tarea = 0;
        //Crear Generador de numeros aleatorios
        random = new Random();
        random.setSeed(System.nanoTime());

        // Inicializar el Reloj de la Simulacion
        tiempo_simulacion = new Timer();

        //Inicializar variables del sistema
        num_total_piezas_procesadas = 0;
        total_demoras_piezas_procesadas = 0;

        //Inicializar la lista de eventos
        eventos = new SimList<>(true);

        //Inicializar las colas de las máquinas
        colas[MAQUINA_1] = new SimList<>();
        colas[MAQUINA_2] = new SimList<>();

        //Poner las maquinas disponibles
       maquinas[MAQUINA_1] = maquinas[MAQUINA_2] = null;


        eventos.add(new Event(LLEGADA, distExponencial(media_llegada_pieza)
                ,TAREA)); /* Programa el evento llegada
                de una pieza nueva;*/

        eventos.add(new Event(FIN_SIMULACION, tiempo_simulacion_max));
    }

    public static void sincronizar(){
        evento_actual = eventos.getFirst();
        tiempo_simulacion.setTime(evento_actual.getTime());
        eventos.removeFirst();
    }

    public static void llegadaPieza(byte pieza_nueva){
        int maquina;
        if(pieza_nueva == NUEVA){
            eventos.add(new Event(LLEGADA,tiempo_simulacion.getTime()+
                    distExponencial(media_llegada_pieza)));
            tarea = 1;
            pieza_actual = new SimListObject((float)tarea,tiempo_simulacion.getTime());
        }

        maquina = ruta[tarea];

        if(maquinas[maquina] != null)/*Si la maquina esta ocupada*/{
            colas[maquina].add(pieza_actual);
            colas[maquina].update(tiempo_simulacion.getTime());
        }else{
            maquinas[maquina] = pieza_actual;
            eventos.add(new Event(TERMINACION,
                    tiempo_simulacion.getTime() +
                            disTriangular(medias[maquina][a], medias[maquina][c],medias[maquina][b])
            ,tarea));
        }

    }

    public static void terminacionServicio(){
        int maquina = ruta[(int) evento_actual.getAtribute(TAREA)];
        pieza_actual = maquinas[maquina];
        if(colas[maquina].isEmpty()){
            maquinas[maquina] = null;// Poner la maquina disponible
        }else{
            maquinas[maquina] = colas[maquina].getFirst();
            colas[maquina].removeFirst();
            eventos.add(new Event(TERMINACION,tiempo_simulacion.getTime()
            + disTriangular(medias[maquina][a],medias[maquina][c],medias[maquina][b])
                    ,maquinas[maquina].getAtribute(TAREA)));
        }
        if(pieza_actual.getAtribute(TAREA) < ruta.length-1){
            tarea = (int) (pieza_actual.getAtribute(TAREA) + 1);
            pieza_actual.setAtribute(TAREA,tarea);
            llegadaPieza(NO_NUEVA);
        }else{
            total_demoras_piezas_procesadas += tiempo_simulacion.getTime() - pieza_actual.getAtribute(TIEMPO_LLEGADA);
            num_total_piezas_procesadas++;
            pieza_actual = null;
        }
    }

    public static void finSimulacion(BufferedWriter bw) throws IOException {

        bw.write("PROMEDIO DE PIEZAS EN LA COLA DE LA MAQUINA 1: "
                + colas[MAQUINA_1].getAvgSize(tiempo_simulacion.getTime())+ " \n");
        bw.write("PROMEDIO DE PIEZAS EN LA COLA DE LA MAQUINA 2: "
                + colas[MAQUINA_2].getAvgSize(tiempo_simulacion.getTime())+ " \n");
        bw.write("PROMEDIO DE DEMORA DE LAS PIEZAS EN EL SISTEMA: "
                + (total_demoras_piezas_procesadas/num_total_piezas_procesadas) + " min\n");
        //System.out.println(num_total_piezas_procesadas);
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

    private static float distExponencial(double mean){
        return (float)(-mean*Math.log(random.nextFloat()));
    }

}
