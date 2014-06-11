package philosophers;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.out;

/*
 Data structure used to represent forks- array of reentrant locks

 Invariant - A philosopher requires to have both the left and right forks to eat . This is realized by using lock/unlock 
 mechanism while checking using trylock(), to check if a philosopher has acquired both the locks(i.e, the forks)
 to eat.In case a philosopher has acquired only one lock den he is made to relinquish that lock to prevent 
 a subsequent dealock situation(which might occur if all threads are holding one lock at a time and then
 possibly none of them get to acquire both locks to eat).  
  
 Implementation is data race free due to the use of synchronized blocks and non usage of shared varaibles.
 */
public class Table {

    int numSeats;
    Thread[] phils;
    final Random r = new Random();
    final int timesToEat;
    static final int MAXMSECS = 1000;
    final Logger log;
    boolean checker = true;

    class Philosopher implements Runnable {

        final int seat;  //where this philosopher is seated, seats numbered from 0 to numSeats-1
        int timesToEat;
        private Lock leftfork;
        private Lock rightfork;

        Philosopher(int seat, int timesToEat, Lock leftfork, Lock rightfork) {
            this.seat = seat;
            this.timesToEat = timesToEat;
            this.leftfork = leftfork;
            this.rightfork = rightfork;

        }

        void think() {
            log.thinks(seat);//condition that checks that a philosopher thinks if he has either both the forks or none
            try {
                // if (checker)
                Thread.sleep(r.nextInt(MAXMSECS));

            } catch (InterruptedException e) {
                /*ignore*/
            }
        }

        void eat() {
            log.eats(seat);//condition that a philosopher eats once he has both the forks
            try {

                Thread.sleep(r.nextInt(MAXMSECS));

            } catch (InterruptedException e) {
                /*ignore*/
            }
        }
        /*
         This function allows the philosopher to eat while checking against the timeToEat variable.
         This is done by passing a flag variable(checker) which is only read by the threads to check if the time has elapsed.
         In case a philosopher is eating then he is allowed to complete its task and gracefully die.
         */

        public synchronized void run() {
            while (checker) {
                //condition that checks a philosopher must have both the forks to eat
                if (leftfork.tryLock()) { // philosopher has left fork
                    if (rightfork.tryLock()) { // philosopher has right fork
                        eat();
                        putdown();
                        timesToEat--;
                    } else {//if a philosopher has only one fork then unlock it to avoid deadlock situation.
                        leftfork.unlock();
                    }
                }
                if (checker) {
                    think();
                }
            }
        }

        private void pickLeft() {
            leftfork.lock();//pick up left fork
        }

        private void pickRight() {
            rightfork.lock();//pick up right fork
        }

        private void putdown() {//release both left and right forks
            leftfork.unlock();
            rightfork.unlock();
        }
    }

    Table(int numSeats, int timesToEat, Logger log) throws InterruptedException {
        this.numSeats = numSeats;  //set the number of seats around the table.  Must be at least 2
        this.timesToEat = timesToEat;  //number of times each philosopher should eat
        this.log = log;
        phils = new Thread[numSeats];  //create a Thread for each philosopher
        ReentrantLock[] forks = new ReentrantLock[numSeats];// data-structure used to represent forks

        for (int i = 0; i < numSeats; i++) {
            forks[i] = new ReentrantLock();
        }
        for (int i = 0; i < numSeats; i++) {
            phils[i] = new Thread(new Philosopher(i, timesToEat, forks[i], forks[(i + 1) % numSeats]));
        }
    }

    void startDining() {
        checker = true;
        for (int i = 0; i < numSeats; i++) {
            phils[i].start();
        }
    }

    void closeRestaurant() throws InterruptedException {
        checker = false;
        for (int i = 0; i < numSeats; i++) {
            phils[i].join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            out.println("usage:  java Table numSeats timesToEat");
            return;
        }
        int numPhils = Integer.parseInt(args[0]);
        int timesToEat = Integer.parseInt(args[1]);
        Logger log = new CheckingLogger(numPhils);
        Table table = new Table(numPhils, timesToEat, log);
        table.startDining();
        long sysCurtym = System.currentTimeMillis();
        long sysStrtym = 0L;
        while (sysStrtym <= timesToEat * 1000) {
            sysStrtym = System.currentTimeMillis() - sysCurtym;

        }

        table.closeRestaurant();
        System.out.println("restaurant closed.  Behavior was " + (log.isCorrect() ? "correct" : "incorrect"));
    }
}
