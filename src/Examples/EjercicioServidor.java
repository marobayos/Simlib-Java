package Examples;

import simlib.*;
import java.io.*;
import java.util.Random;

public class EjercicioServidor {

    static Random random;
    static final byte LLEGADA = 0, FIN_SERVICIO = 1, RENUNCIA = 2, FIN_SIM = 3;
    static final byte IDLE = 0, BUSSY = 1;
    static Timer simTime;
    static float lambdaL, meanS, meanZP, meanTolerance[] = new float[3],mean_max_tiempo_cola,
            max_tiempo_sim;
    static Event nowEvent;
    static ContinStat servidor;
    static DiscreteStat tiempoEspera,tiempoRenuncia, tiempoSistema;
    static SimList<Event> eventos;
    static SimList<SimListObject> colaClientes;
    static int clientesNoIngresan;

    public static void main(String[] args)throws IOException {
        /* ABRIR ARCHIVOS */
        SimReader input = new SimReader("InputServidor.txt");
        SimWriter out = new SimWriter("OutputServidor.txt");

        /* LEER Y GUARDAR PARÁMETROS */
        lambdaL = Float.parseFloat( input.readLine() );
        meanS = Float.parseFloat( input.readLine() );
        String tria[] = input.readLine().split(" ");
        meanTolerance[0] = Float.parseFloat(tria[0]);
        meanTolerance[1] = Float.parseFloat(tria[1]);
        meanTolerance[2] = Float.parseFloat(tria[2]);
        mean_max_tiempo_cola = Float.parseFloat(input.readLine());
        meanZP = Float.parseFloat(input.readLine());
        max_tiempo_sim = Float.parseFloat(input.readLine());

        /* INICIALIZAR */
        inicializar();
        System.out.println("Init");
        do {
            //System.out.println(eventos);
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
        } while ( nowEvent.getType() != FIN_SIM );
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
        servidor = new ContinStat( (float)IDLE, simTime, "SERVIDOR" );

        colaClientes = new SimList<>("Cola de Clientes", simTime,false);

        /* Crea la cola de eventos */
        eventos = new SimList<>("Cola de eventos", simTime, true);

        /* Agrega a la cola los primeros eventos */
        eventos.add(new Event(LLEGADA, distExponencial( lambdaL )));

        eventos.add( new Event(FIN_SIM, max_tiempo_sim));
        System.out.println(eventos.getFirst().getTime()+" "+eventos.getLast().getTime());

        /* Inicializamos el tiempo de espera */
        tiempoEspera = new DiscreteStat("TIEMPO DE ESPERA");
        tiempoRenuncia = new DiscreteStat("TIEMPO DE CLIENTES QUE RENUNCIAN");
        tiempoSistema = new DiscreteStat("TIEMPO DE CLIENTES EN EL SISTEMA");
        clientesNoIngresan = 0;
    }

    static void sincronizar(){
        colaClientes.update();
        System.out.print(servidor.getValue()+": ");
        for (int i = 0; i < colaClientes.size() ; i++) {
            System.out.print("º");
        }
        System.out.println();

        // Actualiza el tiempo, origen y evento en curso en la simulación
        nowEvent = eventos.getFirst();
        simTime.setTime( eventos.getFirst().getTime() );

        // Elimina el evento ya procesado
        eventos.removeFirst();

    }

    static void llegada(){
        float tolerancia = disTriangular(meanTolerance[0], meanTolerance[1], meanTolerance[2]);
        eventos.add( new Event( LLEGADA, simTime.getTime() + distExponencial( lambdaL ) ) );
        SimListObject cliente = new SimListObject(simTime.getTime(),(float)distPoisson(meanZP));
        if( tolerancia >= colaClientes.size() ){
            if ( servidor.getValue() == IDLE ){
                float tiempoServicio =  distExponencial( meanS );
                servidor.recordContin( BUSSY );
                tiempoEspera.recordDiscrete( 0  );
                eventos.add( new Event( FIN_SERVICIO,simTime.getTime() + tiempoServicio ) );
                tiempoSistema.recordDiscrete( tiempoServicio );
            } else {
                colaClientes.add( cliente );
                eventos.add( new Event( RENUNCIA, simTime.getTime() + distErlang( mean_max_tiempo_cola ),
                        cliente.getAtribute(0), cliente.getAtribute(1)) );
            }
        } else
            clientesNoIngresan ++;
    }

    static void renunciaCliente(){
        SimListObject cliente = null;
        for( SimListObject cl : colaClientes ){
            if (cl.getIndex() == nowEvent.getAtribute(0)){
                System.out.println("YEIII");
                cliente = cl;
            }
        }
        if (cliente != null && colaClientes.indexOf(cliente) >= cliente.getAtribute(1)){
            colaClientes.remove(cliente);
            tiempoRenuncia.recordDiscrete( simTime.getTime()-cliente.getAtribute(0));
        }
    }

    static void finServicio(){
        SimListObject cliente = null;
        if( colaClientes.isEmpty() ){
            servidor.recordContin((float) IDLE);
        } else {
            float tiempoServicio =  distExponencial( meanS );
            cliente = colaClientes.removeFirst();
            Event renuncia = null;
            for( Event cl : eventos ){
                try {
                    if (cl.getAtribute(0) == cliente.getIndex()) {
                        renuncia = cl;
                    }
                } catch (Exception ex){

                }
            }
            eventos.remove(renuncia);
            tiempoEspera.recordDiscrete( simTime.getTime()-cliente.getIndex() );
            eventos.add( new Event( FIN_SERVICIO,simTime.getTime() + tiempoServicio ) );
            tiempoSistema.recordDiscrete( simTime.getTime() - cliente.getIndex() + tiempoServicio );
        }
    }


    static void finSim(SimWriter out) throws IOException {
        tiempoSistema.report(out);
        tiempoEspera.report(out);
        tiempoRenuncia.report(out);
        colaClientes.report(out, simTime.getTime());
        servidor.report(out);
        out.write("NUMERO DE CLIENTES QUE RENUNCIAN: "+tiempoRenuncia.getDiscreteObs()+"\n\n");
        out.write("NUMERO DE CLIENTES QUE NO INGRESAN: "+ clientesNoIngresan+"\n\n");
    }

    /**
     * Distribución exponencial
     *
     * @param lambda    1/lambda media de la distribución
     * @return  valor aleatorio con distribucuón exponencial.
     */
    private static float distExponencial( double lambda ){
        return (float)(-lambda*Math.log(random.nextFloat()));
    }

    static float disTriangular( double a, double b, double c ){
        double rand = random.nextDouble();
        double x;
        double aux;
        if( rand <= ((b-a)/c-a) ){
            aux = Math.sqrt( ( c-a )*( b-a )*rand );
            x = a + aux;
        } else {
            aux = Math.sqrt( ( c-a )*( c-b )*( 1-rand ) );
            x = c - aux;
        }
        return ( float )x;
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
        return distExponencial(mean/2) + distExponencial(mean/2);
    }
}
