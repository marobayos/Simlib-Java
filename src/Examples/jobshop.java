package Examples;

import static simlib.SimLib.*;

import java.io.IOException;

import simlib.io.*;
import simlib.elements.*;
import simlib.collection.*;


public class jobshop {
    static final byte EVENT_ARRIVAL = 1, EVENT_DEPARTURE = 2, EVENT_END_SIMULATION = 3;
    static final byte STREAM_INTERARRIVAL = 1,STREAM_JOB_TYPE = 2, STREAM_SERVICE = 3;

    static int   numStations, numJobTypes, numTasks[], route[][],
            jobType, task;
    static float meanInterarrival, lengthSimulation, probDistribJobType[], meanService[][];

    static DiscreteStat[] delays;
    static Queue<Job>[] queues;
    static Store[] stations;
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

        delays = new DiscreteStat[ numStations ];
        stations = new Store[ numStations ];
        numTasks = new int[ numJobTypes ];

        for ( int i = 0; i < numStations; i++ ) {
            stations[i] = new Store("Station "+i, reader.readInt() );
        }
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
            writer.write(stations[j].getCapacity()+" ");
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

        /*  If this is a new arrival to the system, generate the time of the next
            arrival and determine the job type and task number of the arriving
            job. */
        if (newJob == 1) {
            eventSchedule(simTime + expon(meanInterarrival, STREAM_INTERARRIVAL),
                    EVENT_ARRIVAL);
            jobType = randomInteger(probDistribJobType, STREAM_JOB_TYPE);
            task     = 1;
        }

        /* Determine the station from the route matrix. */
        station = route[jobType][task];

        if (stations[station].isFull()) {

            /*All machines in this station are busy, so place the arriving job at
                the end of the appropriate queue. Note that the following data are
                stored in the record for each job:
                1. Time of arrival to this station.
                2. Job type.
                3. Current task number. */
            queues[station].offer(new Job(simTime, jobType, task));
        } else {
             /* A machine in this station is idle, so start service on the arriving
                job (which has a delay of zero). */
             delays[station].record(0);
             stations[station].enter();

             /* Schedule a service completion.  Note defining attributes beyond the
                first two for the event record before invoking event_schedule. */
             eventSchedule(simTime + erlang( 2, meanService[jobType][task], STREAM_SERVICE ), EVENT_DEPARTURE, jobType, task);
        }
    }


    static void depart()  /* Event function for departure of a job from a particular
                      station. */
    {
        int station, jobTypeQueue, task_queue;
        jobType  = (int)eventAttributes[1];
        task     = (int)eventAttributes[2];
        station  = route[jobType][task];

        if (queues[station].isEmpty()) {

        /* The queue for this station is empty, so make a machine in this
           station idle. */
            stations[station].leave();
        } else {
            delays[station].record(simTime-queues[station].peek().arriveTime);

            eventSchedule(simTime + erlang(2, meanService[jobType][task], STREAM_SERVICE),
                    EVENT_DEPARTURE, jobType, task);
        }

        /* If the current departing job has one or more tasks yet to be done, send
       the job to the next station on its route. */
        if (task < numTasks[jobType]) {
            ++task;
            arrive(2);
        }
    }


    static void report() throws IOException  /* Report generator function. */
    {
        float overall_avg_job_tot_delay, avg_job_tot_delay, sum_probs;

    /* Compute the average total delay in queue for each job type and the
       overall average job total delay. */

        writer.write("\n\n\n\nJob type     Average total delay in queue");
        overall_avg_job_tot_delay = sum_probs= (float)0.0;
        for (int i = 0; i < numJobTypes; ++i) {
            avg_job_tot_delay = delays[i].getAverage() * numTasks[i];
            writer.write("\n\n"+i+" "+avg_job_tot_delay+" ");
            overall_avg_job_tot_delay += (probDistribJobType[i] - sum_probs) * avg_job_tot_delay;
            sum_probs = probDistribJobType[i];
        }
        writer.write("\n\nOverall average job total delay ="+overall_avg_job_tot_delay+"\n");

    /* Compute the average number in queue, the average utilization, and the
       average delay in queue for each station. */

        writer.write("\n\n\n Work      Average number      Average       Average delay");
        writer.write("\nstation       in queue       utilization        in queue");
        for (int j = 1; j <= numStations; ++j)
            writer.write("\n\n"+j+" "+stations[j].getAverage()+" "+ delays[j].getAverage());
    }

    static class Job {
        float arriveTime;
        int type, task;

        public Job(float arriveTime, int type, int task) {
            this.arriveTime = simTime;
        }
    }

}
