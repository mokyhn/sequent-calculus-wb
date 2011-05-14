/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chandratoueg;

/**
 *
 * @author mku
 */
public class Agent extends Thread {
  Network net;
  Failure failure;
  Clock   globalClock;
  Clock   localClock;

  // Chandra and Toueg variables
  int     p;        // Id of this agent
  int     N;        // Total number of agents
  int    estimate;  // p's current estimate
  String state;     // State variable
  int    r;         // Current round number
  int    ts;        // The last round estimate was updated

  // Update global and local time
  private void tick() {
     globalClock.tick();
     localClock.tick();
  }

  public Agent(int i, Network net, Failure f, Clock c, int N) {
      this.p           = i;
      this.net         = net;
      this.failure     = f;
      this.globalClock = c;
      this.localClock  = new Clock();
      this.N           = N;

      // Initialization of algorithm variables
      estimate      = p;      // The suggested value is the id of the process
      state         = "undecided"; //
      r             = 0;           // We start in round 0
      ts            = 0;           // The estimate was last updated in round 0
  }
 
  private class Algorithm extends Thread {
    public void algo() {
      for (int j = 0; j < 200; j++) {
          tick();
          if  (failure.amIalive(p, globalClock.getTime())) {
           System.out.println("Hello from a thread " + p);
           
         }
         else {
             System.out.println("Thread " + p + " crashed");
             break;
         }
         
        }
        
     failure.Isucceded(p);
    }
    @Override
    public void run() { algo();  }
  }


  private class ChandraToueg extends Thread {
       int kl1;
  
      public void ChandraToueg() {
        int     c; // coordinator id

        while (state.equals("undecided")) {
         r = r + 1;
         c = (r % N) + 1;

         // Phase 1
         net.snd(new Message(p, c, "phase1", r,  estimate, ts));


         // Phase 2
         if (p == c) {
         }

      }
     }
    }

  private class RBlistener extends Thread {
     public void listen() {
      int  kl2;

      for (kl2 = 0; kl2 < 10; kl2++)
          System.out.println(p + " RBListener");
     }

     @Override
     public void run() { listen();  }
  }



    @Override
  public void run() {
    Algorithm a = new Algorithm();
    RBlistener rb = new RBlistener();
    
    a.start();
    rb.start();
    }

  
}
