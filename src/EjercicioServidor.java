import simlib.*;
import java.io.*;
import java.util.Random;

public class EjercicioServidor {

    static Random random;
    static final byte LLEGADA = 0, FIN_SERVICIO = 1, RENUNCIA = 2, FIN_SIM = 3;
    static final byte IDLE = 0, BUSSY = 1, a= 0, b = 2, c = 1;
    static Timer simTime;
    static float lambdaL, meanS, meanZP, meanTolerance[] = new float[3],mean_max_tiempo_cola,
            max_tiempo_sim;
    static Event nowEvent;
    static ContinStat servidor;
    static DiscreteStat tiempoEspera,tiempoSistema,tiempo_renuncia;
    static SimList<Float>[] colas;
    static SimList<Event> eventos;
    static SimList<SimListObject> colaClientes;
    static SimListObject cliente_en_el_servidor;
    static int clientesNoIngresan;

    public static void main(String[] args)throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("InputServidor.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("OutputServidor.txt"));

        /* LEER Y GUARDAR PARÁMETROS */
        lambdaL = Float.parseFloat( input.readLine() );
        meanS = Float.parseFloat( input.readLine() );
        String tria[] = input.readLine().split(" ");
        meanTolerance[a] = Float.parseFloat(tria[a]);
        meanTolerance[b] = Float.parseFloat(tria[b]);
        meanTolerance[c] = Float.parseFloat(tria[c]);
        mean_max_tiempo_cola = Float.parseFloat(input.readLine());
        meanZP = Float.parseFloat(input.readLine());
        max_tiempo_sim = Float.parseFloat(input.readLine());


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
        } while ( nowEvent.getType() != FIN_SIM );
        finSim(out);
        /* CERRAR ARCHIVOS */
        input.close();
        out.close();
    }

    static void inicializar(){
        /* Para tener datos diferentes en cada simulación */
        random = new Random();
        random.setSeed( System.nanoTime() );

        /* Inicializa el tiempo */
        simTime = new Timer();

        /* Crea e inicializa todos los cajeros como disponibles */
        servidor = new ContinStat( (float)IDLE, simTime.getTime(), "SERVIDOR" );

        colaClientes = new SimList<>("Cola de Clientes", 0,false);

        /* Crea la cola de eventos */
        eventos = new SimList<>("Cola de eventos", 0, true);

        /* Agrega a la cola los primeros eventos */
        eventos.add(new Event(LLEGADA, distExponencial( lambdaL )));

        eventos.add( new Event(FIN_SIM,max_tiempo_sim));
        System.out.println(eventos.getFirst().getTime()+" "+eventos.getLast().getTime());

        /* Inicializamos el tiempo de espera */
        tiempoEspera = new DiscreteStat("TIEMPO DE ESPERA");
        tiempoSistema = new DiscreteStat("TIEMPO EN EL SISTEMA");
        tiempo_renuncia = new DiscreteStat("TIEMPO DE CLIENTES QUE RENUNCIAN");

        clientesNoIngresan = 0;
    }

    static void sincronizar(){
        // Actualiza el tiempo, origen y evento en curso en la simulación
        nowEvent = eventos.getFirst();
        simTime.setTime( eventos.getFirst().getTime() );

        // Elimina el evento ya procesado
        eventos.removeFirst();
    }

    static void llegada(){
        float tolerancia = disTriangular(meanTolerance[a], meanTolerance[c], meanTolerance[b]);
        eventos.add( new Event( LLEGADA, simTime.getTime() + distExponencial( lambdaL ) ) );
        SimListObject cliente = new SimListObject(simTime.getTime(),(float)distPoisson(meanZP));
        if( tolerancia >= colaClientes.size() ){
            if (servidor.getValue() == IDLE){
                servidor.recordContin(BUSSY, simTime.getTime());
                tiempoEspera.recordDiscrete(0);
                cliente_en_el_servidor = cliente;
                eventos.add( new Event( FIN_SERVICIO, simTime.getTime() + distExponencial( lambdaL ) ) );
            } else {
                colaClientes.add( cliente );
                colaClientes.update(simTime.getTime());
                eventos.add( new Event( RENUNCIA, simTime.getTime() + distErlang( mean_max_tiempo_cola ),
                        cliente.getAtribute(0),cliente.getAtribute(1)) );
            }
        } else
            clientesNoIngresan ++;
    }

    static void renunciaCliente(){
        int index = colaClientes.indexOf( new Client( nowEvent.getTime(), 0 ) );
        if (index != -1){
            SimListObject cliente = colaClientes.get(index);
            colaClientes.remove(index);
            tiempo_renuncia.recordDiscrete(simTime.getTime()-cliente.getAtribute(LLEGADA));
            colaClientes.update(simTime.getTime());
        }
    }

    static void finServicio(){
        SimListObject cliente = null;
        if(colaClientes.isEmpty()){
            servidor.recordContin((float) IDLE, simTime.getTime());
        }else{
            cliente = colaClientes.removeFirst();
            tiempoEspera.recordDiscrete(simTime.getTime());
            colaClientes.update(simTime.getTime());
            eventos.add( new Event(FIN_SERVICIO,simTime.getTime() + distExponencial(meanS)));
        }
        tiempoSistema.recordDiscrete(simTime.getTime()-cliente_en_el_servidor.getAtribute(LLEGADA));
        servidor.recordContin((float) BUSSY, simTime.getTime());
        cliente_en_el_servidor = cliente;
    }


    static void finSim(BufferedWriter out) throws IOException {
        float cont = 0;
        out.write("PROMEDIO DE TIEMPO DE ESPERA EN EL SISTEMA: "
                + tiempoSistema.getDiscreteAverage() +"\n\n");
        out.write("PROMEDIO DE TIEMPO DE ESPERA EN LA COLA:" +
                " "+tiempoEspera.getDiscreteAverage()+"\n\n");
        out.write("PROMEDIO DE ESPERA DE DE CLIENTES QUE RENUNCIAN: "+
                tiempo_renuncia.getDiscreteAverage()+"\n\n");
        out.write("NUMERO PROMEDIO DE CLIENTES EN LA COLA: "+
                colaClientes.getAvgSize(simTime.getTime())+"\n\n");
        out.write("PROMEDIO DE OCUPACION DEL SERVIDOR: "+
                servidor.getContinAve(simTime.getTime())+"\n\n");
        out.write("NUMERO DE CLIENTES QUE RENUNCIAN: "+tiempo_renuncia.getDiscreteObs()+"\n\n");
        out.write("NUMERO DE CLIENTES QUE NO INGRESAN: "+ clientesNoIngresan+"\n\n");
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

    public static float distErlang( double mean ){
        return distExponencial(2/mean) + distExponencial(2/mean);
    }
}
