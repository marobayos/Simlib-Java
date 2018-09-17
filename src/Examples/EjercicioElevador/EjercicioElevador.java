package Examples.EjercicioElevador;

import simlib.*;
import simlib.collection.SimList;

import java.io.*;
import java.util.Random;

public class EjercicioElevador {

    static final byte LLEGADA_A = 0, LLEGADA_B = 1, LLEGADA_C = 2, DESCARGA = 3, REGRESO = 4, FIN_SIM = 5;
    static final byte IDLE = 0, BUSSY = 1;
    static int capacidad;
    static int pesoEnElevador;
    static Random random;
    static int maxA, minA, valB, distC;
    static double maxTime;
    static Timer simTime;
    static DiscreteStat transitoA, esperaB;
    static int totalC;
    static SimList<Box> cajasATransportar;
    static SimList< Event > eventos;
    static SimList<Box> cajasFaltantes;
    static ContinStat elevador;
    static byte eventType;
    static int viajes = 0;

    public static void main(String[]args)throws IOException {
        /* ABRIR ARCHIVOS */
        SimReader input = new SimReader("InputElevador.txt") ;
        SimWriter out = new SimWriter("OutputElevador.txt");

        /* LEER Y GUARDAR PARÁMETROS */
        capacidad = input.readInt();
        input.useDelimiter('-');
        maxA = input.readInt();
        minA = input.readInt();
        valB = input.readInt();
        distC = input.readInt();
        maxTime = input.readDouble();

        /* INICIALIZAR */
        inicializar();
        System.out.println("Init");
        do {
            sincronizar();
            switch ( eventType ) {
                case LLEGADA_A:
                    llegadaA();
                    break;
                case LLEGADA_B:
                    llegadaB();
                    break;
                case LLEGADA_C:
                    llegadaC();
                    break;
                case DESCARGA:
                    descarga();
                    break;
                case REGRESO:
                    regreso();
                    break;
                case FIN_SIM:
                    finSim( out );
                    break;
            }
            System.out.println(cajasFaltantes);
            System.out.println(cajasATransportar);
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

        //Actualiza acumuladores estadísticos
        cajasFaltantes.update();
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
        eventos = new SimList<Event>("Lista de Eventos", simTime, true);

        /* Programa la primera llevada de cada tipo de caja */
        eventos.add( new Event( LLEGADA_A , simTime.getTime()+distUniforme( maxA, minA ) ) );
        eventos.add( new Event( LLEGADA_B , simTime.getTime()+valB) );
        eventos.add( new Event( LLEGADA_C , simTime.getTime()+distC() ) );

        /* Programa el fin de la simulación */
        eventos.add( new Event( FIN_SIM , (float)maxTime ) );

        /* Inicializa las demás colas y listas */
        cajasATransportar = new SimList<Box>("Cajas a transportar", simTime, false);
        cajasFaltantes = new SimList("Cajas faltantes", simTime, false);

        /* Inicializa las variables de estado y acumuladores */
        elevador = new ContinStat( "estado del elevador", (float)0.0, simTime);
        transitoA = new DiscreteStat("Tiempo de tránsito para cajas A");
        esperaB = new DiscreteStat("Tiempo de espera para cajas B");
        totalC = 0;
        pesoEnElevador = 0;
    }

    /**
     * RUTINAS DE EVENTOS
     **********************/

    /**
     * LLegada de una caja tipo A: Programa siguiente evento de este tipo. Si el
     * elevador está disponible verifica si puede ingresar la caja y subirlo, en
     * ese caso lo carga y programa su descarga, en caso contrario solo añade la
     * caja a la cola de cajas faltantes.
     */
    private static void llegadaA() {
        /* Programa siguiente llegada de caja tipo A */
        eventos.add(new Event(LLEGADA_A, simTime.getTime() + distUniforme(maxA, minA)));
        if(elevador.getValue() == IDLE && pesoEnElevador + 200 <= capacidad){
            cajasATransportar.add( new Box(simTime.getTime(), 'A') );
            pesoEnElevador += 200;
            if (pesoEnElevador == capacidad){
                cargarElevador();
                eventos.add( new Event(DESCARGA, simTime.getTime() + 3) );
            }
        } else {
            cajasFaltantes.addLast( new Box(simTime.getTime(), 'A') );
        }
    }

    /**
     * LLegada de una caja tipo B: Programa siguiente evento de este tipo. Si el
     * elevador está disponible verifica si puede ingresar la caja y subirlo, en
     * ese caso lo carga y programa su descarga, en caso contrario solo añade la
     * caja a la cola de cajas faltantes.
     */
    private static void llegadaB() {
        eventos.add(new Event(LLEGADA_B, simTime.getTime() + valB));
        if(elevador.getValue() == IDLE && pesoEnElevador + 100 <= capacidad){
            cajasATransportar.add( new Box(simTime.getTime(), 'B' ));
            pesoEnElevador += 100;
            if (pesoEnElevador == capacidad){
                cargarElevador();
                eventos.add( new Event(DESCARGA, simTime.getTime() + 3) );
            }
        } else {
            cajasFaltantes.addLast( new Box(simTime.getTime(), 'B') );
        }
    }

    /**
     * LLegada de una caja tipo B: Programa siguiente evento de este tipo. Si el
     * elevador está disponible verifica si puede ingresar la caja y subirlo, en
     * ese caso lo carga y programa su descarga, en caso contrario solo añade la
     * caja a la cola de cajas faltantes.
     */
    private static void llegadaC() {
        eventos.add(new Event(LLEGADA_C, simTime.getTime() + distUniforme(maxA, minA)));
        if(elevador.getValue() == IDLE && pesoEnElevador + 50 <= capacidad){
            cajasATransportar.add( new Box(simTime.getTime(), 'C') );
            pesoEnElevador += 50;
            if (pesoEnElevador == capacidad){
                cargarElevador();
                eventos.add( new Event(DESCARGA, simTime.getTime() + 3) );
            }
        } else {
            cajasFaltantes.addLast( new Box(simTime.getTime(), 'C') );
        }
    }

    /**
     * Descarga del elevador en 2do piso: programa regreso del elevador al 1er piso,
     * actualiza acumuladores estadísticos y variables de estado del sistema. Vacía
     * la cola de cajas a transportar.
     */
    private static void descarga(){
        eventos.add(new Event(REGRESO, simTime.getTime() + 1));
        for (Box caja : cajasATransportar){
            switch (caja.getBoxType()){
                case 'A':
                    transitoA.recordDiscrete(simTime.getTime() - caja.getArriveTime());
                    break;
                case 'C':
                    totalC ++;
                    break;
            }
            pesoEnElevador -= caja.getWeight();
        }
        cajasATransportar.clear();
    }

    private static void cargarElevador(){
        elevador.recordContin( BUSSY );
        for (Box caja : cajasATransportar){
            if (caja.getBoxType() == 'B'){
                esperaB.recordDiscrete(simTime.getTime()-caja.getArriveTime());
            }
        }
    }

    /**
     * Regreso del elevador al 1er piso: marca el elevador disponible, mete las cajas
     * que quepan en el elevador en la lista de cajas a transportar, si la capacidad
     * se completa, carga el elevador y programa su descarga.
     */
    private static void regreso(){
        elevador.recordContin( IDLE );
        SimList<Box> cajasRestantes = new SimList<>(simTime);
        for(Box caja : cajasFaltantes){
            if (caja.getWeight() + pesoEnElevador <= capacidad){
                cajasATransportar.add(caja);
                pesoEnElevador += caja.getWeight();
            } else
                cajasRestantes.add(caja);
        }
        cajasFaltantes.clear();
        cajasFaltantes.addAll(cajasRestantes);
        if ( pesoEnElevador == capacidad ){
            cargarElevador();
            eventos.add( new Event( DESCARGA, simTime.getTime() + 3 ) );
        }
    }

    /**
     * Fin de la simulación: Actualiza una última vez las variables del sistema, y
     * guarda en el archivo los datos obtenidos para las medidas de desempeño.
     *
     * @param bw   archivo en el que se guardarán los datos.
     */
    private static void finSim(SimWriter bw) throws IOException {
        elevador.report(bw);
        transitoA.report(bw);
        esperaB.report(bw);
        cajasFaltantes.report(bw);
        bw.write("Promedio de cajas C transportadas por hora: "+totalC/simTime.getTime()*60);
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
     * Distribución especial para la caja tipo C.
     *      – Opción 1: Distribución de la forma P(x)=X
     *      – Opción 2: Distribución de exponencial con media 6.
     *
     * @return variable aleatoria perteneciente a la distribución seleccionada.
     */
    private static float distC(){
        double rand = random.nextDouble();
        if(distC == 0){
            if(rand<0.33){
                return 2;
            }  else {
                return 3;
            }
        } else {
            return distExponencial(6);
        }
    }

    static float disTriangular( double a, double b, double c ){
        double rand = random.nextDouble();
        double x;
        double aux;
        if( rand <= ((b-a)/c-a) ){
            aux = Math.sqrt(((c-a)*(b-a)*rand));
            x = a + aux;
        }else{
            aux = Math.sqrt((c-a)*(c-b)*(1-rand));
            x = c - aux;
        }
        return (float)x;
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