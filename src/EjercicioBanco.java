import java.io.*;
import simlib.*;
import java.util.Random;

public class EjercicioBanco {
    static Random random;
    static final byte LLEGADA = 0, FIN_SERVICIO = 1, CIERRE = 2, FIN_SIM = 3;
    static final byte IDLE = 0, BUSSY = 1;
    static Timer simTime;
    static int horaCierre, cantCajeros;
    static boolean puertasAbiertas;
    static float meanL, meanS;
    static byte eventType;
    static ContinStat[] cajeros;
    static SimList<Float>[] colas;
    static SimList<Event> eventos;

    public static void main(String[] args)throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("InputBanco.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("OutputBanco.txt"));

        /* LEER Y GUARDAR PARÁMETROS */
        int inicio, fin;
        inicio = Integer.parseInt( input.readLine() );
        fin = Integer.parseInt( input.readLine() );
        horaCierre = (fin-inicio)*60*60;
        meanL = Float.parseFloat( input.readLine() );
        meanS = Float.parseFloat( input.readLine() );
        cantCajeros = Integer.parseInt( input.readLine() );

        /* INICIALIZAR */
        inicializar();
        System.out.println("Init");
        do {
            sincronizar();
            switch ( eventType ) {
                case LLEGADA:
                    llegada();
                    break;
                case FIN_SERVICIO:
                    finServicio();
                    break;
                case CIERRE:
                    cierreBanco();
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

    static void inicializar(){
        /* Para tener datos diferentes en cada simulación */
        random = new Random( );
        random.setSeed( System.nanoTime() );

        /* Inicializa el tiempo */
        simTime = new Timer();

        /* Crea e inicializa todas las colas con ningún cliente en ellas */
        colas = new SimList[cantCajeros];
        for (int i = 0; i < cantCajeros; i++)
            colas[i] = new SimList<>("Cola #"+(i+1), 0, false);

        /* Crea e inicializa todos los cajeros como disponibles */
        cajeros = new ContinStat[cantCajeros];
        for (int i = 0; i < cantCajeros; i++)
            cajeros[i] = new ContinStat((float)IDLE, 0, "Canero #"+i);

        /* Crea la cola de eventos */
        eventos = new SimList<>("Cola de eventos", 0, true);
        
        /* Agrega a la cola los primeros eventos */
        eventos.add(new Event(LLEGADA, distExponencial( meanL )));
        eventos.add(new Event(FIN_SERVICIO, horaCierre));

        /* Inicialmente las puertas siempre están abiertas */
        puertasAbiertas = true;
    }

    static void sincronizar(){

    }

    static void llegada(){}

    static void finServicio(){}

    static void cierreBanco(){}

    static void finSim(BufferedWriter out){}

    /**
     * Distribución exponencial
     *
     * @param mean    1/lambda media de la distribución
     * @return  valor aleatorio con distribucuón exponencial.
     */
    private static float distExponencial(double mean){
        return (float)(mean*Math.log(random.nextFloat()));
    }
}
