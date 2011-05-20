/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
      }

      public boolean amIalive(int whoAmI) {
       if (crashed.contains(whoAmI)) return false;

       if (prg.nextBoolean() &&
           globalClock.getTime() > waTime &&
           !trustedImortals.contains(whoAmI)) {
           trustedImortals.add(whoAmI);
           return true;
       }

       
       if (!trustedImortals.contains(whoAmI) && 
           prg.nextBoolean() &&
           crashed.size() < N/2) {
             crashed.add(whoAmI);
             net.delete(whoAmI);  // Remove net messages
             return false;
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
       return crashed.contains(p);
      }
      
      
    @Override
      public String toString() {
        
        /*String res = "";
        int i;

        for (i=0; i < N; i++) {
          res = res + "\n";

          if (crashed.contains(i)) {res = res + "(c " + i + ")";}
          if (trustedImortals.contains(i)) {
              res = res + "(ti " + i + ")";
          }
          if (done.contains(i))   res = res + "(d " + i + ")";
         
        }
        */
    /*Comparator comparator = Collections.reverseOrder();
         Collections.sort(crashed.,comparator);
         Collections.sort(trustedImortals.toArray(),comparator);
         Collections.sort(done,comparator);*/
        return "Crashed: " + crashed.toString() + "\n" +
               "TI's:    " + trustedImortals.toString() + "\n" +
               "Done:    " + done.toString();        
      }


}
