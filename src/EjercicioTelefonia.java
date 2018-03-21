import simlib.*;
import java.io.*;
import java.util.Random;

public class EjercicioTelefonia {

    static final byte LLAMADA_A = 0, LLAMADA_B = 1, FIN_LLAMADA = 2, FIN_SIM = 3, LLAMADA = 1, ORIGEN = 0;
    static Random random;
    static int totalLineas;
    static int means[], min, max;
    static double maxTime;
    static Timer simTime;
    static ContinStat lineas;
    static SimList< Event > eventos;
    static Event  now_event;
    static int llamadasAtendidas;
    static int llamadasBloqueadas;

    static double x = 0.0;

    public static void main( String[]args )throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("InputTelefonia.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("OutputTelefonia.txt"));

        /* LEER Y GUARDAR PARÁMETROS */
        totalLineas = Integer.parseInt( input.readLine() );
        means = new int[2];
        means[ (int) LLAMADA_A ] = Integer.parseInt(input.readLine());
        means[ (int) LLAMADA_B ] = Integer.parseInt(input.readLine());
        String in = input.readLine();
        min = Integer.parseInt( in.split("-")[1] );
        max = Integer.parseInt( in.split("-")[0] );
        maxTime = Double.parseDouble( input.readLine() );
        /* ESCRIBIR EMCABEZADP DEL REPORTE */
        out.write("Cantidad de lineas: "+totalLineas+"\n");

        /* INICIALIZAR */

        double it = 0;
        while( x<0.045 ) {

            inicializar();
            System.out.println("Init");
            do {
                sincronizar();
                switch ( now_event.getType() ) {
                    case LLAMADA:
                        llamada();
                        break;
                    case FIN_LLAMADA:
                        finLlamada();
                        break;
                    case FIN_SIM:
                        finSim(out);
                        break;
                }
                System.out.println( now_event.getType()+" "+simTime.getTime() );
            } while ( now_event.getType() != FIN_SIM );
            it++;
            System.out.println(it);
        }
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
        // Actualiza el tiempo, origen y evento en curso en la simulación
        now_event = eventos.getFirst();
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

        /* Añade primeras llamadas a la lista de eventos*/
        eventos.add(new Event(LLAMADA, simTime.getTime() + distExponencial(means[LLAMADA_A]),LLAMADA_A));
        eventos.add(new Event(LLAMADA, simTime.getTime() + distExponencial(means[LLAMADA_B]),LLAMADA_B));

        eventos.add(new Event(FIN_SIM, (float) maxTime));

        llamadasBloqueadas = 0;
        llamadasAtendidas = 0;
    }

    /*
      RUTINAS DE EVENTOS
     */

    private static void llamada(){
        eventos.add(new Event(LLAMADA, simTime.getTime() +
                distExponencial(means[(int) now_event.getAtribute(ORIGEN)]),
                now_event.getAtribute(ORIGEN)));
        if (lineas.getValue()>=1){
            lineas.recordContin(lineas.getValue()-1, simTime.getTime());
            eventos.add( new Event(FIN_LLAMADA, simTime.getTime()+ distUniforme(max, min)));
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

        x = (double)llamadasBloqueadas/(double)(llamadasAtendidas+llamadasBloqueadas);

        bw.write("Promedio de líneas ocupadas: "+(totalLineas-lineas.getContinAve(simTime.getTime()))+"\n");
        bw.write("Proporción promedio de líneas ocupadas: "+(totalLineas-lineas.getContinAve(simTime.getTime()))/(totalLineas)+"\n");
        bw.write("Cantidad de llamadas bloquedas: "+llamadasBloqueadas+"\n");
        bw.write("Proporción de llamadas bloquedas: "+(double)llamadasBloqueadas/(double)(llamadasAtendidas+llamadasBloqueadas)+"\n");
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