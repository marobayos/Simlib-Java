import simlib.*;

import java.io.*;
import java.util.Random;

public class EjercicioTelefonia {

    static final byte LLAMADA_A_B = 0, LLAMADA_B_A = 1, FIN_LLAMADA = 2, FIN_SIM = 3;
    static Random random;
    static int totalLineas;
    static int meanAB, meanBA, min, max;
    static double maxTime;
    static Timer simTime;
    static ContinStat lineas;
    static SimList< Event > eventos;
    static int llamadasAtendidas;
    static int llamadasBloqueadas;
    static byte eventType;

    public static void main(String[]args)throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("Input8.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("Output8.txt"));

        /* LEER Y GUARDAR PARÁMETROS */
        totalLineas = Integer.parseInt( input.readLine() );
        meanAB = Integer.parseInt( input.readLine() );
        meanBA = Integer.parseInt( input.readLine() );
        String in = input.readLine();
        min = Integer.parseInt( in.split("-")[1] );
        max = Integer.parseInt( in.split("-")[0] );
        maxTime = Double.parseDouble( input.readLine() );

        /* INICIALIZAR */
        inicializar();
        System.out.println("Init");
        do {
            sincronizar();
            switch ( eventType ) {
                case LLAMADA_A_B:
                    llamadaAB();
                    break;
                case LLAMADA_B_A:
                    llamadaBA();
                    break;
                case FIN_LLAMADA:
                    finLlamada();
                    break;
                case FIN_SIM:
                    finSim( out );
                    break;
            }
            System.out.println(eventType+" "+simTime.getTime());
        } while ( eventType != FIN_SIM );

        /* CERRAR ARCHIVOS */
        input.close();
        out.close();
    }

    /*********************
     *      RUTINAS      *
     *********************/

    /**
     * Rutina de sincronización: elimina de la cola de eventos el evento ya realizado,
     * actualiza el tiempo de la simulación y actualiza algunas variables.
     **/
    private static void sincronizar() {
        // Actualiza el tiempo y evento en curso en la simulación
        eventType = eventos.getFirst().getType();
        simTime.setTime(eventos.getFirst().getTime());

        // Elimina el evento ya procesado
        eventos.removeFirst();
    }

    /**
     * Rutina de inicialización: inicializa todas las colas y variables de la
     * simulación, programa en la lista de eventos la primera llegada de cada tipo
     * de caja y el fin de la simulación.
     */
    private static void inicializar() {
        /* Para tener datos diferentes en cada simulación */
        random = new Random( );
        random.setSeed( System.nanoTime() );

        /* Inicializa el tiempo de la simulación en 0.0 */
        simTime = new Timer( );

        /* Inicializa la lista de eventos */
        eventos = new SimList<Event>("Lista de Eventos", 0, true);

        /* Inicializa todas las líneas como disponibles */
        lineas = new ContinStat((float)totalLineas,simTime.getTime(),"lineas");

        /* Añadde primeras llamadas a la lista de eventos*/
        eventos.add(new Event(LLAMADA_A_B, simTime.getTime() + distExponencial(meanAB)));
        eventos.add(new Event(LLAMADA_B_A, simTime.getTime() + distExponencial(meanBA)));

        eventos.add(new Event(FIN_SIM, (float) maxTime));
    }

    /*
      RUTINAS DE EVENTOS
     */

    private static void llamadaAB(){
        eventos.add(new Event(LLAMADA_A_B, simTime.getTime() + distExponencial(meanAB)));
        if (lineas.getValue()>=1){
            lineas.recordContin(lineas.getValue()-1, simTime.getTime());
            eventos.add(new Event(FIN_LLAMADA, simTime.getTime()+ distUniforme(max, min)));
            llamadasAtendidas ++;
        } else
            llamadasBloqueadas++;
    }

    private static void llamadaBA(){
        eventos.add(new Event(LLAMADA_B_A, simTime.getTime() + distExponencial(meanBA)));
        if (lineas.getValue()>=1){
            lineas.recordContin(lineas.getValue()-1, simTime.getTime());
            eventos.add(new Event(FIN_LLAMADA, simTime.getTime()+ distUniforme(max, min)));
            llamadasAtendidas ++;
        } else
            llamadasBloqueadas++;
    }

    private static void finLlamada(){
        lineas.recordContin(lineas.getValue()+1, simTime.getTime());
    }
    /**
     * Fin de la simulación: Actualiza una última vez las variables del sistema, y
     * guarda en el archivo los datos obtenidos para las medidas de desempeño.
     *
     * @param bw   archivo en el que se guardarán los datos.
     */
    private static void finSim(BufferedWriter bw) throws IOException {
        bw.write("Promedio de líneas ocupadas: "+(totalLineas-lineas.getContinAve(simTime.getTime()))+"\n");
        bw.write("Proporción promedio de líneas ocupadas: "+(totalLineas-lineas.getContinAve(simTime.getTime()))/(totalLineas)+"\n");
        bw.write("Cantidad de llamadas bloquedas: "+llamadasBloqueadas+"\n");
        bw.write("Proporción de llamadas bloquedas: "+100*(double)llamadasBloqueadas/(double)(llamadasAtendidas+llamadasBloqueadas)+"\n");
    }

    /**********************************
     *   DISTRIBUCIONES ESTADÍSTICAS  *
     **********************************/

    /**
     * Distribución aleatoria con distribución uniforme
     *
     * @param max   valor máximo que puede retornar la distribución
     * @param min   valor mínimo que puede retornar la distribución
     * @return  valor aleatorio uniformemente distribuido en el rango [min, max)
     */
    private static float distUniforme(int max, int min){
        return min + random.nextFloat()*( max-min );
    }

    /**
     * Distribución exponencial
     *
     * @param mean    1/media
     * @return  valor aleatorio con distribucuón exponencial.
     */
    private static float distExponencial(double mean){
        return (float)(-mean*Math.log(random.nextFloat()));
    }
}