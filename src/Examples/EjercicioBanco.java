package Examples;

import java.io.*;
import simlib.*;
import simlib.collection.SimList;

import java.util.Random;

public class EjercicioBanco {
    static Random random;
    static final byte LLEGADA = 0, FIN_SERVICIO = 1, CIERRE = 2, FIN_SIM = 3;
    static final byte IDLE = 0, BUSSY = 1;
    static Timer simTime;
    static int horaCierre, cantCajeros;
    static boolean abierto;
    static float meanL, meanS;
    static Event nowEvent;
    static ContinStat[] cajeros;
    static DiscreteStat tiempoEspera;
    static SimList<Float>[] colas;
    static SimList<Event> eventos;

    public static void main(String[] args) throws Exception {
        /* ABRIR ARCHIVOS */
        SimReader input = new SimReader("InputBanco.txt");
        SimWriter out = new SimWriter("OutputBanco.txt");

        /* LEER Y GUARDAR PARÁMETROS */
        int inicio, fin;
        inicio = input.readInt();
        System.out.println(inicio);
        fin = input.readInt();
        System.out.println(fin);
        horaCierre = (fin-inicio)*60;
        meanL = input.readFloat();
        System.out.println(meanL);
        meanS = input.readFloat();
        System.out.println(meanS);
        cantCajeros = input.readInt();
        System.out.println(cantCajeros);

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
                case CIERRE:
                    cierreBanco();
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
            colas[i] = new SimList<Float>("Cola #"+(i+1), simTime, false);

        /* Crea e inicializa todos los cajeros como disponibles */
        cajeros = new ContinStat[cantCajeros];
        for (int i = 0; i < cantCajeros; i++)
            cajeros[i] = new ContinStat("Cajero #"+i, (float)IDLE, simTime);

        /* Crea la cola de eventos */
        eventos = new SimList<>("Cola de eventos", simTime, true);
        
        /* Agrega a la cola los primeros eventos */
        eventos.add(new Event(LLEGADA, distExponencial( meanL )));
        eventos.add(new Event(CIERRE, horaCierre));
        System.out.println(eventos.getFirst().getTime()+" "+eventos.getLast().getTime());

        /* Inicialmente las puertas siempre están abiertas */
        abierto = true;

        /* Inicializamos el tiempo de espera */
        tiempoEspera = new DiscreteStat("Tiempo de espera");

    }

    static void sincronizar(){
          //IMPRIME EN CONSOLA DE MANERA GRAFICA EL ESTADO DE LAS COLAS Y LOS CAJEROS
        for (int i = 0; i <cantCajeros; i++){
            colas[i].update();
            System.out.print(i+": ");
            for (int j = 0; j < colas[i].size() ; j++) {
                System.out.print("º");
            }
            System.out.println();
        }

        // Actualiza el tiempo, origen y evento en curso en la simulación
        nowEvent = eventos.getFirst();
        simTime.setTime(eventos.getFirst().getTime());

        // Elimina el evento ya procesado
        eventos.removeFirst();
    }

    static void llegada(){
        if( abierto ){
            eventos.add( new Event( LLEGADA, simTime.getTime() + distExponencial( meanL ) ) );

            SimList< Float > colaMasCorta = colas[0];
            for ( int i = 0; i < cantCajeros; i++ ) {
                if ( cajeros[i].getValue() == IDLE ){
                    cajeros[i].recordContin( BUSSY );
                    eventos.add( new Event(FIN_SERVICIO, simTime.getTime() + distExponencial( meanS ), (float)i ) );
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
        cajeros[ index ].recordContin( IDLE );
        cambiarCola( index );
        if( colas[ index ].size()>0 ){
            cajeros[ index ].recordContin( BUSSY );
            tiempoEspera.recordDiscrete( simTime.getTime()-colas[ index ].getFirst() );
            colas[ index ].removeFirst();
            eventos.add( new Event( FIN_SERVICIO, simTime.getTime() + distExponencial( meanS ), (float)index ) );
        }
    }

    static void cierreBanco(){
        abierto = false;
    }


    static void finSim(SimWriter out) throws IOException {
        float cont = 0;
        for (int i = 0; i < cantCajeros; i++) {
            cont += colas[i].getAvgSize();
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
