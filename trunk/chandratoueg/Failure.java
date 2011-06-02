package chandratoueg;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;

/**
 *
 * @author mku
 */
public class Failure {
     private int                    N;                // Total number of agents
     private Clock                  clock;           // Timing
     private long                   waTime;          // Weak accuracy time
     private long                   scTime;          // Strong completeness time

     private ConcurrentLinkedQueue  crashed;          // Agents that failed
     private ConcurrentLinkedQueue  completed;        // Agents that completed
     private ConcurrentLinkedQueue  trustedImortals;  // Trusted immortal agents
     
     private Random                 prg;

     private Log                    log;              // For log messages
     
     private final Object           lock_CRASHED;
     
     /**
      * 
      * @param N Number of agents
      * @param clock The global clock
      * @param waTime Weak Accuracy time - time after which the unreliable failure detector fd_DS obtains weak accuracy.
      * @param scTime Strong Completeness time - time after which the unreliable failure detector fd_DS obtains the strong completeness property.
      * @param log Object for logging of log messages
      */
      public Failure(int N, Clock clock, long waTime, long scTime, Log log) {
           this.N           = N; 
           this.clock       = clock;
           this.waTime      = waTime;
           this.scTime      = scTime;
           this.log         = log;

           prg             = new Random();
           crashed         = new ConcurrentLinkedQueue();
           completed       = new ConcurrentLinkedQueue();
           trustedImortals = new ConcurrentLinkedQueue();
           waTime          = Integer.MAX_VALUE;
           scTime          = Integer.MAX_VALUE; 
           
           lock_CRASHED    = new Object();

      }

      public boolean amIalive(int whoAmI) {
        synchronized (lock_CRASHED) {    
           if (crashed.contains(whoAmI)) return false;
        
               if (!trustedImortals.contains(whoAmI)) {
                if (prg.nextBoolean() && clock.getTime() > waTime) { 
                 trustedImortals.add(whoAmI);
                 log.add("Agent " + whoAmI + " is now TI...");
                 return true;
                }
               
               if (!trustedImortals.contains(whoAmI) && 
                   prg.nextBoolean() &&
                   crashed.size() < N/2) {
                   crashed.add(whoAmI);
                   log.add("Agent " + whoAmI + " CRASHED...");
                   return false;
                   }
               }
            
        }
       return true;
      }

      public void ICompleted(int whoAmI) {
          if (!completed.contains(whoAmI) &&
              !crashed.contains(whoAmI)) completed.add(whoAmI);
      }

      public boolean amIdone(int whoAmI) {
       return completed.contains(whoAmI);
      }
      
      /**
       * The unreliable failure detector "diamond S".
       * @param whoAmI Agent id of caller
       * @param whoToSuspect Agent id of agent to suspect
       * @return true when agent whoToSuspect is suspected to have crashed 
       * and false otherwise
       */
      public boolean fd_DS(int whoAmI, int whoToSuspect) {
          if (whoAmI == whoToSuspect) 
              return false;                           // Do not suspect yourself
          if (trustedImortals.contains(whoToSuspect)) // Weak accuracy property
              return false;
          if (clock.getTime() >= scTime && crashed.contains(whoToSuspect)) 
              return true;                       // Strong completeness property
          return prg.nextBoolean();             // Non-deterministic behavior...
      }

      /**
       * A reliable failure detector
       * @param p The agent to suspect
       * @return true, when agent p has crashed and false otherwise
       */
      public  boolean fd_P(int p) {
          synchronized (lock_CRASHED) {
           return crashed.contains(p);
          }
      }
      
      
    @Override
      public String toString() {
        return "Crashed: " + crashed.toString() + "\n" +
               "TI's:    " + trustedImortals.toString() + "\n" +
               "Done:    " + completed.toString();        
      }


}
