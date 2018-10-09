package simlib;

import simlib.elements.Event;
import simlib.elements.Element;
import simlib.collection.Collection;
import simlib.exception.*;
import simlib.io.SimWriter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.HashSet;

public class SimLib {
    /************************************************************
     *      EVENTS, ELEMENTS AND TIME MANAGEMENT COMPONENT      *
     ************************************************************/
    public static float simTime;
    public static int eventType;
    public static float[] eventAttributes;

    private static PriorityQueue<Event> events;

    public static void initSimlib() {
        events = new PriorityQueue<Event>();
        simTime = eventType = 0;
        eventAttributes = new float[0];
    }

        public static void timing(){
        try{
            simTime = events.element().getTime();
            eventType = events.element().getType();
            eventAttributes = events.element().getAttributes();
            events.remove();	
        } catch (Exception e){	
            System.exit(1);	
        }	
    }

    /**
     * Creates and add a new event to the events list
     *
     * @param type       type of the event
     * @param time       time of occurrence
     * @param attributes attributes of the event
     */
    public static void eventSchedule(double time, byte type, float... attributes) {
        if( time < 0 ){
            throw new EventTimeException();
        } else if( time < simTime ){
            throw new PasstEventException();
        }
        events.offer(new Event(type, time, attributes));
    }

    /**
     * Remove all events in a given time
     *
     * @param time the time to remove events
     * @return if at least one event was removed
     */
    public static boolean removeEvent( float time ){
        HashSet<Event> removed = new HashSet<>();
        while( !events.isEmpty() && events.element().getTime() >= time ){
            Event  event = events.remove();
            if( event.getTime() != time )
                removed.add( event );
        }
        events.addAll( removed );
        return !removed.isEmpty();
    }


    /************************************************************
     *             RANDOM NUMBER GENERATOR COMPONENT            *
     ************************************************************/
    private static final int MULT1 = 24112;
    private static final int MULT2 = 26143;
    private static final int B2E15 = 32768; // 2 to 15th power
    private static final int B2E16 = 65536; // 2 to 16th power
    private static final int MODLUS = 2147483647;  // largest integer supported
    private static long zrng[] = {0,
            1973272912,  281629770,   20006270, 1280689831, 2096730329,
            1933576050,  913566091,  246780520, 1363774876,  604901985,
            1511192140, 1259851944,  824064364,  150493284,  242708531,
            75253171, 1964472944, 1202299975,  233217322, 1911216000,
            726370533,  403498145,  993232223, 1103205531,  762430696,
            1922803170, 1385516923,   76271663,  413682397,  726466604,
            336157058, 1432650381, 1120463904,  595778810,  877722890,
            1046574445,   68911991, 2088367019,  748545416,  622401386,
            2122378830,  640690903, 1774806513, 2132545692, 2079249579,
            78130110,  852776735, 1187867272, 1351423507, 1645973084,
            1997049139,  922510944, 2045512870,  898585771,  243649545,
            1004818771,  773686062,  403188473,  372279877, 1901633463,
            498067494, 2087759558,  493157915,  597104727, 1530940798,
            1814496276,  536444882, 1663153658,  855503735,   67784357,
            1432404475,  619691088,  119025595,  880802310,  176192644,
            1116780070,  277854671, 1366580350, 1142483975, 2026948561,
            1053920743,  786262391, 1792203830, 1494667770, 1923011392,
            1433700034, 1244184613, 1147297105,  539712780, 1545929719,
            190641742, 1645390429,  264907697,  620389253, 1502074852,
            927711160,  364849192, 2049576050,  638580085,  547070247
    };

    public static float rand(int stream) {
        long zi, lowprd, hi31;

        zi = zrng[stream];
        lowprd = (zi & 65535) * MULT1;
        hi31 = (zi >> 16) * MULT1 + (lowprd >> 16);
        zi = ((lowprd & 65535) - MODLUS) +
                ((hi31 & 32767) << 16) + (hi31 >> 15);
        if (zi < 0) zi += MODLUS;
        lowprd = (zi & 65535) * MULT2;
        hi31 = (zi >> 16) * MULT2 + (lowprd >> 16);
        zi = ((lowprd & 65535) - MODLUS) +
                ((hi31 & 32767) << 16) + (hi31 >> 15);
        if (zi < 0) zi += MODLUS;
        zrng[stream] = zi;
        return ((zi >> 7 | 1) + 1)/(float)16777216.0;
    }

    public static void randst(long zset, int stream) {
        zrng[stream] = zset;
    }

    public static long randgt(int stream) {
        return zrng[stream];
    }

    public static float expon(float rmean, int istrm) {
        float u;
        u = rand(istrm);
        return rmean*(float)Math.log(u)*(-1);
    }

    public static int irandi(int nvalue, float probd[], int istrm) {
        int randInt = nvalue;
        float u = rand(istrm);
        for (int i = nvalue; i > 0; i--) {
            if (u < probd[i]) {
                randInt = i;
            }
        }
        return randInt;  // This is an indication of an error
    }

    public static float unifrm(float a, float b, int istrm) {
        float u;
        u = rand(istrm);
        return a + u*(b-a);
    }

    public static double Normal(double mi, double sigma, int istrm)
    {
        int i;
        double SUM = 0.0;
        for (i=0; i<12; i++)  SUM += rand(istrm);
        return (SUM-6.0)*sigma + mi;
    }


    public static float erlang(int m, float mean, int stream) {
        float mean_exponential, sum;

        mean_exponential = mean/m;
        sum = (float)0.0;
        for (int i = 1; i <= m; i++)
            sum += expon(mean_exponential, stream);
        return sum;
    }

    public static double triag(double mod, double min, double max, int stream)
    {
        double RN,BMA,CMA,TR;

        RN = rand(stream);
        BMA=mod-min;
        CMA=max-min;
        if  (RN<BMA/CMA)
            TR= min + Math.sqrt(BMA*CMA*RN);
        else
            TR=max-Math.sqrt(CMA*(1.0-RN)*(max-mod));
        return (TR);
    }


    public static int randomInteger(float probDistrib[], int stream) {
        float u = rand(stream);
        int retrn = 1;
        for (int i = 1; u >= probDistrib[i]; i++)
            retrn++;
        return retrn;
    }
}
