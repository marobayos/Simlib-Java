package Examples;

import static simlib.SimLib.*;

import java.io.IOException;

import simlib.io.*;
import simlib.elements.*;
import simlib.collection.*;


public class jobshop {
    static final byte EVENT_ARRIVAL = 1, EVENT_DEPARTURE = 2, EVENT_END_SIMULATION = 3;
    static final byte STREAM_INTERARRIVAL = 1,STREAM_JOB_TYPE = 2, STREAM_SERVICE = 3;

    static int   numStations, numJobTypes, numMachines[], numTasks[], route[][], numMachinesBusy[],
            jobType, task;
    static float meanInterarrival, lengthSimulation, probDistribJobType[], meanService[][];

    static Queue<Job>[] queues;
    static SimReader reader;
    static SimWriter writer;

    public static void main(String[] args) throws IOException {
        /* Open input and output files. */

        reader = new SimReader("jobshop2.in");
        writer = new SimWriter("jobshop2.out");

        /* Read input parameters. */
        numStations = reader.readInt();
        numJobTypes = reader.readInt();
        meanInterarrival = reader.readFloat();
        lengthSimulation = reader.readFloat();

        numMachines = new int[ numStations ];
        numTasks = new int[ numJobTypes ];

        for ( int i = 0; i < numStations; i++ )
            numMachines[i] = reader.readInt();
        for (int i = 0; i < numJobTypes; i++)
            numTasks[i] =  reader.readInt();
        for (int i = 0; i < numJobTypes ; i++) {
            for (int j = 0; j < numTasks[i] ; j++)
                route[i][j] = reader.readInt();
            for (int j = 0; j < numTasks[i] ; j++)
                meanService[i][j] = reader.readFloat();
        }
        for (int i = 0; i < numJobTypes; i++)
            probDistribJobType[i] = reader.readFloat();

        /* Write report heading and input parameters. */

        writer.write("Job-shop model\n\n");
        writer.write("Number of work stations "+numStations+"\n\n");
        writer.write("Number of machines in each station     ");
        for (int j = 1; j <= numStations; ++j)
            writer.write(numMachines[j]+" ");
        writer.write("\n\nNumber of job types "+ numJobTypes +"\n\n");
        writer.write("Number of tasks for each job type      ");
        for (int i= 1; i <= numJobTypes; ++i)
            writer.write(numTasks[i]+" ");
        writer.write("\n\nDistribution function of job types  ");
        for (int i= 1; i <= numJobTypes; ++i)
            writer.write(probDistribJobType[i]+" ");
        writer.write("\n\nMean interarrival time of jobs "+meanInterarrival+" hours\n\n");
        writer.write("Length of the simulation"+lengthSimulation+" eight-hour days\n\n\n");
        writer.write("Job type     Work stations on route");
        for (int i= 1; i <= numJobTypes; ++i) {
            writer.write("\n\n"+i+"        ");
            for (int j = 1; j <= numTasks[i]; ++j)
                writer.write(route[i][j]+" ");
        }
        writer.write("\n\n\nJob type     ");
        writer.write("Mean service time (in hours) for successive tasks");
        for (int i= 1; i <= numJobTypes; ++i) {
            writer.write("\n\n"+i+"    ");
            for (int j = 1; j <= numTasks[i]; ++j)
                writer.write(meanService[i][j]+" ");
        }

        /* Initialize all machines in all stations to the idle state. */
        for (int j = 1; j <= numStations; ++j)
            numMachinesBusy[j] = 0;

        /* Initialize simlib */
        initSimlib();

        /* Schedule the arrival of the first job. */
        eventSchedule(expon(meanInterarrival, STREAM_INTERARRIVAL), EVENT_ARRIVAL);

        /* Schedule the end of the simulation.  (This is needed for consistency of
           units.) */
        eventSchedule(8 * lengthSimulation, EVENT_END_SIMULATION);

        /* Run the simulation until it terminates after an end-simulation event
           (type EVENT_END_SIMULATION) occurs. */
        do {

            /* Determine the next event. */
            timing();

            /* Invoke the appropriate event function. */
            switch (eventType) {
                case EVENT_ARRIVAL:
                    arrive(1);
                    break;
                case EVENT_DEPARTURE:
                    depart();
                    break;
                case EVENT_END_SIMULATION:
                    report();
                    break;
            }

        /*  If the event just executed was not the end-simulation event (type
            EVENT_END_SIMULATION), continue simulating.  Otherwise, end the
            simulation. */
        } while (eventType != EVENT_END_SIMULATION);

        reader.close();
        writer.close();
    }

    /* Function to serve as both an arrival event of a job to the system, as well
       as the non-event of a job's arriving to a subsequent station along its
       route. */
    static void arrive( int newJob ){
        int station;
    }


    static void depart()  /* Event function for departure of a job from a particular
                      station. */
    {
    }


    static void report() throws IOException  /* Report generator function. */
    {
    }

    static class Job {
        float arriveTime;
        int type, task;

        public Job(float arriveTime, int type, int task) {
            this.arriveTime = simTime;
        }
    }

}
