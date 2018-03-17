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
    static int llamadasRechazadas;
    static byte eventType;

    public static void main(String[]args)throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("Input.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("Output.txt"));

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
                    //llegadaA();
                    break;
                case LLAMADA_B_A:
                    //llegadaB();
                    break;
                case FIN_LLAMADA:
                    //llegadaC();
                    break;
                case FIN_SIM:
                    finSim( out );
                    break;
            }
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

        /* Inicializa todas las líneas disponibles */
        lineas = new ContinStat((float)totalLineas,simTime.getTime(),"lineas");
    }

    /**
     * RUTINAS DE EVENTOS
     **********************/

    /**
     * Fin de la simulación: Actualiza una última vez las variables del sistema, y
     * guarda en el archivo los datos obtenidos para las medidas de desempeño.
     *
     * @param bw   archivo en el que se guardarán los datos.
     */
    private static void finSim(BufferedWriter bw) throws IOException {
        /*elevador.report(bw, simTime.getTime());
        transitoA.report(bw);
        esperaB.report(bw);
        cajasFaltantes.report(bw, simTime.getTime());
        bw.write("Promedio de cajas C transportadas por hora: "+totalC/simTime.getTime()*60);*/
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
     * @param lambda    1/media
     * @return  valor aleatorio con distribucuón exponencial.
     */
    private static float distExponencial(double lambda){
        return (float)(-1/lambda*Math.log(random.nextFloat()));
    }
}