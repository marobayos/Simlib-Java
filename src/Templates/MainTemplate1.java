package Templates;

import simlib.collection.*;
import simlib.elements.*;
import simlib.io.*;

import java.io.IOException;

public class MainTemplate1 {

    static final byte EVENTO_1 = 0, EVENTO_2 = 1, EVENTO_3 = 2, FIN_SIM = 3;

    static Timer timer;             // Reloj de la simulación
    static EventsList events;   // Lista de eventos
    static Event nowEvent;          // Evento que está en curso

    // Variables de estado
    static DiscreteStat stat1;
    static ContinStat stat2;

    public static void main(String[] args) throws Exception {
        /* ABRIR ARCHIVOS */
        SimReader input = new SimReader("archivoDeEntrada.txt");
        SimWriter out = new SimWriter("archivoDeSalida.txt");

        /* LEER Y GUARDAR PARÁMETROS */


        /* INICIALIZAR */
        inicializar();

        /* DESARROLLO DE LA SIMULACION */
        do {
            sincronizar();
            switch ( nowEvent.getType() ) {
                case EVENTO_1:
                    evento1();
                    break;
                case EVENTO_2:
                    evento2();
                    break;
                case EVENTO_3:
                    evento3();
                    break;
                case FIN_SIM:
                    break;
            }
        } while ( events.size()>0 );

        /* GENERAR REPORTE */
        finSim(out);

        /* CERRAR ARCHIVOS */
        input.close();
        out.close();
    }

    static void inicializar(){
        // Inicializa el reloj de la simulacion
        timer = new Timer();

        // Inicializa la cola de eventos de la simulacion
        events = new EventsList( timer );

        // Añade el primer evento a la cola de eventos
        events.add(new Event( EVENTO_1, 0.0001 ) );

        stat1 = new DiscreteStat("Variable discreta");       // Ambos tipos de variable se pueden inicializar con o sin primer valor

        stat2 = new ContinStat("Variable continua", timer);
    }

    static void sincronizar(){
        // Actualiza el evento que se va a procesar.
        nowEvent = events.getFirst();

        // Actualiza el reloj de la simulacion.
        timer.setTime(events.getFirst().getTime());

        // Elimina el evento que se va a procesar de la cola de eventos.
        events.removeFirst();
    }

    static void evento1(){
        //Proceso del evento 1
    }

    static void evento2(){
        //Proceso del evento 2
    }

    static void evento3(){
        //Proceso del evento 3
    }

    static void finSim(SimWriter out) throws IOException {
        stat1.report(out);
        stat2.report(out);
    }

}
