import simlib.*;

import java.io.*;
import java.util.LinkedList;
import java.util.Random;

public class Main {
    static final byte LLEGADA_A = 0, LLEGADA_B = 1, LLEGADA_C = 2, DESCARGA = 3, REGRESO = 4, FIN_SIM = 5;
    static final byte IDLE = 0, BUSSY = 1;
    static final int capacidad = 400;
    static int pesoEnElevador;
    static Random random;
    static int maxA, minA, valB, distC;
    static double maxTime;
    static Timer simTime;
    static DiscreteStat transitoA, esperaB;
    static int totalC;
    static SimList<Box> cajasATransportar;
    static SimList< Event > eventos;
    static SimList< Box > cajasFaltantes;
    static ContinStat elevador;

    public static void main(String[]args)throws IOException {
        /* ABRIR ARCHIVOS */
        FileWriter output = new FileWriter( "Output.txt" );
        BufferedReader input = new BufferedReader( new FileReader("Input.txt") );

        /* LEER Y GUARDAR PARÁMETROS */
        String in = input.readLine();
        maxA = Integer.parseInt( in.split("-")[1] );
        minA = Integer.parseInt( in.split("-")[0] );
        valB = Integer.parseInt( input.readLine() );
        distC = Integer.parseInt( input.readLine() );
        maxTime = Double.parseDouble( input.readLine() );

        //INICIALIZAR
        inicializar();
        do {
            sincronizar();
            switch ( eventos.getFirst().getType() ) {
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
                    break;
            }
        } while ( eventos.getFirst( ).getType( ) != FIN_SIM );
    }

    static void sincronizar() {
        eventos.removeLast();
        cajasFaltantes.update(simTime.getTime());
        simTime.setTime(eventos.getFirst().getTime());
    }

    static void inicializar( ) {
        random = new Random( );
        random.setSeed( System.nanoTime() );
        simTime = new Timer( );
        eventos = new SimList<Event>("Lista de Eventos", 0, true);
        eventos.add( new Event( LLEGADA_A , simTime.getTime()+distUniforme( maxA, minA ) ) );
        eventos.add( new Event( LLEGADA_B , simTime.getTime()+valB) );
        eventos.add( new Event( LLEGADA_C , simTime.getTime()+distC() ) );
        eventos.add( new Event( FIN_SIM , (float)maxTime ) );
        cajasATransportar = new SimList<Box>("Cajas a transportar", 0, false);
        cajasFaltantes = new SimList("Cajas faltantes", 0, false);
        elevador = new ContinStat(0, simTime.getTime());
        transitoA = new DiscreteStat();
        esperaB = new DiscreteStat();
        totalC = 0;
        pesoEnElevador = 0;
    }

    static void llegadaA() {
        eventos.add(new Event(LLEGADA_A, simTime.getTime() + distUniforme(maxA, minA)));
        Box estaCaja = new Box( simTime.getTime(), 'A');
        if(pesoEnElevador + estaCaja.getWeight() <= capacidad){
            cajasATransportar.add(estaCaja);
            if (pesoEnElevador == capacidad){
                cargarElevador();
                eventos.add( new Event(DESCARGA, simTime.getTime()) );
            }
        } else
            cajasFaltantes.addLast(estaCaja);
    }

    static void llegadaB() {
        eventos.add(new Event(LLEGADA_B, simTime.getTime() + valB));
        Box estaCaja = new Box( simTime.getTime(), 'B');
        if(pesoEnElevador + estaCaja.getWeight() <= capacidad){
            cajasATransportar.add(estaCaja);
            if (pesoEnElevador == capacidad){
                cargarElevador();
                eventos.add( new Event(DESCARGA, simTime.getTime()) );
            }
        } else
            cajasFaltantes.addLast(estaCaja);
    }

    static void llegadaC() {
        eventos.add(new Event(LLEGADA_C, simTime.getTime() + distUniforme(maxA, minA)));
        Box estaCaja = new Box( simTime.getTime(), 'C');
        if(pesoEnElevador + estaCaja.getWeight() <= capacidad){
            cajasATransportar.add(estaCaja);
            if (pesoEnElevador == capacidad){
                cargarElevador();
                eventos.add( new Event(DESCARGA, simTime.getTime() + 3) );
            }
        } else
            cajasFaltantes.addLast(estaCaja);
    }

    static void descarga(){
        eventos.add(new Event(REGRESO, simTime.getTime() + 1));
        for (Box caja : cajasATransportar){
            if (caja.getBoxType() == 'A'){
                transitoA.recordDiscrete(simTime.getTime() - caja.getArriveTime());
            }
            pesoEnElevador -= caja.getWeight();
        }
        cajasATransportar.clear();
    }

    static void regreso(){
        elevador.recordContin(IDLE, simTime.getTime());
        for(Box caja : cajasFaltantes){
            if (caja.getWeight() + pesoEnElevador <capacidad){
                cajasATransportar.add(caja);
                pesoEnElevador += caja.getWeight();
                cajasFaltantes.remove(caja);
            }
        }
    }

    static void cargarElevador(){
        elevador.recordContin(BUSSY, simTime.getTime());
        for (Box caja : cajasATransportar){
            switch (caja.getBoxType()){
                case 'B':
                    esperaB.recordDiscrete(simTime.getTime() - caja.getArriveTime());
                    break;
                case 'C':
                    totalC ++;
                    break;
            }
        }
    }

    static float distUniforme(int max, int min){
        return min + random.nextFloat()*(max-min);
    }

    static float distC(){
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

    static float disTriangular(double a, double b, double c){
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

    static float distExponencial(double lambda){
        return (float)(-1/lambda*Math.log(random.nextFloat()));
    }
}
