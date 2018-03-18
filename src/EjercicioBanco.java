import java.io.*;

public class EjercicioBanco {
    static final byte LLEGADA = 0, FIN_SERVICIO = 1, CIERRE = 2, FIN_SIM = 3;
    static int totalTime, meanL, meanS;
    static byte eventType;

    public static void main(String[] args)throws IOException {
        /* ABRIR ARCHIVOS */
        BufferedReader input = new BufferedReader( new FileReader("Input1.txt") );
        BufferedWriter out = new BufferedWriter(new FileWriter("Output1.txt"));

        /* LEER Y GUARDAR PARÁMETROS */
        int inicio, fin;
        inicio = Integer.parseInt( input.readLine() );
        fin = Integer.parseInt( input.readLine() );
        totalTime = (fin-inicio)*60*60;
        meanL = Integer.parseInt( input.readLine() );
        meanS = Integer.parseInt( input.readLine() );

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

    static void inicializar(){}

    static void sincronizar(){}

    static void llegada(){}

    static void finServicio(){}

    static void cierreBanco(){}

    static void finSim(BufferedWriter out){}
}
