package Examples;

import simlib.collection.*;
import simlib.elements.*;
import simlib.io.*;
import static simlib.SimLib.*;

import java.io.IOException;

public class tscomp {
    static final byte EVENT_ARRIVAL = 1, EVENT_END_CPU_RUN = 2, EVENT_END_SIMULATION = 3;
    static final byte STREAM_THINK = 1, STREAM_SERVICE = 2;

    static int minTerms, maxTerms, incrTerms, numTerms, numResponses, numResponsesRequired, term;
    static float meanThink, meanService, quantum, swap;

    static SimReader reader;
    static SimWriter writer;

    static DiscreteStat responseTime;
    static Resource<Job> CPU;
    static Queue<Job> queueCPU;

    public static void main(String[] args) throws Exception {
        /* Open inputs and outputs files */
        reader = new SimReader("tscomp.in");
        writer = new SimWriter("tscomp.out");

        /* Read input parameters */
        minTerms = reader.readInt();
        maxTerms = reader.readInt();
        incrTerms = reader.readInt();
        numResponsesRequired = reader.readInt();
        meanThink = reader.readFloat();
        meanService = reader.readFloat();
        quantum = reader.readFloat();
        swap = reader.readFloat();

        /* Write report heading and input parameters */
        writer.write("Time-shared computer model\n\n" +
                "Number of terminals " + minTerms + " to " + maxTerms + " by " + incrTerms + "\n" +
                "Mean think time     " + meanThink + "\n" +
                "Mean service time   " + meanService + "\n" +
                "Quantum             " + quantum + "\n" +
                "Number of jobs processed " + numResponsesRequired + "\n\n"
        );

        /* Run the simulation varying the number of terminals. */
        for (numTerms = minTerms; numTerms <= maxTerms; numTerms += incrTerms) {

            writer.write("SIMULATION WITH " + numTerms+"\n");

            /* Initialize simlib */
            initSimlib();

            /* Initialize the statistical counters and simulation elements. */
            numResponses = 0;
            responseTime = new DiscreteStat("Response times");
            CPU = new Resource<Job>("CPU");
            queueCPU = new Queue<>("Jobs queue to CPU" );


            /* Schedule the first arrival to the CPU from each terminal and
            initialize every terminal. */
            for (int term = 0; term < numTerms; term++) {
                eventSchedule(expon(meanThink, STREAM_THINK), EVENT_ARRIVAL);
            }

            /* Run the simulation until it terminates after an end-simulation event
            (type EVENT_END_SIMULATION) occurs. */
            do {

                /* Determine the next event. */
                timing();

                /* Invoke the appropriate event function. */
                switch (eventType) {
                    case EVENT_ARRIVAL:
                        arrive();
                        break;
                    case EVENT_END_CPU_RUN:
                        end_CPU_run();
                        break;
                    case EVENT_END_SIMULATION:
                        report();
                        break;
                }
                /* If the event just executed was not the end-simulation event (type
                   EVENT_END_SIMULATION), continue simulating.  Otherwise, end the
                   simulation. */
            } while (eventType != EVENT_END_SIMULATION);;
        }

        writer.close();
        reader.close();
    }

    /* Event function for arrival of job at CPU after think time. */
    static void arrive() {
        /* Place the arriving job at the end of the CPU queue.*/
        queueCPU.offer(new Job());

        /* If the CPU is idle, start a CPU run. */
        if ( CPU.isIdle() )
            start_CPU_run();
    }

    /* Non-event function to start a CPU run of a job. */
    static void start_CPU_run() {
        /* Remove and save the first job from the queue. */
        Job job = queueCPU.poll();

        /* Determine the CPU time for this pass, including the swap time. */
        float runTime;
        if( job.remainingTime > quantum )
            runTime = quantum + swap;
        else
            runTime = job.remainingTime + swap;

        /* Decrement remaining CPU time by a full quantum.  (If less than a full
           quantum is needed, this attribute becomes negative.  This indicates that
           the job, after exiting the CPU for the current pass, will be done and is
           to be sent back to its terminal.) */
        job.remainingTime -= quantum;

        /* Place the job into the CPU. */
        CPU.emplace(job);

        /* Schedule the end of the CPU run. */
        eventSchedule(simTime + runTime, EVENT_END_CPU_RUN);
    }

    /* Event function to end a CPU run of a job. */
    static void end_CPU_run() {
        /* Remove the job from the CPU. */
        Job job = CPU.remove();

        if( job.remainingTime > 0.0 ){
            /* This job requires more CPU time, so place it at the end of the queue
               and start the first job in the queue. */
            queueCPU.offer( job );
            start_CPU_run();
        } else {
            /* This job is finished, so collect response-time statistics and send it
               back to its terminal, i.e., schedule another arrival from the same
               terminal. */
            responseTime.record( simTime-job.arriveTime);
            eventSchedule(simTime + expon(meanThink, STREAM_THINK), EVENT_ARRIVAL);

            /* Increment the number of completed jobs. */
            ++numResponses;

            /* Check to see whether enough jobs are done. */
            if (numResponses >= numResponsesRequired)

            /* Enough jobs are done, so schedule the end of the simulation
               immediately (forcing it to the head of the event list). */
                eventSchedule(simTime, EVENT_END_SIMULATION);

            else

            /* Not enough jobs are done; if the queue is not empty, start
               another job. */
                if ( queueCPU.size() > 0)
                    start_CPU_run();
        }
    }

    static void report() throws IOException {
        responseTime.report( writer );
        CPU.report( writer );
        queueCPU.report( writer );
    }

    static class Job {
        float remainingTime, arriveTime;

        public Job() {
            this.remainingTime = expon(meanService, STREAM_SERVICE);
            this.arriveTime = simTime;
        }

    }
}