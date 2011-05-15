/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chandratoueg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author mku
 */
public class Failure {
     private Random             prg;
     private ArrayList          crashed; // List of agent id's that have failed
     private ArrayList          done;   // List of agent id's that have
                                        // completed a trace
     private ArrayList          trustedImortals;
     private int                N;

     Clock   globalClock;

     private long waTime;       // Weak accuracy time
     private long scTime;       // Strong completeness time

      public Failure(int n) {
           prg             = new Random();
           crashed         = new ArrayList();
           done            = new ArrayList();
           trustedImortals = new ArrayList();
           N               = n; // Total number of agents
           waTime          = 2000;
           scTime          = 0;
           globalClock     = new Clock();
      }

      public synchronized boolean amIalive(int whoAmI) {
       if (crashed.contains(whoAmI)) return false;
       if (!trustedImortals.contains(whoAmI) && 
           prg.get_random_float() < 0.9 &&
           crashed.size() < N/2) {
             crashed.add(whoAmI);
             return false;
       }

       if (prg.get_random_float() < 0.1 &&
           globalClock.getTime() > waTime &&
           !trustedImortals.contains(whoAmI)) {
           trustedImortals.add(whoAmI);
           return true;
       }

        return true;
       }

      public synchronized void IamDone(int whoAmI) {
          if (!done.contains(whoAmI) &&
              !crashed.contains(whoAmI)) done.add(whoAmI);
      }

      public synchronized boolean amIdone(int whoAmI) {
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
          return prg.get_random_bit();
      }

      public synchronized boolean fd_P(int p) {
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
    Comparator comparator = Collections.reverseOrder();
         Collections.sort(crashed,comparator);
         Collections.sort(trustedImortals,comparator);
         Collections.sort(done,comparator);
        return "Crashed: " + crashed.toString() + "\n" +
               "TI's:    " + trustedImortals.toString() + "\n" +
               "Done:    " + done.toString();        
      }


}
