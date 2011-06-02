package chandratoueg;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;

/**
 *
 * @author mku
 */
public class Failure {
     private int                    N;                // Total number of agents
     private Random                 prg;
     private ConcurrentLinkedQueue  crashed;          // Agents that failed
     private ConcurrentLinkedQueue  completed;        // Agents that completed
     private ConcurrentLinkedQueue  trustedImortals;  // Trusted immortal agents

     Log     log;
     Clock   globalClock;

     
     private long waTime;       // Weak accuracy time
     private long scTime;       // Strong completeness time

     
     private final Object lock_CRASHED;
     
      public Failure(int n, Log log) {
           this.N          = n; 
           prg             = new Random();
           crashed         = new ConcurrentLinkedQueue();
           completed            = new ConcurrentLinkedQueue();
           trustedImortals = new ConcurrentLinkedQueue();
           waTime          = Integer.MAX_VALUE;
           scTime          = Integer.MAX_VALUE; // 2000;
           globalClock     = new Clock();
           this.log        = log;
           
           lock_CRASHED    = new Object();

      }

      public boolean amIalive(int whoAmI) {
        synchronized (lock_CRASHED) {    
           if (crashed.contains(whoAmI)) return false;
        
               if (!trustedImortals.contains(whoAmI)) {
                if (prg.nextBoolean() && globalClock.getTime() > waTime) { 
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

      public void IamDone(int whoAmI) {
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
          // Do not suspect yourself
          if (whoAmI == whoToSuspect) return false;

          // Weak accuracy property
          if (trustedImortals.contains(whoToSuspect)) return false;

          // Strong completeness property
          if (globalClock.getTime() >= scTime && crashed.contains(whoToSuspect)) return true;

          // Non-deterministic behavior elsewise...
          return prg.nextBoolean();
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
