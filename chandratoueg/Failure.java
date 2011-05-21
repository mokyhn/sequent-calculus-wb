package chandratoueg;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;

/**
 *
 * @author mku
 */
public class Failure {
    private  Network net;
     private Random             prg;
     private ConcurrentLinkedQueue  crashed; // List of agent id's that have failed
     private ConcurrentLinkedQueue  done;   // List of agent id's that have
                                        // completed a trace
     private ConcurrentLinkedQueue  trustedImortals;
     private int                N;

     Clock   globalClock;

     private long waTime;       // Weak accuracy time
     private long scTime;       // Strong completeness time

     
     private Object lock_TI;
     private Object lock_CRASHED;
     
      public Failure(int n, Network net) {
           prg             = new Random();
           crashed         = new ConcurrentLinkedQueue();
           done            = new ConcurrentLinkedQueue();
           trustedImortals = new ConcurrentLinkedQueue();
           N               = n; // Total number of agents
           waTime          = 100;
           scTime          = 2000;
           globalClock     = new Clock();
           this.net        = net;
           
           lock_TI         = new Object();
           lock_CRASHED    = new Object();

      }

      public boolean amIalive(int whoAmI) {
        synchronized (lock_CRASHED) {    
           if (crashed.contains(whoAmI)) return false;
        
            synchronized (lock_TI) {
               if (!trustedImortals.contains(whoAmI)) {
                if (prg.nextBoolean() && globalClock.getTime() > waTime) { 
                 trustedImortals.add(whoAmI);
                 return true;
                }
               
               if (!trustedImortals.contains(whoAmI) && 
                   prg.nextBoolean() &&
                   crashed.size() < N/2) {
                   crashed.add(whoAmI);
                   //net.delete(whoAmI);  // Remove net messages
                   return false;
                   }
               }
            }
        }
       return true;
      }

      public void IamDone(int whoAmI) {
          if (!done.contains(whoAmI) &&
              !crashed.contains(whoAmI)) done.add(whoAmI);
      }

      public boolean amIdone(int whoAmI) {
       return done.contains(whoAmI);
      }
      
      // Agent who
      public boolean fd_DS(int whoAmI, int whoToSuspect) {
          // Do not suspect yourself
          if (whoAmI == whoToSuspect) return false;

          // Weak accuracy property
          if (trustedImortals.contains(whoToSuspect)) return false;

          // Strong completeness property
          if (globalClock.getTime() >= scTime && crashed.contains(whoToSuspect)) return true;

          // Non-deterministic behavior elsewise...
          return prg.nextBoolean();
      }

      public  boolean fd_P(int p) {
          synchronized (lock_CRASHED) {
           return crashed.contains(p);
          }
      }
      
      
    @Override
      public String toString() {
        
        return "Crashed: " + crashed.toString() + "\n" +
               "TI's:    " + trustedImortals.toString() + "\n" +
               "Done:    " + done.toString();        
      }


}
