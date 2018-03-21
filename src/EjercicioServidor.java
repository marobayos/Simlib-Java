import simlib.*;
import java.io.*;
import java.util.Random;

public class EjercicioServidor {

    static Random random;
    static final byte LLEGADA = 0, FIN_SERVICIO = 1, RENUNCIA = 2, FIN_SIM = 3;
    static final byte IDLE = 0, BUSSY = 1;
    static Timer simTime;
    static int horaCierre, cantCajeros;
    static boolean abierto;
    static float lambdaL, meanS;
    static Event nowEvent;
    static ContinStat[] cajeros;
    static DiscreteStat tiempoEspera;
    static SimList<Float>[] colas;
    static SimList<Event> eventos;

    public static void main(String[] args)throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("InputServidor.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("OutputServidor.txt"));

        /* LEER Y GUARDAR PARÁMETROS */
        int inicio, fin;
        inicio = Integer.parseInt( input.readLine() );
        fin = Integer.parseInt( input.readLine() );
        horaCierre = ( fin-inicio )*60;
        lambdaL = Float.parseFloat( input.readLine() );
        meanS = Float.parseFloat( input.readLine() );
        cantCajeros = Integer.parseInt( input.readLine() );

        out.write("REPORTE DE SIMULACIÓN CON "+cantCajeros+" CAJEROS\n\n");

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

    static void inicializar(){
        /* Para tener datos diferentes en cada simulación */
        random = new Random( );
        random.setSeed( System.nanoTime() );

        /* Inicializa el tiempo */
        simTime = new Timer();

        /* Crea e inicializa todas las colas con ningún cliente en ellas */
        colas = new SimList[cantCajeros];
        for (int i = 0; i < cantCajeros; i++)
            colas[i] = new SimList<Float>("Cola #"+(i+1), 0, false);

        /* Crea e inicializa todos los cajeros como disponibles */
        cajeros = new ContinStat[cantCajeros];
        for (int i = 0; i < cantCajeros; i++)
            cajeros[i] = new ContinStat((float)IDLE, 0, "Cajero #"+i);

        /* Crea la cola de eventos */
        eventos = new SimList<>("Cola de eventos", 0, true);

        /* Agrega a la cola los primeros eventos */
        eventos.add(new Event(LLEGADA, distExponencial( lambdaL )));
        eventos.add(new Event(RENUNCIA, horaCierre));
        System.out.println(eventos.getFirst().getTime()+" "+eventos.getLast().getTime());

        /* Inicialmente las puertas siempre están abiertas */
        abierto = true;

        /* Inicializamos el tiempo de espera */
        tiempoEspera = new DiscreteStat("TIEMPO DE ESPERA");
    }

    static void sincronizar(){
        // Actualiza el tiempo, origen y evento en curso en la simulación
        nowEvent = eventos.getFirst();
        simTime.setTime(eventos.getFirst().getTime());

        // Elimina el evento ya procesado
        eventos.removeFirst();
    }

    static void llegada(){
        eventos.add( new Event( LLEGADA, simTime.getTime() + distExponencial( lambdaL ) ) );
        if( abierto ){

            SimList< Float > colaMasCorta = colas[0];
            for ( int i = 0; i < cantCajeros; i++ ) {
                if ( cajeros[i].getValue() == IDLE ){
                    cajeros[i].recordContin( BUSSY, simTime.getTime() );
                    float[] atributos = {i};
                    eventos.add( new Event( FIN_SERVICIO, simTime.getTime() + distExponencial( meanS ), atributos ) );
                    colaMasCorta = null;
                    tiempoEspera.recordDiscrete(0);
                    break;
                } else if( colas[i].size() < colaMasCorta.size() )
                    colaMasCorta = colas[i];
            }
            if (colaMasCorta != null){
                colaMasCorta.addLast( simTime.getTime() );
            }
        }
    }

    static void finServicio(){
        int index = (int)nowEvent.getAtribute(0);
        cajeros[ index ].recordContin( IDLE, simTime.getTime() );
        cambiarCola( index );
        if( colas[ index ].size()>0 ){
            cajeros[ index ].recordContin(BUSSY, simTime.getTime());
            tiempoEspera.recordDiscrete( simTime.getTime()-colas[ index ].getFirst() );
            colas[ index ].removeFirst();
            float[] atributos = { index };
            eventos.add( new Event( FIN_SERVICIO, simTime.getTime() + distExponencial( meanS ), atributos ) );
        }
    }

    static void renunciaCliente(){
        abierto = false;
    }


    static void finSim(BufferedWriter out) throws IOException {
        float cont = 0;
        for (int i = 0; i < cantCajeros; i++) {
            cont += colas[i].getAvgSize(simTime.getTime());
        }
        out.write("PROMEDIO DE CLIENTES EN COLA: "+cont+"\n\n");
        tiempoEspera.report(out);

    }

    static void cambiarCola(int index){
        for (int i = 0; i < cantCajeros; i++) {
            if (colas[i].size() + cajeros[i].getValue() > colas[index].size() + cajeros[index].getValue() + 1){
                colas[index].addLast(colas[i].removeLast());
                cambiarCola(i);
                break;
            }
        }
    }

    /**
     * Distribución exponencial
     *
     * @param mean    1/lambda media de la distribución
     * @return  valor aleatorio con distribucuón exponencial.
     */
    private static float distExponencial(double mean){
        return (float)(-mean*Math.log(random.nextFloat()));
    }

}
