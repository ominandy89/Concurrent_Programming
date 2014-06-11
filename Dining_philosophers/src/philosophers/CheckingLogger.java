/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philosophers;

import static java.lang.System.out;

/**
 * void eats()- set ar[] to true for that thread.Checks if neighboring
 * philosophers are trying to eat at the same time.In that case we set the
 * checker variable to false for that thread .Subsequently this checker is used
 * to check for correctness of implementation void thinks()-if a philosopher is
 * thinking then set ar[] to false for that thread; 
 * boolean isCorrect()- checks the checker flag to determine if the implementation was correct or not.
 */
public class CheckingLogger implements Logger {

    boolean checker = true;
    boolean ar[];//boolean array which is set to true if a thread is eating; used later to check if neighbouring phiosophers are not eating
    int seat;

    CheckingLogger(int philosopher) {
        seat = philosopher;
        ar = new boolean[philosopher];
    }

    //functions are data race free due to the use of synchoronized methods
    public synchronized void eats(int philosopher) {
        ar[philosopher] = true;//set the specific philosopher to true as it has both the forks and hence eating
        //pre-conditions to check neighbouring philosophers don't eat together
        if (ar[Math.abs((philosopher + 1) % seat)] == true || ar[Math.abs((philosopher - 1) % seat)] == true) {
            checker = false;//if two neighbouring philosophers are eating together then its an incorrect implementation
            out.println("Philosopher " + philosopher + " is eating is Incorrect");
        } else {
            out.println("Philosopher " + philosopher + " is eating");
        }
    }

    @Override
    public synchronized void thinks(int philosopher) {
        out.println("Philosopher " + philosopher + " is thinking");
        ar[philosopher] = false;//the specific philosopher is set to false that ensures he is not eating 
    }

    @Override
    public boolean isCorrect() {
        // if any pair of neighbours are eating together then output as a wrong implementation
        if (checker == true) {
            out.println("correct");
            return true;
        } else {
            out.println("incorrect");
            return false;
        }
    }

}
