import simlib.*;
import java.io.*;
import java.util.Random;

public class EjercicioServidor {

    static Random random;
    static final byte LLEGADA = 0, FIN_SERVICIO = 1, RENUNCIA = 2, FIN_SIM = 3;
    static final byte IDLE = 0, BUSSY = 1;
    static Timer simTime;
    static float lambdaL, meanS, meanZP;
    static int  lambdaTE;
    static Event nowEvent;
    static ContinStat servidor;
    static DiscreteStat tiempoEspera;
    static SimList<Float>[] colas;
    static SimList<Event> eventos;
    static SimList<SimListObject> colaClientes;
    static int clientesNoIngresan;
    static int clientesRenuncian;

    public static void main(String[] args)throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("InputServidor.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("OutputServidor.txt"));

        /* LEER Y GUARDAR PARÁMETROS */
        int inicio, fin;
        inicio = Integer.parseInt( input.readLine() );
        fin = Integer.parseInt( input.readLine() );
        lambdaL = Float.parseFloat( input.readLine() );
        meanS = Float.parseFloat( input.readLine() );


        /* INICIALIZAR */
        inicializar();
        System.out.println("Init");
        do {
            sincronizar();
            switch ( nowEvent.getType() ) {
                case LLEGADA:
                    llegada();
                    break;
                case FIN_SERVICIO:
                    finServicio();
                    break;
                case RENUNCIA:
                    renunciaCliente();
                    break;
                case FIN_SIM:
                    finSim( out );
                    break;
            }
            System.out.println(eventos);
        } while ( eventos.size()>0 );
        finSim(out);
        /* CERRAR ARCHIVOS */
        input.close();
        out.close();
    }

    static void finServicio(){}

    static void inicializar(){
        /* Para tener datos diferentes en cada simulación */
        random = new Random();
        random.setSeed( System.nanoTime() );

        /* Inicializa el tiempo */
        simTime = new Timer();

        /* Crea e inicializa todos los cajeros como disponibles */
        servidor = new ContinStat( (float)IDLE, simTime.getTime(), "SERVIDOR" );

        /* Crea la cola de eventos */
        eventos = new SimList<>("Cola de eventos", 0, true);

        /* Agrega a la cola los primeros eventos */
        eventos.add(new Event(LLEGADA, distExponencial( lambdaL )));
        System.out.println(eventos.getFirst().getTime()+" "+eventos.getLast().getTime());

        /* Inicializamos el tiempo de espera */
        tiempoEspera = new DiscreteStat("TIEMPO DE ESPERA");
    }

    static void sincronizar(){
        // Actualiza el tiempo, origen y evento en curso en la simulación
        nowEvent = eventos.getFirst();
        simTime.setTime( eventos.getFirst().getTime() );

        // Elimina el evento ya procesado
        eventos.removeFirst();
    }

    static void llegada(){
        float tolerancia = disTriangular(3, 6, 15);
        eventos.add( new Event( LLEGADA, simTime.getTime() + distExponencial( lambdaL ) ) );
        if( tolerancia >= colaClientes.size() ){
            if (servidor.getValue() == IDLE){
                servidor.recordContin(BUSSY, simTime.getTime());
                tiempoEspera.recordDiscrete(0);
                eventos.add( new Event( FIN_SERVICIO, simTime.getTime() + distExponencial( lambdaL ) ) );
            } else {
                Float values[] = {simTime.getTime(), (float)distPoisson(meanZP)};
                SimListObject cliente = new SimListObject(0, values);
                colaClientes.add( cliente );
                eventos.add( new Event( RENUNCIA, simTime.getTime() + distErlang( lambdaTE ), values ) );
            }
        } else
            clientesNoIngresan ++;
    }

    static void renunciaCliente(){
        int index = colaClientes.indexOf( new Client( nowEvent.getAtribute(0), 0 ) );
        if (index != -1){
            clientesRenuncian ++;
            colaClientes.remove(index);
        }
    }


    static void finSim(BufferedWriter out) throws IOException {
        float cont = 0;
        out.write("PROMEDIO DE CLIENTES EN COLA: "+cont+"\n\n");
        tiempoEspera.report(out);

    }

    /**
     * Distribución exponencial
     *
     * @param lambda    1/lambda media de la distribución
     * @return  valor aleatorio con distribucuón exponencial.
     */
    private static float distExponencial(double lambda){
        return (float)(-lambda*Math.log(random.nextFloat()));
    }

    static float disTriangular( double a, double b, double c ){
        double rand = random.nextDouble();
        double x;
        double aux;
        if( rand <= ((b-a)/c-a) ){
            aux = Math.sqrt( ( c-a )*( b-a )*rand );
            x = a + aux;
        }else{
            aux = Math.sqrt( ( c-a )*( c-b )*( 1-rand ) );
            x = c - aux;
        }
        return (float)x;
    }

    public static int distPoisson( double lambda ){
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return k - 1;
    }

    public static float distErlang( int mean ){
        return distExponencial(2/mean) + distExponencial(2/mean);
    }
}
